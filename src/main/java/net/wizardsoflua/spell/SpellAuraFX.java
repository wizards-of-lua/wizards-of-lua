package net.wizardsoflua.spell;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;

public class SpellAuraFX {
  private static class State {
    final EnumParticleTypes type;
    final double speed;
    final double offset;
    final boolean longDistance;

    public State(EnumParticleTypes type, double speed, double offset, boolean longDistance) {
      this.type = type;
      this.speed = speed;
      this.offset = offset;
      this.longDistance = longDistance;
    }
  }

  private static final State[] STATES = {new State(EnumParticleTypes.CLOUD, 0, 0.1, true),
      new State(EnumParticleTypes.REDSTONE, 0, 0.4, false),
      new State(EnumParticleTypes.SPELL_MOB, 1, 0, false),
      new State(EnumParticleTypes.TOWN_AURA, 0, 0.12, false)};
  private static final int[] PARTICLE_ARGS = new int[0];

  public static void spawnParticle(SpellEntity spell) {
    int time = spell.ticksExisted;
    if (time % 2 > 0) {
      return;
    }
    time = time / 2;

    WorldServer worldserver = (WorldServer) spell.getEntityWorld();
    State state = STATES[time % STATES.length];
    EnumParticleTypes particleType = state.type;
    boolean longDistance = state.longDistance;
    double x = spell.posX;
    double y = spell.posY;
    double z = spell.posZ;
    int numberOfParticles = 2;
    double offset = state.offset;
    double xOffset = offset;
    double yOffset = offset;
    double zOffset = offset;
    double particleSpeed = state.speed;

    SpellUtil.spawnParticle(worldserver, particleType, longDistance, x, y, z, numberOfParticles,
        xOffset, yOffset, zOffset, particleSpeed, PARTICLE_ARGS);
  }
}
