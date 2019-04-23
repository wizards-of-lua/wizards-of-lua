package net.wizardsoflua.testenv;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;
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
import net.wizardsoflua.testenv.junit.WolTestExecutionListener;

public class TestCommand implements Command<CommandSource> {
  private static final String CLASS_ARGUMENT = "class";
  private static final String METHOD_ARGUMENT = "method";

  private static final ExecutorService EXECUTOR =
      Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
          Thread newThread = Executors.defaultThreadFactory().newThread(r);
          newThread.setDaemon(true);
          return newThread;
        }
      });

  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("test")//
            .executes(this)//
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
    String testclassName = StringArgumentType.getString(context, CLASS_ARGUMENT);
    return run(context, request().selectors(selectClass(testclassName)).build());
  }

  public int runMethod(CommandContext<CommandSource> context) throws CommandSyntaxException {
    String className = StringArgumentType.getString(context, CLASS_ARGUMENT);
    String methodName = StringArgumentType.getString(context, METHOD_ARGUMENT);
    return run(context, request().selectors(selectMethod(className, methodName)).build());
  }

  private int run(CommandContext<CommandSource> context, LauncherDiscoveryRequest request) {
    Launcher launcher = LauncherFactory.create();
    TestPlan plan = launcher.discover(request);
    long result = plan.countTestIdentifiers(it -> it.isTest());

    EXECUTOR.submit(() -> {
      CommandSource source = context.getSource();
      WolTestExecutionListener listener = new WolTestExecutionListener(source);
      launcher.execute(plan, listener);
    });
    return Math.toIntExact(result);
  }
}
