package net.karneim.luamod.lua;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import net.karneim.luamod.LuaMod;
import net.karneim.luamod.credentials.Credentials;
import net.karneim.luamod.credentials.Realm;
import net.karneim.luamod.cursor.Clipboard;
import net.karneim.luamod.cursor.Snapshots;
import net.karneim.luamod.cursor.Spell;
import net.karneim.luamod.lua.event.Events;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.Continuation;

public class SpellEntity extends Entity {

  private LuaMod mod;
  private ICommandSender owner;
  private Clipboard clipboard;
  private Spell spell;

  private enum State {
    START, RESUME, SUSPEND, PAUSE, STOP, DEAD
  }

  private State state = State.START;
  private String command;
  private Continuation continuation;
  private LuaUtil luaUtil;
  private Ticket chunkLoaderTicket;
  private ChunkPos chunkPos;

  public SpellEntity(World worldIn) {
    super(worldIn);
  }

  public SpellEntity(World worldIn, LuaMod mod, ICommandSender aOwner, Clipboard clipboard,
      Vec3d pos, Rotation rotation, EnumFacing surface, String name, Collection<String> profiles, String command) {
    this(worldIn);
    this.mod = mod;
    this.owner = aOwner;
    this.clipboard = clipboard;
    Entity entity = owner.getCommandSenderEntity();
    String userId;
    if (entity == null || entity.getUniqueID() == null) {
      userId = null;
    } else {
      userId = owner.getCommandSenderEntity().getUniqueID().toString();
    }
    Snapshots snapshots = new Snapshots();
    this.spell = new Spell(owner, this, this.getEntityWorld(), pos, rotation, surface, snapshots);
    Credentials credentials = mod.getCredentialsStore().retrieveCredentials(Realm.GitHub, userId);
    luaUtil = new LuaUtil(this.getEntityWorld(), this, owner, spell, clipboard, credentials, snapshots);
    this.command = command;
    // TODO pass profile & command into constructor
    luaUtil.setProfiles(profiles);
    luaUtil.setCommand(command);
    setCustomNameTag(name);
    if (surface != null) {
      pos = pos.add(new Vec3d(surface.getDirectionVec()));
    }
    updatePosition();
  }

  public ICommandSender getOwner() {
    return owner;
  }

  public Clipboard getClipboard() {
    return clipboard;
  }
  
  public @Nullable String getCommand() {
    return command;
  }

  @Override
  protected void entityInit() {
    requestChunkLoaderTicket();
    chunkLoaderTicket.bindEntity(this);
    chunkPos = new ChunkPos(getPosition());
    ForgeChunkManager.forceChunk(chunkLoaderTicket, chunkPos);
  }

  boolean isInside(ChunkPos cPos, Vec3d pos) {
    return cPos.getXStart() <= pos.xCoord && pos.xCoord <= cPos.getXEnd()
        && cPos.getZStart() <= pos.zCoord && pos.zCoord <= cPos.getZEnd();
  }

  @Override
  protected void readEntityFromNBT(NBTTagCompound compound) {
    // TODO Auto-generated method stub

  }

  @Override
  protected void writeEntityToNBT(NBTTagCompound compound) {
    // TODO Auto-generated method stub

  }

  // @Override
  // public void addChatMessage(ITextComponent component) {
  // // whispers to you
  // String unformattedComponentText = component.getUnformattedComponentText();
  // System.out.println("unformattedComponentText "+unformattedComponentText);
  // // Player695 whispers to you: hi
  // String unformattedText = component.getUnformattedText();
  // System.out.println("unformattedText "+unformattedText);
  //
  // luaUtil.handleEvent(new Event(EventType.MESSAGE_EVENT, unformattedText));
  // }

  private void requestChunkLoaderTicket() {
    chunkLoaderTicket = ForgeChunkManager.requestTicket(LuaMod.instance, getEntityWorld(),
        ForgeChunkManager.Type.ENTITY);
  }

  private void releaseChunkLoaderTicket() {
    if (chunkLoaderTicket != null) {
      try {
        ForgeChunkManager.releaseTicket(chunkLoaderTicket);
      } catch (Throwable e) {
        // ignored
      }
      chunkLoaderTicket = null;
    }
  }

  /**
   * Syncs the spell's position back into this spell entity
   */
  public void updatePosition() {
    Vec3d pos = spell.getPosition();
    setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
    float yaw = spell.getRotation();
    float pitch = 0;
    setRotation(yaw, pitch);
  }
  
  /**
   * Updates the ChunkManager by pasing in the entity's current position
   */
  private void updateChunkManager() {
    if (chunkLoaderTicket != null) {
      Vec3d pos = getPositionVector();
      if (!isInside(chunkPos, pos)) {
        ForgeChunkManager.unforceChunk(chunkLoaderTicket, chunkPos);
        chunkPos = new ChunkPos(new BlockPos(pos));
        ForgeChunkManager.forceChunk(chunkLoaderTicket, chunkPos);
      }
    }    
  }

  public void onUpdate() {
    super.onUpdate();
    if (luaUtil == null) {
      setDead();
      return;
    }
    if (!getEntityWorld().isRemote) {
      luaUtil.setCurrentTime(ticksExisted);
      switch (state) {
        case START:
          try {
            mod.getSpellRegistry().register(this);
            luaUtil.run();
            state = State.STOP;
          } catch (CallPausedException e) {
            continuation = e.getContinuation();
            if (canResume()) {
              state = State.RESUME;
            }
          } catch (Exception e) {
            e.printStackTrace();
            String message = String.format("Error during command execution: %s", e.getMessage());
            TextComponentString txt = new TextComponentString(message);
            txt.setStyle((new Style()).setColor(TextFormatting.RED).setBold(Boolean.valueOf(true)));
            owner.addChatMessage(txt);
            state = State.STOP;
          }
          break;
        case RESUME:
          try {
            if (!luaUtil.isWaiting()) {
              luaUtil.resume(continuation);
              state = State.STOP;
            }
          } catch (CallPausedException e) {
            continuation = e.getContinuation();
            if (canResume()) {
              state = State.RESUME;
            }
          } catch (Exception e) {
            e.printStackTrace();
            String message = String.format("Error during command execution: %s!", e.getMessage());
            TextComponentString txt = new TextComponentString(message);
            txt.setStyle((new Style()).setColor(TextFormatting.RED).setBold(Boolean.valueOf(true)));
            owner.addChatMessage(txt);
            state = State.STOP;
          }
          break;
        case STOP:
          setDead();
          break;
        case DEAD:
          System.err.println("onUpdate() called on dead entity!");
        case SUSPEND:
        default:
          break;
      }
    }
    updatePosition();
    updateChunkManager();
  }


  public Spell getCursor() {
    return spell;
  }

  @Override
  public void setDead() {
    state = State.DEAD;
    System.out.println("Terminating " + getName() + "!");
    super.setDead();
    releaseChunkLoaderTicket();
    LuaMod.instance.getSpellRegistry().unregister(this);
  }

  public void pause() {
    if (canPause()) {
      state = State.PAUSE;
    }
  }

  public void unpause() {
    if (canUnpause()) {
      state = State.RESUME;
    }
  }

  private boolean canResume() {
    switch (state) {
      case START:
      case RESUME:
        return true;
      default:
        return false;
    }
  }

  private boolean canPause() {
    switch (state) {
      case START:
      case RESUME:
        return true;
      default:
        return false;
    }
  }

  private boolean canUnpause() {
    switch (state) {
      case PAUSE:
        return true;
      default:
        return false;
    }
  }

  public Events getEvents() {
    return luaUtil.getEvents();
  }


}
