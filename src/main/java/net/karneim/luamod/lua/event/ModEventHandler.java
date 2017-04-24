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
import net.karneim.luamod.lua.classes.event.AnimationHandEventClass;
import net.karneim.luamod.lua.classes.event.ClickWindowEventClass;
import net.karneim.luamod.lua.classes.event.PlayerInteractEventClass;
import net.karneim.luamod.lua.classes.event.PlayerLoggedInEventClass;
import net.karneim.luamod.lua.classes.event.PlayerLoggedOutEventClass;
import net.karneim.luamod.lua.classes.event.PlayerRespawnEventClass;
import net.karneim.luamod.lua.classes.event.RightClickBlockEventClass;
import net.karneim.luamod.lua.classes.event.ServerChatEventClass;
import net.karneim.luamod.lua.classes.event.WhisperEventClass;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraftforge.event.ServerChatEvent;
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
  public void onChat(ServerChatEvent evt) {
    onEvent(ServerChatEventClass.class, evt);
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
            onEvent(AnimationHandEventClass.class,
                new AnimationHandEvent(player, animation.getHand()));
          }
        } else if (msg instanceof CPacketClickWindow) {
          EntityPlayer player = getPlayer(ctx);
          if (player != null) {
            CPacketClickWindow clickWindow = (CPacketClickWindow) msg;
            onEvent(ClickWindowEventClass.class, new ClickWindowEvent(player, clickWindow));
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

      private <E extends Event> void onEvent(Class<? extends ImmutableLuaClass<E>> luaClass,
          E event) {
        mod.getServer().addScheduledTask(() -> ModEventHandler.this.onEvent(luaClass, event));
      }
    };
    pipeline.addAfter("fml:packet_handler", "luamod:packet_handler", handler);
  }

  private <E extends Event> void onEvent(Class<? extends ImmutableLuaClass<E>> luaClass, E event) {
    onEvent(getModuleNameOf(luaClass), event);
  }

  private void onEvent(String eventType, Event event) {
    for (SpellEntity e : mod.getSpellRegistry().getAll()) {
      Events events = e.getEvents();
      if (events.getRegisteredEventTypes().contains(eventType)) {
        PatchedImmutableTable luaEvent = events.getRepo().wrap(event);
        events.handle(eventType, luaEvent);
      }
    }
  }

  @SubscribeEvent
  public void onLeftClickBlock(LeftClickBlock evt) {
    if (evt.getWorld().isRemote) {
      return;
    }
    onEvent(PlayerInteractEventClass.class, evt);
  }

  @SubscribeEvent
  public void onPlayerLoggedIn(PlayerLoggedInEvent evt) {
    onEvent(PlayerLoggedInEventClass.class, evt);
  }

  @SubscribeEvent
  public void onPlayerLoggedOut(PlayerLoggedOutEvent evt) {
    onEvent(PlayerLoggedOutEventClass.class, evt);
  }

  @SubscribeEvent
  public void onPlayerRespawn(PlayerRespawnEvent evt) {
    onEvent(PlayerRespawnEventClass.class, evt);
  }

  @SubscribeEvent
  public void onRightClickBlock(RightClickBlock evt) {
    if (evt.getWorld().isRemote) {
      return;
    }
    onEvent(RightClickBlockEventClass.class, evt);
  }

  public void onWhisper(WhisperEvent evt) {
    onEvent(WhisperEventClass.class, evt);
  }
}
