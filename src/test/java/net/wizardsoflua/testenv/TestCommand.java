package net.wizardsoflua.testenv;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static java.util.Objects.requireNonNull;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherFactory;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.EntityPlayerMP;
import net.wizardsoflua.extension.InjectionScope;
import net.wizardsoflua.testenv.junit.WolTestExecutionListener;

public class TestCommand implements Command<CommandSource> {
  private static final String CLASS_ARGUMENT = "class";
  private static final String METHOD_ARGUMENT = "method";

  private final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
    @Override
    public Thread newThread(Runnable r) {
      Thread newThread = Executors.defaultThreadFactory().newThread(r);
      newThread.setDaemon(true);
      return newThread;
    }
  });
  private final Set<Runnable> aborters = Collections.newSetFromMap(new WeakHashMap<>());
  private final WolTestMod mod;
  private final InjectionScope serverScope;

  public TestCommand(WolTestMod mod, InjectionScope serverScope) {
    this.mod = requireNonNull(mod, "mod");
    this.serverScope = requireNonNull(serverScope, "serverScope");
  }

  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("test")//
            .executes(this)//
            .then(literal("abort")//
                .executes(this::abort))//
            .then(argument(CLASS_ARGUMENT, string())//
                .executes(this::runClass)//
                .then(argument(METHOD_ARGUMENT, string())//
                    .executes(this::runMethod))));
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    return run(context, request().selectors(selectPackage("")).build());
  }

  public int runClass(CommandContext<CommandSource> context) throws CommandSyntaxException {
    String className = StringArgumentType.getString(context, CLASS_ARGUMENT);
    return run(context, request().selectors(selectClass(className)).build());
  }

  public int runMethod(CommandContext<CommandSource> context) throws CommandSyntaxException {
    String className = StringArgumentType.getString(context, CLASS_ARGUMENT);
    String methodName = StringArgumentType.getString(context, METHOD_ARGUMENT);
    return run(context, request().selectors(selectMethod(className, methodName)).build());
  }

  private int run(CommandContext<CommandSource> context, LauncherDiscoveryRequest request)
      throws CommandSyntaxException {
    CommandSource source = context.getSource();
    EntityPlayerMP player = source.asPlayer();

    Launcher launcher = LauncherFactory.create();
    TestPlan plan = launcher.discover(request);
    int result = (int) plan.countTestIdentifiers(it -> it.isTest());

    executor.submit(() -> {
      try (WolTestenv testenv = new WolTestenv(mod, serverScope, player)) {
        WolTestExecutionListener listener = new WolTestExecutionListener(source);
        Runnable aborter = () -> {
          listener.onAbort();
          testenv.getAbortExtension().abortTestRun();
        };
        aborters.add(aborter);
        try {
          launcher.execute(plan, listener);
        } finally {
          deregisterAborter(aborter);
        }
      }
    });
    return result;
  }

  private void deregisterAborter(Runnable aborter) {
    aborters.remove(aborter);
  }

  private int abort(CommandContext<CommandSource> context) {
    int result = 0;
    for (Runnable aborter : aborters) {
      aborter.run();
      result++;
    }
    return result;
  }
}
