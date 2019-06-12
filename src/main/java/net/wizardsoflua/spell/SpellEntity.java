package net.wizardsoflua.spell;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.command.CommandResultStats;
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
  private SpellProgram program;
  private long sid; // immutable spell id

  private boolean visible = false;
  private Table data = new DefaultTable();
  private Map<CommandResultStats.Type, Integer> lastCommandResults = new HashMap<>();

  public SpellEntity(World world, ICommandSender owner, SpellProgram program,
      PositionAndRotation posRot, long sid) {
    super(checkNotNull(world, "world==null!"), posRot.getPos());
    this.owner = checkNotNull(owner, "owner==null!");
    this.program = checkNotNull(program, "program==null!");
    this.sid = sid;
    setPositionAndRotation(posRot);
    String name = SpellEntity.NAME + "-" + sid;
    setName(name);
  }

  @Override
  public void setCommandStat(CommandResultStats.Type resultType, int amount) {
    Integer old = lastCommandResults.get(resultType);
    if (old == null) {
      old = 0;
    }
    lastCommandResults.put(resultType, amount + old);
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

  public ICommandSender getOwner() {
    return owner;
  }

  public long getSid() {
    return sid;
  }

  public Entity getOwnerEntity() {
    return owner.getCommandSenderEntity();
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
    Entity commandSender = owner.getCommandSenderEntity();
    if (commandSender instanceof EntityPlayer) {
      if (commandSender.getUniqueID().equals(player.getUniqueID())) {
        owner = player;
      }
    }
    getProgram().replacePlayerInstance(player);
  }

  /**
   * Example:
   *
   * /lua r=spell:execute([[ /fill -43 100 312 -40 210 333 minecraft:stone ]])
   *
   * r is 9768
   */
  public int execute(String command) {
    lastCommandResults.clear();
    boolean isExecute =
        command.trim().startsWith("execute") || command.trim().startsWith("/execute");
    getEntityWorld().getMinecraftServer().getCommandManager().executeCommand(this, command);

    Integer result = null;
    Integer successCount = lastCommandResults.get(CommandResultStats.Type.SUCCESS_COUNT);
    if (isExecute) {
      result = successCount;
    } else {
      if (result == null) {
        result = lastCommandResults.get(CommandResultStats.Type.QUERY_RESULT);
      }
      if (result == null) {
        result = lastCommandResults.get(CommandResultStats.Type.AFFECTED_BLOCKS);
      }
      if (result == null) {
        result = lastCommandResults.get(CommandResultStats.Type.AFFECTED_ITEMS);
      }
      if (result == null) {
        result = lastCommandResults.get(CommandResultStats.Type.AFFECTED_ENTITIES);
      }
      if (result == null) {
        result = lastCommandResults.get(CommandResultStats.Type.SUCCESS_COUNT);
      }
    }
    if (result == null) {
      result = 0;
    }
    // System.out.println("lastCommandResults=" + lastCommandResults);
    return result;
  }

}
