package net.karneim.luamod.lua.event;

import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.classes.event.AnimationHandEventClass;
import net.karneim.luamod.lua.classes.event.ClickWindowEventClass;
import net.karneim.luamod.lua.classes.event.PlayerEventClass;
import net.karneim.luamod.lua.classes.event.PlayerInteractEventClass;
import net.karneim.luamod.lua.classes.event.ServerChatEventClass;
import net.karneim.luamod.lua.classes.event.WhisperEventClass;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public enum EventType {
  CHAT {
    @Override
    public EventWrapper<?> wrap(LuaTypesRepo repo, Object evt) {
      return repo.get(ServerChatEventClass.class).newInstance((ServerChatEvent) evt);
    }
  }, //
  WHISPER {
    @Override
    public EventWrapper<?> wrap(LuaTypesRepo repo, Object evt) {
      return repo.get(WhisperEventClass.class).newInstance((WhisperEvent) evt);
    }
  }, //
  LEFT_CLICK {
    @Override
    public EventWrapper<?> wrap(LuaTypesRepo repo, Object evt) {
      return repo.get(PlayerInteractEventClass.class).newInstance((LeftClickBlock) evt,
          EventType.LEFT_CLICK);
    }
  }, //
  RIGHT_CLICK {
    @Override
    public EventWrapper<?> wrap(LuaTypesRepo repo, Object evt) {
      return repo.get(PlayerInteractEventClass.class).newInstance((RightClickBlock) evt,
          EventType.RIGHT_CLICK);
    }
  }, //
  PLAYER_JOINED {
    @Override
    public EventWrapper<?> wrap(LuaTypesRepo repo, Object evt) {
      return repo.get(PlayerEventClass.class).newInstance((PlayerLoggedInEvent) evt,
          EventType.PLAYER_JOINED);
    }
  }, //
  PLAYER_LEFT {
    @Override
    public EventWrapper<?> wrap(LuaTypesRepo repo, Object evt) {
      return repo.get(PlayerEventClass.class).newInstance((PlayerLoggedOutEvent) evt,
          EventType.PLAYER_LEFT);
    }
  }, //
  PLAYER_SPAWNED {
    @Override
    public EventWrapper<?> wrap(LuaTypesRepo repo, Object evt) {
      return repo.get(PlayerEventClass.class).newInstance((PlayerRespawnEvent) evt,
          EventType.PLAYER_SPAWNED);
    }

  }, //
  ANIMATION_HAND {
    @Override
    public EventWrapper<?> wrap(LuaTypesRepo repo, Object evt) {
      return repo.get(AnimationHandEventClass.class).newInstance((AnimationHandEvent) evt,
          EventType.ANIMATION_HAND);
    }
  }, //
  CLICK_WINDOW {
    @Override
    public EventWrapper<?> wrap(LuaTypesRepo repo, Object evt) {
      ClickWindowEventClass cls = repo.get(ClickWindowEventClass.class);
      return cls.newInstance((ClickWindowEvent) evt,
          EventType.CLICK_WINDOW);
    }
  },//
  ;

  public abstract EventWrapper<?> wrap(LuaTypesRepo repo, Object evt);
}
