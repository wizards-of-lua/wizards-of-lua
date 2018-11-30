package net.wizardsoflua.spell;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.wizardsoflua.lua.SpellProgram;
import net.wizardsoflua.lua.view.ViewFactory;

public class SpellEntity extends VirtualEntity {
  public static final String NAME = "Spell";

  private ICommandSender owner;
  private @Nullable SpellProgram program;
  private long sid; // immutable spell id

  private boolean visible = false;
  private Table data = new DefaultTable();

  public SpellEntity(World world, ICommandSender owner, PositionAndRotation posRot, long sid) {
    super(checkNotNull(world, "world==null!"), posRot.getPos());
    this.owner = checkNotNull(owner, "owner==null!");
    this.sid = sid;
    setPositionAndRotation(posRot);
    String name = SpellEntity.NAME + "-" + sid;
    setName(name);
  }

  public @Nullable SpellProgram getProgram() {
    return program;
  }

  public void setProgram(@Nullable SpellProgram program) {
    this.program = program;
  }

  public PositionAndRotation getPositionAndRotation() {
    return new PositionAndRotation(getPositionVector(), rotationYaw, rotationPitch);
  }

  private void setPositionAndRotation(PositionAndRotation posRot) {
    Vec3d pos = posRot.getPos();
    float yaw = posRot.getRotationYaw();
    float pitch = posRot.getRotationPitch();
    setPositionAndRotation(pos.x, pos.y, pos.z, yaw, pitch);
  }

  public ICommandSender getOwner() {
    return owner;
  }

  public long getSid() {
    return sid;
  }

  public Entity getOwnerEntity() {
    return owner.getCommandSenderEntity();
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public Table getData(ViewFactory viewer) {
    ViewFactory provider = program.getViewFactory();
    if (viewer == provider) {
      return data;
    }
    return (Table) viewer.getView(data, provider);
  }

  @Override
  public void onUpdate() {
    super.onUpdate();
    if (program == null) {
      setDead();
      return;
    }
    program.tick();
    if (program.isTerminated()) {
      setDead();
    }

    if (visible) {
      SpellAuraFX.spawnParticle(this);
    }
  }

  @Override
  public void setDead() {
    if (isAlive()) {
      MinecraftForge.EVENT_BUS.post(new SpellBreakEvent(this));
      super.setDead();
      if (program != null) {
        program.terminate();
      }
      MinecraftForge.EVENT_BUS.post(new SpellTerminatedEvent(this));
    }
  }

  public void replacePlayerInstance(EntityPlayerMP player) {
    Entity commandSender = owner.getCommandSenderEntity();
    if (commandSender instanceof EntityPlayer) {
      if (commandSender.getUniqueID().equals(player.getUniqueID())) {
        owner = player;
      }
    }
    getProgram().replacePlayerInstance(player);
  }

}
