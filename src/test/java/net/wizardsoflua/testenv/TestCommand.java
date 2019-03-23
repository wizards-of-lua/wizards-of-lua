package net.wizardsoflua.testenv;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.junit.runner.notification.Failure;
import org.junit.runners.model.InitializationError;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.wizardsoflua.testenv.junit.TestResults;

public class TestCommand implements Command<CommandSource> {

  public static void register(CommandDispatcher<CommandSource> dispatcher) {
    new TestCommand()._register(dispatcher);
  }

  private static final String TRIANGLE = "\u25B6";
  private static final String NO_BREAK_SPACE = "\u00A0";

  public TestCommand() {}

  private void _register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("test")//
            .executes(this)//
            .then(argument("testclass", string())//
                .executes(this::runClass)//
                .then(argument("method", string())//
                    .executes(this::runMethod))));
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    CommandSource source = context.getSource();
    return run(source, null, null);
  }

  public int runClass(CommandContext<CommandSource> context) throws CommandSyntaxException {
    CommandSource source = context.getSource();
    String testclassName = StringArgumentType.getString(context, "testclass");
    return run(source, testclassName, null);
  }

  public int runMethod(CommandContext<CommandSource> context) throws CommandSyntaxException {
    CommandSource source = context.getSource();
    String testclassName = StringArgumentType.getString(context, "testclass");
    String methodName = StringArgumentType.getString(context, "method");
    return run(source, testclassName, methodName);
  }

  private int run(CommandSource source, @Nullable String testclassName,
      @Nullable String methodName) {
    // TODO reuse threads
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          if (testclassName != null) {
            Class<?> testClass = loadTestClass(testclassName);
            if (methodName != null) {
              sendFeedback(source, new TestEnvMessage("Running test"));
              TestResults result = WolTestEnvironment.instance.runTestMethod(testClass, methodName);
              sendFeedback(source, toTestEnvMessage(result));
            } else {
              sendFeedback(source, new TestEnvMessage("Running tests"));
              TestResults result = WolTestEnvironment.instance.runTests(testClass);
              sendFeedback(source, toTestEnvMessage(result));
            }
          } else {
            sendFeedback(source, new TestEnvMessage("Running all test"));
            Iterable<TestResults> result = WolTestEnvironment.instance.runAllTests();
            sendFeedback(source, toTestEnvMessage(result));
          }
        } catch (InitializationError | ClassNotFoundException e) {
          sendFeedback(source, new TextComponentString(e.getMessage()));
        }
      }
    }, "test-command-thread");
    t.start();
    return Command.SINGLE_SUCCESS;
  }

  private Class<?> loadTestClass(String name) throws ClassNotFoundException {
    return Thread.currentThread().getContextClassLoader().loadClass(name);
  }

  private void sendFeedback(CommandSource source, ITextComponent message) {
    if (source.getEntity() instanceof EntityPlayerMP) {
      // Send the message to the player with the given name.
      // This ensures that the player gets the message even when he or she has logged out during the
      // test execution
      String playerName = ((EntityPlayerMP) source.getEntity()).getGameProfile().getName();
      EntityPlayerMP player = source.getServer().getPlayerList().getPlayerByUsername(playerName);
      if (player != null) {
        player.sendMessage(message);
      }
    } else {
      source.getServer().sendMessage(message);
    }
  }

  private ITextComponent toTestEnvMessage(TestResults result) {
    return toTestEnvMessage(Arrays.asList(result));
  }

  private ITextComponent toTestEnvMessage(Iterable<TestResults> results) {
    List<ITextComponent> details = new ArrayList<>();
    int testCount = 0;
    int failureCount = 0;
    for (TestResults r : results) {
      testCount += r.getTestsFinished();
      if (!r.isOK()) {
        Iterable<Failure> f = r.getFailures();
        for (Failure failure : f) {
          failureCount++;
          if (details.size() > 0) {
            details.add(new TextComponentString("\n"));
          }
          ITextComponent header =
              new TextComponentString(TRIANGLE + NO_BREAK_SPACE + failure.getTestHeader() + ":\n");
          header.setStyle(new Style().setColor(TextFormatting.DARK_AQUA));
          details.add(header);
          String message = failure.getMessage();
          if (message == null) {
            message = "<null>";
          }
          ITextComponent failureMessage = new TextComponentString(message);
          failureMessage.setStyle(new Style().setColor(TextFormatting.RED));
          details.add(failureMessage);
        }
      }
    }
    ITextComponent result;
    if (failureCount > 0) {
      result = new TestEnvMessage(failureCount + " of " + testCount + " tests");
      ITextComponent status = new TextComponentString(" failed\n");
      status.setStyle(new Style().setColor(TextFormatting.RED));
      result.appendSibling(status);
      for (ITextComponent iTextComponent : details) {
        result.appendSibling(iTextComponent);
      }
    } else {
      result = new TestEnvMessage(testCount + " tests");
      ITextComponent status = new TextComponentString(" OK");
      status.setStyle(new Style().setColor(TextFormatting.GREEN));
      result.appendSibling(status);
    }
    return result;
  }

}
