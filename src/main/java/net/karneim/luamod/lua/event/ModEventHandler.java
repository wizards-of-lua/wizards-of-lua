package net.karneim.luamod.lua.event;

import net.karneim.luamod.LuaMod;
import net.karneim.luamod.lua.SpellEntity;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class ModEventHandler {
  private LuaMod mod;

  public ModEventHandler(LuaMod mod) {
    this.mod = mod;
  }

  @SubscribeEvent
  public void onCommand(CommandEvent evt) {
    // System.out.println("onCommand: " + evt.getCommand().getCommandName() + " "
    // + Arrays.toString(evt.getParameters()));
  }

  @SubscribeEvent
  public void onChat(ServerChatEvent evt) {
    onEvent(EventType.CHAT, evt);
  }

  @SubscribeEvent
  public void onLeftClickBlock(LeftClickBlock evt) {
    if (evt.getWorld().isRemote) {
      return;
    }
    onEvent(EventType.LEFT_CLICK, evt);
  }

  @SubscribeEvent
  public void onRightClickBlock(RightClickBlock evt) {
    if (evt.getWorld().isRemote) {
      return;
    }
    onEvent(EventType.RIGHT_CLICK, evt);
  }

  @SubscribeEvent
  public void onPlayerLoggedIn(PlayerLoggedInEvent evt) {
    onEvent(EventType.PLAYER_JOINED, evt);
  }

  @SubscribeEvent
  public void onPlayerLoggedOut(PlayerLoggedOutEvent evt) {
    onEvent(EventType.PLAYER_LEFT, evt);
  }

  @SubscribeEvent
  public void onPlayerRespawn(PlayerRespawnEvent evt) {
    onEvent(EventType.PLAYER_SPAWNED, evt);
  }

  /////

  public void onWhisper(WhisperEvent evt) {
    onEvent(EventType.WHISPER, evt);
  }

  ////

  private void onEvent(EventType type, Object evt) {
    EventWrapper<?> wrapper = type.wrap(evt);
    for (SpellEntity e : mod.getSpellRegistry().getAll()) {
      e.getEvents().handle(wrapper);
    }
  }

}
