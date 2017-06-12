package net.wizardsoflua.lua;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.spell.SpellEntity;

public class LuaCommand extends CommandBase {
  private static final String CMD_NAME = "lua";
  private final WizardsOfLua wol = WizardsOfLua.instance;
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
    // TODO return usage
    return "";
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    World world = server.getEntityWorld();
    SpellEntity spell = wol.getSpellEntityFactory().create(world, sender, concat(args));
    world.spawnEntity(spell);
  }

  private String concat(String[] args) {
    return Joiner.on(" ").join(args);
  }

}
