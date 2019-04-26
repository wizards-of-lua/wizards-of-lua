package net.wizardsoflua.spell;

import static java.util.Objects.requireNonNull;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.wizardsoflua.chunk.ChunkForceManager;
import net.wizardsoflua.lua.SpellProgram;
import net.wizardsoflua.lua.SpellProgramFactory;
import net.wizardsoflua.lua.module.print.PrintRedirector.PrintReceiver;

/**
 * Factory for creating {@link SpellEntity} objects.
 */
public class SpellEntityFactory {
  private final SpellRegistry spellRegistry;
  private final SpellProgramFactory programFactory;
  private final ChunkForceManager chunkForceManager;

  private long nextSid = 1;

  public SpellEntityFactory(SpellRegistry spellRegistry, SpellProgramFactory programFactory,
      ChunkForceManager chunkForceManager) {
    this.spellRegistry = requireNonNull(spellRegistry, "spellRegistry");
    this.programFactory = requireNonNull(programFactory, "programFactory");
    this.chunkForceManager = requireNonNull(chunkForceManager, "chunkForceManager");
  }

  public SpellEntity create(CommandSource source, PrintReceiver printReceiver, String code,
      @Nullable String... arguments) {
    Entity owner = getOwner(source);
    World world = source.getWorld();
    PositionAndRotation pos = getPositionAndRotation(source);
    return create(owner, printReceiver, world, pos, code, arguments);
  }

  public SpellEntity create(@Nullable Entity owner, PrintReceiver printReceiver, World world,
      PositionAndRotation pos, String code, String... arguments) {
    SpellProgram program = programFactory.create(world, owner, printReceiver, code, arguments);
    nextSid++;
    SpellEntity result = new SpellEntity(world, program, pos, nextSid, chunkForceManager);
    program.setSpellEntity(result);
    spellRegistry.add(result);
    return result;
  }

  private Entity getOwner(CommandSource source) {
    if (source instanceof WolCommandSource) {
      WolCommandSource wolSource = (WolCommandSource) source;
      ICommandSource source2 = wolSource.getSource();
      if (source2 instanceof SpellEntity) {
        SpellEntity spell = (SpellEntity) source2;
        return spell.getOwner();
      }
    }
    return source.getEntity();
  }

  private PositionAndRotation getPositionAndRotation(CommandSource source) {
    Entity entity = source.getEntity();
    Vec3d pos = source.getPos();
    Vec2f pitchYaw = source.getPitchYaw();
    if (entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) entity;
      if (pos.equals(player.getPositionVector()) && pitchYaw.equals(player.getPitchYaw())) {
        pos = SpellUtil.getPositionLookingAt(player);
        float rotationYaw = SpellUtil.getRotationYaw(player.getHorizontalFacing());
        float rotationPitch = 0;
        return new PositionAndRotation(pos, rotationYaw, rotationPitch);
      }
    }
    return new PositionAndRotation(pos, pitchYaw);
  }

}
