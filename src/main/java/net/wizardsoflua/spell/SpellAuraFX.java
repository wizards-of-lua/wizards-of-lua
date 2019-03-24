package net.wizardsoflua.spell;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Particles;
import net.minecraft.particles.IParticleData;
import net.minecraft.world.WorldServer;

public class SpellAuraFX {
  private static class State {
    final IParticleData type;
    final float speed;
    final double offset;
    final boolean longDistance;

    public State(IParticleData type, float speed, double offset, boolean longDistance) {
      this.type = type;
      this.speed = speed;
      this.offset = offset;
      this.longDistance = longDistance;
    }
  }

  private static final State[] STATES = {new State(Particles.CLOUD, 0, 0.1, true),
      new State(net.minecraft.particles.RedstoneParticleData.REDSTONE_DUST, 0, 0.4, false),
      new State(Particles.WITCH, 1, 0, false), new State(Particles.MYCELIUM, 0, 0.12, false)};

  public static void spawnParticle(SpellEntity spell) {
    int time = spell.getAge();
    if (time % 2 > 0) {
      return;
    }
    time = time / 2;

    double x = spell.posX;
    double y = spell.posY;
    double z = spell.posZ;

    spawnParticle(spell, x, y, z, time);
  }

  private static void spawnParticle(SpellEntity spell, double x, double y, double z, int time) {
    WorldServer world = (WorldServer) spell.getWorld();
    State state = STATES[time % STATES.length];
    IParticleData particleData = state.type;
    double offset = state.offset;
    double offsetX = offset;
    double offsetY = offset;
    double offsetZ = offset;
    float particleSpeed = state.speed;
    int numberOfParticles = 2;
    boolean force = state.longDistance;
    Collection<EntityPlayerMP> viewers = world.getServer().getPlayerList().getPlayers();
    SpellUtil.spawnParticle(world, particleData, x, y, z, offsetX, offsetY, offsetZ, particleSpeed,
        numberOfParticles, force, viewers);
  }
}
