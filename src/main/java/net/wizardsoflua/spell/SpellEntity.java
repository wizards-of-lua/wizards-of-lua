package net.wizardsoflua.spell;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.lua.SpellProgram;
import net.wizardsoflua.permissions.Permissions;

public class SpellEntity extends Entity {
  public static final String NAME = "Spell";
  public static final int ID = 1;
  private static final ResourceLocation RES_LOCATION =
      new ResourceLocation(WizardsOfLua.MODID + ":" + SpellEntity.NAME);

  public static void register() {
    Object mod = WizardsOfLua.instance;
    int trackingRange = 0;
    int updateFrequency = 1;
    boolean sendsVelocityUpdates = false;
    EntityRegistry.registerModEntity(RES_LOCATION, SpellEntity.class, NAME, ID, mod, trackingRange,
        updateFrequency, sendsVelocityUpdates);
  }

  private ICommandSender owner;
  private SpellProgram program;
  private long sid; // immutable spell id
  private ChunkLoaderTicketSupport chunkLoaderTicketSupport;
  private boolean visible = false;
  private boolean lastCommandWasDenied = false;

  public SpellEntity(World world) {
    // Used by MC when loading this entity from persistent data
    super(checkNotNull(world, "world==null!"));
  }

  public SpellEntity(World world, ICommandSender owner, SpellProgram program,
      PositionAndRotation posRot, long sid) {
    this(world);
    this.owner = checkNotNull(owner, "owner==null!");
    this.program = checkNotNull(program, "program==null!");
    this.sid = sid;
    setPositionAndRotation(posRot);
    String name = SpellEntity.NAME + "-" + sid;
    setCustomNameTag(name);
    chunkLoaderTicketSupport = new ChunkLoaderTicketSupport(WizardsOfLua.instance, this);
    chunkLoaderTicketSupport.request();
  }

  /**
   * Calleb by {@link Permissions}. This is a work around that informs this spell if the command it
   * sent lately had been canceled.
   * 
   * @param value
   */
  public void setLastCommandWasDenied(boolean value) {
    this.lastCommandWasDenied = value;
  }

  public PositionAndRotation getPositionAndRotation() {
    return new PositionAndRotation(getPositionVector(), rotationYaw, rotationPitch);
  }

  private void setPositionAndRotation(PositionAndRotation posRot) {
    Vec3d pos = posRot.getPos();
    float yaw = posRot.getRotationYaw();
    float pitch = posRot.getRotationPitch();
    setPositionAndRotation(pos.xCoord, pos.yCoord, pos.zCoord, yaw, pitch);
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

  @Override
  protected void readEntityFromNBT(NBTTagCompound compound) {}

  @Override
  protected void writeEntityToNBT(NBTTagCompound compound) {}

  @Override
  protected void entityInit() {}

  @Override
  public void setPosition(double x, double y, double z) {
    if (chunkLoaderTicketSupport != null) {
      chunkLoaderTicketSupport.updatePosition();
    }
    super.setPosition(x, y, z);
  }

  @Override
  public Vec3d getLookVec() {
    return this.getLook(1.0F);
  }

  @Override
  public void onUpdate() {
    super.onUpdate();
    if (program == null) {
      setDead();
      return;
    }
    try {
      program.resume();
    } catch (SpellException e) {
      handleException(e);
    }
    if (program.isTerminated()) {
      setDead();
    }

    if (visible) {
      SpellAuraFX.spawnParticle(this);
    }
    applyMotion();
  }

  private void applyMotion() {
    if (motionX != 0 || motionY != 0 || motionZ != 0) {
      setPositionAndUpdate(posX + motionX, posY + motionY, posZ + motionZ);
    }
  }

  @Override
  public void setDead() {
    if (program != null) {
      program.terminate();
    }
    if (chunkLoaderTicketSupport != null) {
      chunkLoaderTicketSupport.release();
    }
    super.setDead();
    MinecraftForge.EVENT_BUS.post(new SpellTerminatedEvent(this));
  }

  private void handleException(SpellException e) {
    e.printStackTrace();
    String message = String.format("Error during command execution: %s", e.getMessage());
    TextComponentString txt = new TextComponentString(message);
    txt.setStyle((new Style()).setColor(TextFormatting.RED).setBold(Boolean.valueOf(true)));
    owner.sendMessage(txt);
  }

  public int execute(String command) {
    // We only want to accept this call when it is done by this spell's program.
    if (program.isRunning()) {
      World world = getEntityWorld();
      int result = world.getMinecraftServer().getCommandManager().executeCommand(this, command);
      // Since forge would not return 0 for canceled commands, we must work around this.
      if (result == 1 && lastCommandWasDenied) {
        return 0;
      } else {
        return result;
      }
    }
    return 0;
  }

}
