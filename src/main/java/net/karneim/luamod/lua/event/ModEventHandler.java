package net.karneim.luamod.lua.event;

import static net.karneim.luamod.lua.classes.LuaClass.getModuleNameOf;

import java.lang.reflect.Field;
import java.lang.reflect.UndeclaredThrowableException;

import javax.annotation.Nullable;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import net.karneim.luamod.LuaMod;
import net.karneim.luamod.lua.SpellEntity;
import net.karneim.luamod.lua.classes.event.EventClass;
import net.karneim.luamod.lua.classes.event.ServerChatEventClass;
import net.karneim.luamod.lua.classes.event.entity.EntityEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.LeftClickBlockEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.PlayerEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.PlayerInteractEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.RightClickBlockEventClass;
import net.karneim.luamod.lua.classes.event.game.PlayerGameEventClass;
import net.karneim.luamod.lua.classes.event.game.PlayerLoggedInEventClass;
import net.karneim.luamod.lua.classes.event.game.PlayerLoggedOutEventClass;
import net.karneim.luamod.lua.classes.event.game.PlayerRespawnEventClass;
import net.karneim.luamod.lua.classes.event.wol.AnimationHandEventClass;
import net.karneim.luamod.lua.classes.event.wol.ClickWindowEventClass;
import net.karneim.luamod.lua.classes.event.wol.WhisperEventClass;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;

public class ModEventHandler {
  private LuaMod mod;

  public ModEventHandler(LuaMod mod) {
    this.mod = mod;
  }

  @SubscribeEvent
  public void onClientConnected(ServerConnectionFromClientEvent evt) {
    NetworkManager networkManager = evt.getManager();
    Channel channel = networkManager.channel();
    ChannelPipeline pipeline = channel.pipeline();
    ChannelHandler handler = new ChannelInboundHandlerAdapter() {
      @Override
      public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof CPacketAnimation) {
          EntityPlayer player = getPlayer(ctx);
          if (player != null) {
            CPacketAnimation animation = (CPacketAnimation) msg;
            onLuaEvent(AnimationHandEventClass.class,
                new AnimationHandEvent(player, animation.getHand()));
          }
        } else if (msg instanceof CPacketClickWindow) {
          EntityPlayer player = getPlayer(ctx);
          if (player != null) {
            CPacketClickWindow clickWindow = (CPacketClickWindow) msg;
            onLuaEvent(ClickWindowEventClass.class, new ClickWindowEvent(player, clickWindow));
          }
        }
        super.channelRead(ctx, msg);
      }

      private EntityPlayer getEntityPlayer(NetworkDispatcher nx) {
        try {
          Field f = NetworkDispatcher.class.getDeclaredField("player");
          f.setAccessible(true);
          EntityPlayer result = (EntityPlayer) f.get(nx);
          return result;
        } catch (IllegalAccessException | NoSuchFieldException | SecurityException e) {
          throw new UndeclaredThrowableException(e);
        }
      }

      private @Nullable EntityPlayer getPlayer(ChannelHandlerContext ctx) {
        ChannelHandler xx = ctx.pipeline().get("fml:packet_handler");
        if (xx instanceof NetworkDispatcher) {
          NetworkDispatcher nx = (NetworkDispatcher) xx;
          return getEntityPlayer(nx);
        }
        return null;
      }

      private <E extends Event> void onLuaEvent(Class<? extends ImmutableLuaClass<E>> luaClass,
          E event) {
        mod.getServer().addScheduledTask(() -> ModEventHandler.this.onLuaEvent(luaClass, event));
      }
    };
    pipeline.addAfter("fml:packet_handler", "luamod:packet_handler", handler);
  }

  @SubscribeEvent
  public void onEvent(EntityEvent evt) {
    onLuaEvent(EntityEventClass.class, evt);
  }

  @SubscribeEvent
  public void onEvent(Event evt) {
    onLuaEvent(EventClass.class, evt);
  }

  @SubscribeEvent
  public void onEvent(LeftClickBlock evt) {
    if (evt.getWorld().isRemote) {
      return;
    }
    onLuaEvent(LeftClickBlockEventClass.class, evt);
  }

  @SubscribeEvent
  public void onEvent(LivingEvent evt) {
    onLuaEvent(LivingEventClass.class, evt);
  }

  @SubscribeEvent
  public void onEvent(net.minecraftforge.fml.common.gameevent.PlayerEvent evt) {
    onLuaEvent(PlayerGameEventClass.class, evt);
  }

  @SubscribeEvent
  public void onEvent(PlayerEvent evt) {
    onLuaEvent(PlayerEventClass.class, evt);
  }

  @SubscribeEvent
  public void onEvent(PlayerInteractEvent evt) {
    if (evt.getWorld().isRemote) {
      return;
    }
    onLuaEvent(PlayerInteractEventClass.class, evt);
  }

  @SubscribeEvent
  public void onEvent(PlayerLoggedInEvent evt) {
    onLuaEvent(PlayerLoggedInEventClass.class, evt);
  }

  @SubscribeEvent
  public void onEvent(PlayerLoggedOutEvent evt) {
    onLuaEvent(PlayerLoggedOutEventClass.class, evt);
  }

  @SubscribeEvent
  public void onEvent(PlayerRespawnEvent evt) {
    onLuaEvent(PlayerRespawnEventClass.class, evt);
  }

  @SubscribeEvent
  public void onEvent(RightClickBlock evt) {
    if (evt.getWorld().isRemote) {
      return;
    }
    onLuaEvent(RightClickBlockEventClass.class, evt);
  }

  @SubscribeEvent
  public void onEvent(ServerChatEvent evt) {
    onLuaEvent(ServerChatEventClass.class, evt);
  }

  public void onEvent(WhisperEvent evt) {
    onLuaEvent(WhisperEventClass.class, evt);
  }

  private <E extends Event> void onLuaEvent(Class<? extends ImmutableLuaClass<E>> luaClass,
      E event) {
    onLuaEvent(getModuleNameOf(luaClass), event);
  }

  private void onLuaEvent(String eventType, Event event) {
    for (SpellEntity e : mod.getSpellRegistry().getAll()) {
      Events events = e.getEvents();
      if (events.getRegisteredEventTypes().contains(eventType)) {
        PatchedImmutableTable luaEvent = events.getRepo().wrap(event);
        events.handle(eventType, luaEvent);
      }
    }
  }
}
