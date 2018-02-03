package net.wizardsoflua.testenv;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.notification.Failure;
import org.junit.runners.model.InitializationError;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.wizardsoflua.testenv.junit.TestResults;

public class TestCommand extends CommandBase {
  private static final String CMD_NAME = "test";
  private static final String TRIANGLE = "\u25B6";
  private static final String NO_BREAK_SPACE = "\u00A0";

  private final List<String> aliases = new ArrayList<String>();



  public TestCommand() {
    aliases.add(CMD_NAME);
  }

  @Override
  public String getName() {
    return CMD_NAME;
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "";
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    // TODO reuse threads
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          if (hasArgs(args)) {
            Class<?> testClass = parseTestClass(args);
            String methodName = parseMethodName(args);
            if (methodName != null) {
              sender.sendMessage(new TestEnvMessage("Running test"));
              TestResults result = WolTestEnvironment.instance.runTestMethod(testClass, methodName);
              sendResult(server, sender.getName(), toTestEnvMessage(result));
            } else {
              sender.sendMessage(new TestEnvMessage("Running tests"));
              TestResults result = WolTestEnvironment.instance.runTests(testClass);
              sendResult(server, sender.getName(), toTestEnvMessage(result));
            }
          } else {
            sender.sendMessage(new TestEnvMessage("Running all tests"));
            Iterable<TestResults> result = WolTestEnvironment.instance.runAllTests();
            sendResult(server, sender.getName(), toTestEnvMessage(result));
          }
        } catch (InitializationError | ClassNotFoundException e) {
          sendResult(server, sender.getName(), new TextComponentString(e.getMessage()));
        }
      }

    }, "test-command-thread");
    t.start();
  }

  // Send a message to the player with the given name.
  // This ensures that the player gets the message even when he or she has logged out during the
  // test execution
  private void sendResult(MinecraftServer server, String name, ITextComponent message) {
    EntityPlayerMP player = getPlayerByName(server, name);
    if (player != null) {
      player.sendMessage(message);
    }
  }

  private EntityPlayerMP getPlayerByName(MinecraftServer server, String name) {
    EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(name);
    return player;
  }

  private ITextComponent toTestEnvMessage(TestResults result) {
    return toTestEnvMessage(Lists.newArrayList(result));
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

  private boolean hasArgs(String[] args) {
    return args != null && args.length > 0;
  }

  private String parseMethodName(String[] args) {
    if (args == null || args.length < 2) {
      return null;
    }
    return args[1];
  }

  private Class<?> parseTestClass(String[] args) throws ClassNotFoundException {
    if (args == null || args.length < 1) {
      return null;
    }
    return Thread.currentThread().getContextClassLoader().loadClass(args[0]);
  }

}
