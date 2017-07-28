package net.karneim.luamod.lua.event;

import static net.karneim.luamod.lua.classes.LuaClass.getModuleNameOf;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import net.karneim.luamod.LuaMod;
import net.karneim.luamod.lua.SpellEntity;
import net.karneim.luamod.lua.SpellRegistry;
import net.karneim.luamod.lua.classes.GlobalLuaTypesRepo;
import net.karneim.luamod.lua.classes.LuaClass;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.classes.event.wol.AnimationHandEventClass;
import net.karneim.luamod.lua.classes.event.wol.ClickWindowEventClass;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.relauncher.Side;

public class ModEventHandler {
  private final GlobalLuaTypesRepo globalLuaTypesRepo;
  private final SpellRegistry spellRegistry;

  public ModEventHandler(GlobalLuaTypesRepo globalLuaTypesRepo, SpellRegistry spellRegistry) {
    this.globalLuaTypesRepo = globalLuaTypesRepo;
    this.spellRegistry = spellRegistry;
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
            MinecraftServer mcSserver = player.getServer();
            CPacketAnimation animation = (CPacketAnimation) msg;
            onLuaEvent(mcSserver, AnimationHandEventClass.class,
                new AnimationHandEvent(player, animation.getHand()));
          }
        } else if (msg instanceof CPacketClickWindow) {
          EntityPlayer player = getPlayer(ctx);
          if (player != null) {
            MinecraftServer mcSserver = player.getServer();
            CPacketClickWindow clickWindow = (CPacketClickWindow) msg;
            onLuaEvent(mcSserver, ClickWindowEventClass.class,
                new ClickWindowEvent(player, clickWindow));
          }
        }
        super.channelRead(ctx, msg);
      }

      private EntityPlayer getEntityPlayer(NetworkDispatcher nx) {
        // try {
        // Field f = NetworkDispatcher.class.getDeclaredField("player");
        // f.setAccessible(true);
        // EntityPlayer result = (EntityPlayer) f.get(nx);
        // return result;
        // } catch (IllegalAccessException | NoSuchFieldException | SecurityException e) {
        // throw new UndeclaredThrowableException(e);
        // }
        INetHandler netHandler = nx.getNetHandler();
        if (netHandler instanceof NetHandlerPlayServer) {
          NetHandlerPlayServer serverHandler = (NetHandlerPlayServer) netHandler;
          return serverHandler.playerEntity;
        } else {
          throw new IllegalStateException(
              "Expected NetHandlerPlayServer, but got " + netHandler.getClass().getName());
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

      private <E extends Event> void onLuaEvent(MinecraftServer mcSserver,
          Class<? extends DelegatingLuaClass<E>> luaClass, E event) {
        mcSserver.addScheduledTask(() -> ModEventHandler.this.onLuaEvent(luaClass, event));
      }
    };
    pipeline.addAfter("fml:packet_handler", "luamod:packet_handler", handler);
  }
  
//  @SubscribeEvent
//  public void onEvent(AchievementEvent evt) {
//    onLuaEvent(AchievementEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(AnvilRepairEvent evt) {
//    onLuaEvent(AnvilRepairEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(AttackEntityEvent evt) {
//    onLuaEvent(AttackEntityEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(BonemealEvent evt) {
//    onLuaEvent(BonemealEventClass.class, evt);
//  }
//
//  public void onEvent(CustomLuaEvent evt) {
//    onLuaEvent(evt.getType(), evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(EntityEvent evt) {
//    onLuaEvent(EntityEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(EntityItemPickupEvent evt) {
//    onLuaEvent(EntityItemPickupEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(FillBucketEvent evt) {
//    onLuaEvent(FillBucketEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(ItemEvent evt) {
//    onLuaEvent(ItemEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(ItemExpireEvent evt) {
//    onLuaEvent(ItemExpireEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(ItemTossEvent evt) {
//    onLuaEvent(ItemTossEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(LeftClickBlock evt) {
//    onLuaEvent(LeftClickBlockEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(LivingAttackEvent evt) {
//    onLuaEvent(LivingAttackEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(LivingDeathEvent evt) {
//    onLuaEvent(LivingDeathEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(LivingDropsEvent evt) {
//    onLuaEvent(LivingDropsEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(LivingEntityUseItemEvent.Finish evt) {
//    onLuaEvent(LivingEntityUseItemFinishEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(LivingEntityUseItemEvent.Start evt) {
//    onLuaEvent(LivingEntityUseItemStartEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(LivingEntityUseItemEvent.Stop evt) {
//    onLuaEvent(LivingEntityUseItemStopEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(LivingEntityUseItemEvent.Tick evt) {
//    onLuaEvent(LivingEntityUseItemTickEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(LivingEvent evt) {
//    onLuaEvent(LivingEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(LivingSpawnEvent evt) {
//    onLuaEvent(LivingSpawnEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(MinecartCollisionEvent evt) {
//    onLuaEvent(MinecartCollisionEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(MinecartEvent evt) {
//    onLuaEvent(MinecartEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(MinecartInteractEvent evt) {
//    onLuaEvent(MinecartInteractEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(net.minecraftforge.fml.common.gameevent.PlayerEvent evt) {
//    onLuaEvent(PlayerGameEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(PlayerEvent evt) {
//    onLuaEvent(PlayerEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(PlayerInteractEvent evt) {
//    onLuaEvent(PlayerInteractEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(PlayerLoggedInEvent evt) {
//    onLuaEvent(PlayerLoggedInEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(PlayerLoggedOutEvent evt) {
//    onLuaEvent(PlayerLoggedOutEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(PlayerRespawnEvent evt) {
//    onLuaEvent(PlayerRespawnEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(PotionBrewEvent evt) {
//    onLuaEvent(PotionBrewEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(PotionBrewEvent.Post evt) {
//    onLuaEvent(PotionBrewPostEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(PotionBrewEvent.Pre evt) {
//    onLuaEvent(PotionBrewPreEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(RightClickBlock evt) {
//    onLuaEvent(RightClickBlockEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(ServerChatEvent evt) {
//    onLuaEvent(ServerChatEventClass.class, evt);
//  }
//
//  @SubscribeEvent
//  public void onEvent(SpecialSpawn evt) {
//    onLuaEvent(SpecialSpawnEventClass.class, evt);
//  }
//
//  public void onEvent(WhisperEvent evt) {
//    onLuaEvent(WhisperEventClass.class, evt);
//  }

//  private <E extends Event> void onLuaEvent(Class<? extends DelegatingLuaClass<E>> luaClass,
//      E event) {
//    onLuaEvent(getModuleNameOf(luaClass), event);
//  }
  
  private final Set<Class<?>> warnings = new HashSet<>();
  
  // TODO instead register only specific events to possibly increase performance
  @SubscribeEvent
  public void onEvent(Event evt) {
    Class<? extends LuaClass> luaClass = globalLuaTypesRepo.getLuaClass(evt.getClass());
    if ( luaClass != null) {
      onLuaEvent(luaClass, evt);
    } else {
      // just warn about this the first time
      if (warnings.add(evt.getClass())) {
        LuaMod.instance.logger.warn("No lua class registered for event of type "+evt.getClass());
      }
    }
  }
  
  private <E extends Event> void onLuaEvent(Class<? extends LuaClass> luaClass,
      E event) {
    onLuaEvent(getModuleNameOf(luaClass), event);
  }

  public void onLuaEvent(String eventType, Event event) {
    if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
      return;
    }
    for (SpellEntity e : spellRegistry.getAll()) {
      Events events = e.getEvents();
      if (events.getRegisteredEventTypes().contains(eventType)) {
        LuaTypesRepo repo = events.getRepo();
        events.handle(eventType, repo.wrap(event));
      }
    }
  }
}
