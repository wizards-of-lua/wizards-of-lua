package net.wizardsoflua.wol;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.wizardsoflua.WizardsOfLua;

public class WolCommand extends CommandBase {
  private static final String CMD_NAME = "wol";
  private final WizardsOfLua wol = WizardsOfLua.instance;
  private final List<String> aliases = new ArrayList<String>();
  private final WolCommandParser commandParser = new WolCommandParser();

  public WolCommand() {
    aliases.add(CMD_NAME);
  }

  @Override
  public String getName() {
    return CMD_NAME;
  }

  @Override
  public String getUsage(ICommandSender sender) {
    // TODO return usage
    return "";
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    WolCommandAction action = commandParser.parse(args);
    action.execute(sender);
  }

}
