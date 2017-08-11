package net.wizardsoflua.spell;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
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
  
  private long nextId = 1;

  public SpellEntityFactory(SpellRegistry spellRegistry, SpellProgramFactory programFactory) {
    this.spellRegistry = checkNotNull(spellRegistry, "spellRegistry==null!");
    this.programFactory = checkNotNull(programFactory, "programFactory==null!");
  }

  public SpellEntity create(World world, ICommandSender sender, String code) {
    checkNotNull(world, "world==null!");
    ICommandSender source = getSource(sender);
    SpellProgram program = programFactory.create(world, source, code);
    Vec3d pos = getPos(sender);
    String name = SpellEntity.NAME+"-"+nextId;
    nextId++;
    SpellEntity result = new SpellEntity(world, source, program, pos, name);
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
