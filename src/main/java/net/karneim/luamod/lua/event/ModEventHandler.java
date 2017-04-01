package net.karneim.luamod.lua.event;

import java.lang.reflect.Field;
import java.lang.reflect.UndeclaredThrowableException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import net.karneim.luamod.LuaMod;
import net.karneim.luamod.lua.SpellEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
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
  public void onCommand(CommandEvent evt) {
    // System.out.println("onCommand: " + evt.getCommand().getCommandName() + " "
    // + Arrays.toString(evt.getParameters()));
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
          ChannelHandler xx = ctx.pipeline().get("fml:packet_handler");
          if (xx instanceof NetworkDispatcher) {
            NetworkDispatcher nx = (NetworkDispatcher) xx;
            EntityPlayer p = getEntityPlayer(nx);
            AnimationHandEvent evt = new AnimationHandEvent((CPacketAnimation) msg, p);
            mod.getServer().addScheduledTask(new Runnable() {
              @Override
              public void run() {
                onEvent(EventType.ANIMATION_HAND, evt);
              }
            });
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
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
            | SecurityException e) {
          throw new UndeclaredThrowableException(e);
        }
      }
    };
    pipeline.addAfter("fml:packet_handler", "luamod:packet_handler", handler);
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
    for (SpellEntity e : mod.getSpellRegistry().getAll()) {
      e.getEvents().handle(type, evt);
    }
  }

}
