package net.karneim.luamod.lua.event;

import net.karneim.luamod.lua.classes.event.Player2EventClass;
import net.karneim.luamod.lua.classes.event.PlayerInteractEventClass;
import net.karneim.luamod.lua.classes.event.ServerChatEventClass;
import net.karneim.luamod.lua.classes.event.WhisperEventClass;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.sandius.rembulan.Table;

public enum EventType {
  CHAT {
    @Override
    public EventWrapper<?> wrap(Table env, Object evt) {
      return ServerChatEventClass.get().newInstance(env, (ServerChatEvent) evt);
    }
  }, //
  WHISPER {
    @Override
    public EventWrapper<?> wrap(Table env, Object evt) {
      return WhisperEventClass.get().newInstance(env, (WhisperEvent) evt);
    }
  }, //
  LEFT_CLICK {
    @Override
    public EventWrapper<?> wrap(Table env, Object evt) {
      return PlayerInteractEventClass.get().newInstance(env, (LeftClickBlock) evt,
          EventType.LEFT_CLICK);
    }
  }, //
  RIGHT_CLICK {
    @Override
    public EventWrapper<?> wrap(Table env, Object evt) {
      return PlayerInteractEventClass.get().newInstance(env, (RightClickBlock) evt,
          EventType.RIGHT_CLICK);
    }
  }, //
  PLAYER_JOINED {
    @Override
    public EventWrapper<?> wrap(Table env, Object evt) {
      return Player2EventClass.get().newInstance(env, (PlayerLoggedInEvent) evt,
          EventType.PLAYER_JOINED);
    }
  }, //
  PLAYER_LEFT {
    @Override
    public EventWrapper<?> wrap(Table env, Object evt) {
      return Player2EventClass.get().newInstance(env, (PlayerLoggedOutEvent) evt,
          EventType.PLAYER_LEFT);
    }
  }, //
  PLAYER_SPAWNED {
    @Override
    public EventWrapper<?> wrap(Table env, Object evt) {
      return Player2EventClass.get().newInstance(env, (PlayerRespawnEvent) evt,
          EventType.PLAYER_SPAWNED);
    }

  },//
  ;

  public abstract EventWrapper<?> wrap(Table env, Object evt);
}
