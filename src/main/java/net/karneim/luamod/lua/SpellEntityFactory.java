package net.karneim.luamod.lua;

import java.io.IOException;

import net.karneim.luamod.LuaMod;
import net.karneim.luamod.cursor.Clipboard;
import net.karneim.luamod.cursor.SpellUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.sandius.rembulan.load.LoaderException;

public class SpellEntityFactory {

  private final LuaMod mod;

  public SpellEntityFactory(LuaMod mod) {
    this.mod = mod;
  }

  public SpellEntity create(World world, ICommandSender sender, ICommandSender owner)
      throws IOException, LoaderException {
    Clipboard clipboard = getClipboard(sender);
    Vec3d pos = getPos(sender);
    Rotation rot = getRotation(sender);
    EnumFacing surface = getSurface(sender);
    SpellEntity spell = new SpellEntity(sender.getEntityWorld(), LuaMod.instance, owner, clipboard,
        pos, rot, surface);
    return spell;
  }

  private Clipboard getClipboard(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof SpellEntity) {
      SpellEntity lpe = (SpellEntity) sender;
      return lpe.getClipboard();
    } else if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      return mod.getClipboards().get((EntityPlayer) sender.getCommandSenderEntity());
    }
    return new Clipboard();
  }

  private EnumFacing getSurface(ICommandSender sender) {
    Entity entity = sender.getCommandSenderEntity();
    if (entity == null) {
      return null;
    } else if (entity instanceof SpellEntity) {
      return null;
    } else {
      EnumFacing side = SpellUtil.getSideLookingAt(entity);
      return side == null ? null : side;
    }
  }

  private Rotation getRotation(ICommandSender sender) {
    Entity entity = sender.getCommandSenderEntity();
    if (entity == null) {
      return Rotation.NONE;
    } else if (entity instanceof SpellEntity) {
      SpellEntity luaProcessEntity = (SpellEntity) entity;
      return SpellUtil.roundRotation(luaProcessEntity.getCursor().getRotation());
    } else {
      return SpellUtil.getRotation(entity.getHorizontalFacing());
    }
  }

  private Vec3d getPos(ICommandSender sender) {
    Entity entity = sender.getCommandSenderEntity();
    if (entity == null) {
      return sender.getPositionVector();
    } else if (entity instanceof SpellEntity) {
      SpellEntity luaProcessEntity = (SpellEntity) entity;
      return luaProcessEntity.getCursor().getWorldPosition();
    } else {
      //return new Vec3d(SpellUtil.getPositionLookingAt(entity));
      return SpellUtil.getPositionLookingAt(entity);
    }
  }

}
