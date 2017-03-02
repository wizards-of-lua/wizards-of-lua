package net.karneim.luamod.lua.event;

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
      return new ServerChatEventWrapper(env, (ServerChatEvent) evt);
    }
  }, //
  WHISPER {
    @Override
    public EventWrapper<?> wrap(Table env, Object evt) {
      return new WhisperEventWrapper(env, (WhisperEvent) evt);
    }
  }, //
  LEFT_CLICK {
    @Override
    public EventWrapper<?> wrap(Table env, Object evt) {
      return new PlayerInteractEventWrapper<LeftClickBlock>(env, (LeftClickBlock) evt,
          EventType.LEFT_CLICK);
    }
  }, //
  RIGHT_CLICK {
    @Override
    public EventWrapper<?> wrap(Table env, Object evt) {
      return new PlayerInteractEventWrapper<RightClickBlock>(env, (RightClickBlock) evt,
          EventType.RIGHT_CLICK);
    }
  }, //
  PLAYER_JOINED {
    @Override
    public EventWrapper<?> wrap(Table env, Object evt) {
      return new Player2EventWrapper<PlayerLoggedInEvent>(env, (PlayerLoggedInEvent) evt,
          EventType.PLAYER_JOINED);
    }
  }, //
  PLAYER_LEFT {
    @Override
    public EventWrapper<?> wrap(Table env, Object evt) {
      return new Player2EventWrapper<PlayerLoggedOutEvent>(env, (PlayerLoggedOutEvent) evt,
          EventType.PLAYER_LEFT);
    }
  }, //
  PLAYER_SPAWNED {
    @Override
    public EventWrapper<?> wrap(Table env, Object evt) {
      return new Player2EventWrapper<PlayerRespawnEvent>(env, (PlayerRespawnEvent) evt,
          EventType.PLAYER_SPAWNED);
    }

  },//
  ;

  public abstract EventWrapper<?> wrap(Table env, Object evt);
}
