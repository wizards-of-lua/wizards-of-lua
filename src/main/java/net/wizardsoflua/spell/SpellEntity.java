package net.wizardsoflua.spell;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import net.minecraft.command.ICommandSource;
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

  private ICommandSource owner;
  private SpellProgram program;
  private long sid; // immutable spell id

  private boolean visible = false;
  private Table data = new DefaultTable();

  public SpellEntity(World world, ICommandSource owner, SpellProgram program,
      PositionAndRotation posRot, long sid) {
    super(checkNotNull(world, "world==null!"), posRot.getPos());
    this.owner = checkNotNull(owner, "owner==null!");
    this.program = checkNotNull(program, "program==null!");
    this.sid = sid;
    setPositionAndRotation(posRot);
    String name = SpellEntity.NAME + "-" + sid;
    setName(name);
  }

  // @Override
  // public NBTTagCompound serializeNBT() {
  // NBTTagCompound ret = new NBTTagCompound();
  // // ret.setString("id", this.getEntityString());
  // return writeToNBT(ret);
  // }

  public PositionAndRotation getPositionAndRotation() {
    return new PositionAndRotation(getPositionVector(), rotationYaw, rotationPitch);
  }

  private void setPositionAndRotation(PositionAndRotation posRot) {
    Vec3d pos = posRot.getPos();
    float yaw = posRot.getRotationYaw();
    float pitch = posRot.getRotationPitch();
    setPositionAndRotation(pos.x, pos.y, pos.z, yaw, pitch);
  }

  public ICommandSource getOwner() {
    return owner;
  }

  public long getSid() {
    return sid;
  }

  public @Nullable Entity getOwnerEntity() {
    if (owner instanceof Entity) {
      return (Entity) owner;
    }
    return null;
  }

  public SpellProgram getProgram() {
    return program;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  @Override
  public int getPermissionLevel() {
    return 4;
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
    program.resume();
    if (program.isTerminated()) {
      setDead();
    }

    if (visible) {
      SpellAuraFX.spawnParticle(this);
    }
  }

  @Override
  public void setDead() {
    MinecraftForge.EVENT_BUS.post(new SpellBreakEvent(this));
    super.setDead();
    if (program != null) {
      program.terminate();
    }
    MinecraftForge.EVENT_BUS.post(new SpellTerminatedEvent(this));
  }

  public void replacePlayerInstance(EntityPlayerMP player) {
    Entity commandSender = getOwnerEntity();
    if (commandSender instanceof EntityPlayer) {
      if (commandSender.getUniqueID().equals(player.getUniqueID())) {
        owner = player;
      }
    }
    getProgram().replacePlayerInstance(player);
  }

}
