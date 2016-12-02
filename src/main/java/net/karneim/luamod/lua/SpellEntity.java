package net.karneim.luamod.lua;

import java.util.Collection;

import net.karneim.luamod.LuaMod;
import net.karneim.luamod.credentials.Credentials;
import net.karneim.luamod.cursor.Clipboard;
import net.karneim.luamod.cursor.Cursor;
import net.karneim.luamod.lua.event.EventQueue;
import net.karneim.luamod.lua.event.EventWrapper;
import net.karneim.luamod.lua.event.Events;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.Continuation;
import net.sandius.rembulan.load.LoaderException;

public class SpellEntity extends Entity {

  private LuaMod mod;
  private ICommandSender owner;
  private Clipboard clipboard;
  private Cursor cursor;

  private enum State {
    START, RESUME, SUSPEND, PAUSE, STOP, DEAD
  }

  private State state = State.START;
  private String program;
  private Continuation continuation;
  private LuaUtil luaUtil;
  private Ticket chunkLoaderTicket;
  private ChunkPos chunkPos;

  public SpellEntity(World worldIn) {
    super(worldIn);
  }

  public SpellEntity(World worldIn, LuaMod mod, ICommandSender aOwner, Clipboard clipboard,
      BlockPos pos, Rotation rotation, EnumFacing surface) {
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
    this.cursor = new Cursor(owner, this, this.getEntityWorld(), pos, rotation, surface);
    Credentials credentials = mod.getCredentialsStore().retrieveCredentials("GitHub", userId);
    luaUtil = new LuaUtil(owner, cursor, clipboard, credentials);
    if (surface != null) {
      pos = pos.offset(surface, 1);
    }
    updatePosition();
    mod.getSpellRegistry().register(this);
  }

  public ICommandSender getOwner() {
    return owner;
  }

  public Clipboard getClipboard() {
    return clipboard;
  }

  public void setProgram(String program) throws LoaderException {
    this.program = program;
    luaUtil.compile(program);
  }

  @Override
  protected void entityInit() {
    requestChunkLoaderTicket();
    chunkLoaderTicket.bindEntity(this);
    chunkPos = new ChunkPos(getPosition());
    ForgeChunkManager.forceChunk(chunkLoaderTicket, chunkPos);
  }

  boolean isInside(ChunkPos cPos, BlockPos bPos) {
    return cPos.getXStart() <= bPos.getX() && bPos.getX() <= cPos.getXEnd()
        && cPos.getZStart() <= bPos.getZ() && bPos.getZ() <= cPos.getZEnd();
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

  private void updatePosition() {
    BlockPos pos = cursor.getWorldPosition();
    setPosition(pos.getX(), pos.getY(), pos.getZ());
    if (chunkLoaderTicket != null) {
      if (!isInside(chunkPos, pos)) {
        ForgeChunkManager.unforceChunk(chunkLoaderTicket, chunkPos);
        chunkPos = new ChunkPos(pos);
        ForgeChunkManager.forceChunk(chunkLoaderTicket, chunkPos);
      }
    }
  }

  public void compile() {

  }

  public void onUpdate() {
    super.onUpdate();
    if (luaUtil == null) {
      setDead();
      return;
    }
    if (!getEntityWorld().isRemote) {
      luaUtil.getEvents().setCurrentTime(ticksExisted);
      switch (state) {
        case START:

          try {
            luaUtil.run();
            state = State.STOP;
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
        case RESUME:
          try {
            if (!luaUtil.getEvents().isWaiting()) {
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
  }


  public Cursor getCursor() {
    return cursor;
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
