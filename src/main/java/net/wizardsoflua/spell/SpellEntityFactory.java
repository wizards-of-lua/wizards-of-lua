package net.wizardsoflua.spell;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.wizardsoflua.lua.SpellProgram;
import net.wizardsoflua.lua.SpellProgramFactory;

/**
 * Factory for crating {@link SpellEntity} objects.
 */
public class SpellEntityFactory {

  private final SpellProgramFactory programFactory;

  public SpellEntityFactory(SpellProgramFactory programFactory) {
    this.programFactory = programFactory;
  }

  public SpellEntity create(World world, ICommandSender sender, String code) {
    ICommandSender source = getSource(sender);
    SpellProgram program = programFactory.create(world, source, code);
    Vec3d pos = getPos(sender);
    return new SpellEntity(world, source, program, pos);
  }

  private ICommandSender getSource(ICommandSender sender) {
    Entity entity = sender.getCommandSenderEntity();
    if (entity instanceof SpellEntity) {
      return ((SpellEntity) entity).getSource();
    }
    return sender;
  }

  private Vec3d getPos(ICommandSender sender) {
    if (sender instanceof MinecraftServer) {
      return new Vec3d(((MinecraftServer) sender).getEntityWorld().getSpawnPoint());
    }
    Entity entity = sender.getCommandSenderEntity();
    if (entity == null) {
      return sender.getPositionVector();
    } else {
      return SpellUtil.getPositionLookingAt(entity);
    }
  }
}
