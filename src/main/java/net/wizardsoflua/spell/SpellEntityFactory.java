package net.wizardsoflua.spell;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.wizardsoflua.lua.SpellProgram;
import net.wizardsoflua.lua.SpellProgramFactory;

/**
 * Factory for creating {@link SpellEntity} objects.
 */
public class SpellEntityFactory {
  private final SpellRegistry spellRegistry;
  private final SpellProgramFactory programFactory;

  private long nextSid = 1;

  public SpellEntityFactory(SpellRegistry spellRegistry, SpellProgramFactory programFactory) {
    this.spellRegistry = checkNotNull(spellRegistry, "spellRegistry==null!");
    this.programFactory = checkNotNull(programFactory, "programFactory==null!");
  }

  public SpellEntity create(World world, ICommandSender sender, String code) {
    checkNotNull(world, "world==null!");
    ICommandSender source = getSource(sender);
    SpellProgram program = programFactory.create(world, source, code);
    PositionAndRotation pos = getPositionAndRotation(sender);
    nextSid++;
    SpellEntity result = new SpellEntity(world, source, program, pos, nextSid);
    program.setSpellEntity(result);
    spellRegistry.add(result);
    return result;
  }

  private ICommandSender getSource(ICommandSender sender) {
    Entity entity = sender.getCommandSenderEntity();
    if (entity instanceof SpellEntity) {
      return ((SpellEntity) entity).getSource();
    }
    return sender;
  }

  private PositionAndRotation getPositionAndRotation(ICommandSender sender) {
    if (sender instanceof MinecraftServer) {
      Vec3d pos = new Vec3d(((MinecraftServer) sender).getEntityWorld().getSpawnPoint());
      return new PositionAndRotation(pos, 0, 0);
    }
    Entity entity = sender.getCommandSenderEntity();
    if (entity == null) {
      if (sender instanceof CommandBlockBaseLogic) {
        BlockPos blockPos = sender.getPosition();
        World world = sender.getEntityWorld();
        IBlockState state = world.getBlockState(blockPos);
        EnumFacing facing = (EnumFacing) state.getValue(BlockCommandBlock.FACING);
        float rotationYaw;
        float rotationPitch;
        switch (facing) {
          case UP:
            rotationYaw = 0;
            rotationPitch = -90;
            break;
          case DOWN:
            rotationYaw = 0;
            rotationPitch = 90;
            break;
          default:
            rotationYaw = SpellUtil.getRotationYaw(facing);
            rotationPitch = 0;
            break;
        }
        Vec3d pos = sender.getPositionVector();
        return new PositionAndRotation(pos, rotationYaw, rotationPitch);
      }
      Vec3d pos = sender.getPositionVector();
      return new PositionAndRotation(pos, 0, 0);
    } else if (entity instanceof EntityLivingBase) {
      EntityLivingBase e = (EntityLivingBase) entity;
      Vec3d pos = SpellUtil.getPositionLookingAt(e);
      float rotationYaw = SpellUtil.getRotationYaw(e.getHorizontalFacing());
      float rotationPitch = 0;
      return new PositionAndRotation(pos, rotationYaw, rotationPitch);
    } else if (entity instanceof SpellEntity) {
      return ((SpellEntity) entity).getPositionAndRotation();
    } else {
      throw new IllegalArgumentException(
          format("Unexpected command sender entity: %s", entity.getClass().getName()));
    }
  }

}
