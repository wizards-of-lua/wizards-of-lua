package net.wizardsoflua.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.wizardsoflua.lua.SpellEntity;

public class LuaCommand extends CommandBase {
  private static final String CMD_NAME = "lua";
  private final List<String> aliases = new ArrayList<String>();

  public LuaCommand() {
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
    World world = server.getEntityWorld();
    SpellEntity spell = new SpellEntity(world);
    world.spawnEntity(spell);
    sender.sendMessage(new TextComponentString("haha"));
  }

}
