package net.wizardsoflua.command;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.spell.SpellUtil;

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
    // TODO return usage
    return "";
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    World world = server.getEntityWorld();
    ICommandSender owner = getOwner(sender);
    Vec3d pos = getPos(sender);
    SpellEntity spell = new SpellEntity(world, owner, concat(args));
    spell.setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
    world.spawnEntity(spell);
    //sender.sendMessage(new TextComponentString("haha"));
  }

  private String concat(String[] args) {
    return Joiner.on(" ").join(args);
  }

  private ICommandSender getOwner(ICommandSender sender) {
    // TODO if sender is a spell, return the spell's owner
    return sender;
  }
  
  private Vec3d getPos(ICommandSender sender) {
    if ( sender instanceof MinecraftServer) {
      return new Vec3d(((MinecraftServer)sender).getEntityWorld().getSpawnPoint());
    }
    Entity entity = sender.getCommandSenderEntity();
    if (entity == null) {
      return sender.getPositionVector();
    } else {
      return SpellUtil.getPositionLookingAt(entity);
    }
  }

}
