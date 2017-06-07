package net.wizardsoflua.testenv;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.notification.Failure;
import org.junit.runners.model.InitializationError;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.wizardsoflua.testenv.junit.TestResult;
import net.wizardsoflua.testenv.junit.TestResults;

public class TestCommand extends CommandBase {
  private static final String CMD_NAME = "test";
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
    try {
      if (hasArgs(args)) {
        Class<?> testClass = parseTestClass(args);
        String methodName = parseMethodName(args);
        if (methodName != null) {
          TestResult result = WolTestEnvironment.instance.runTestMethod(testClass, methodName);
          sender.sendMessage(new TextComponentString(toMessage(result)));
        } else {
          TestResults result = WolTestEnvironment.instance.runTests(testClass);
          sender.sendMessage(new TextComponentString(toMessage(result)));
        }
      } else {
        Iterable<TestResults> result = WolTestEnvironment.instance.runAllTests();
        sender.sendMessage(new TextComponentString(toMessage(result)));
      }
    } catch (InitializationError | ClassNotFoundException e) {
      sender.sendMessage(new TextComponentString(e.getMessage()));
    }
  }

  private String toMessage(TestResults result) {
    return toMessage(Lists.newArrayList(result));
  }

  private String toMessage(TestResult result) {
    if (result.isOK()) {
      return "OK";
    } else {
      return result.getFailure().getMessage();
    }
  }

  private String toMessage(Iterable<TestResults> results) {
    StringBuilder result = new StringBuilder();
    int testCount = 0;
    int failureCount = 0;
    for (TestResults r : results) {
      testCount += r.getTestsFinished();
      if (!r.isOK()) {
        Iterable<Failure> f = r.getFailures();
        for (Failure failure : f) {
          failureCount++;
          if (result.length() > 0) {
            result.append("\n");
          }
          result.append(failure.getTestHeader());
          result.append(":\n");
          result.append(failure.getMessage());

        }
      }
    }
    if (failureCount > 0) {
      return failureCount + " of " + testCount + " tests failed." + "\n" + result.toString();
    }
    return testCount + " tests OK";
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
