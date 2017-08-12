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

  private ICommandSender source;
  private SpellProgram program;
  private String sid; // immutable spell id
  private ChunkLoaderTicketSupport chunkLoaderTicketSupport;
  private boolean visible = false;

  public SpellEntity(World world) {
    // Used by MC when loading this entity from persistent data
    super(checkNotNull(world, "world==null!"));
  }

  public SpellEntity(World world, ICommandSender source, SpellProgram program, Vec3d pos,
      String sid) {
    this(world);
    this.source = checkNotNull(source, "source==null!");
    this.program = checkNotNull(program, "program==null!");
    this.sid = sid;
    setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
    setCustomNameTag(sid);
    chunkLoaderTicketSupport = new ChunkLoaderTicketSupport(WizardsOfLua.instance, this);
    chunkLoaderTicketSupport.request();
  }

  public ICommandSender getSource() {
    return source;
  }

  public String getSid() {
    return sid;
  }
  
  public Entity getOwner() {
    return source.getCommandSenderEntity();
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
  protected void entityInit() {

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
    if (chunkLoaderTicketSupport != null) {
      chunkLoaderTicketSupport.updatePosition();
    }
    if (visible) {
      SpellAuraFX.spawnParticle(this);
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
    source.sendMessage(txt);
  }
}
