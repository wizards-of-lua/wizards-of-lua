package net.wizardsoflua.testenv;

import java.util.ArrayList;
import java.util.List;

import org.junit.runners.model.InitializationError;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

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
      Class<?> testClass = parseTestClass(args);
      String methodName = parseMethodName(args);
      String result = WolTestEnvironment.instance.runTest2(testClass, methodName);
      sender.sendMessage(new TextComponentString(result));
    } catch (InitializationError | ClassNotFoundException e) {
      sender.sendMessage(new TextComponentString(e.getMessage()));
    }
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
