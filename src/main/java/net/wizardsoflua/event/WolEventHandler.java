package net.wizardsoflua.event;

import static net.wizardsoflua.WizardsOfLua.LOGGER;
import javax.annotation.Nullable;
import javax.inject.Inject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.extension.api.inject.PostConstruct;
import net.wizardsoflua.extension.api.inject.PreDestroy;
import net.wizardsoflua.extension.server.api.ServerScoped;
import net.wizardsoflua.extension.spell.spi.JavaToLuaConverter;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.module.events.EventsModule;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.spell.SpellRegistry;

@ServerScoped
public class WolEventHandler {
  private static final String WOL_PACKET_HANDLER_NAME = WizardsOfLua.MODID + ":packet_handler";
  private static final String VANILLA_PACKET_HANDLER_NAME = "packet_handler";
  @Inject
  private SpellRegistry registry;

  @PostConstruct
  private void postConstruct() {
    MinecraftForge.EVENT_BUS.register(this);
  }

  @PreDestroy
  private void preDestroy() {
    MinecraftForge.EVENT_BUS.unregister(this);
  }

  @SubscribeEvent
  public void onEvent(Event event) {
    if (EffectiveSide.get() != LogicalSide.SERVER) {
      return; // This early exit is just to improve performance
    }
    if (Converters.isSupported(event.getClass())) {
      for (SpellEntity spellEntity : registry.getAll()) {
        String eventName = getEventName(event, spellEntity);
        if (eventName != null) {
          EventsModule events = spellEntity.getProgram().getEvents();
          events.onEvent(eventName, event);
        }
      }
    }
  }

  private @Nullable String getEventName(Event event, SpellEntity spell) {
    if (event instanceof CustomLuaEvent) {
      return ((CustomLuaEvent) event).getName();
    }
    Converters converters = spell.getProgram().getConverters();
    JavaToLuaConverter<?> converter = converters.getJavaToLuaConverter(event.getClass());
    if (converter != null) {
      return converter.getName();
    }
    return null;
  }

  @SubscribeEvent
  public void onServerTick(ServerTickEvent event) {
    if (event.phase == Phase.START) {
      for (SpellEntity spellEntity : registry.getAll()) {
        if (spellEntity.isAlive()) {
          spellEntity.onUpdate();
        }
      }
    }
  }

  @SubscribeEvent
  public void onPlayerClone(PlayerEvent.Clone event) {
    EntityPlayer oldPlayer = event.getOriginal();
    EntityPlayer newPlayer = event.getEntityPlayer();
    if (oldPlayer instanceof EntityPlayerMP && newPlayer instanceof EntityPlayerMP) {
      EntityPlayerMP oldMultiPlayer = (EntityPlayerMP) oldPlayer;
      EntityPlayerMP newMultiPlayer = (EntityPlayerMP) newPlayer;
      for (SpellEntity spellEntity : registry.getAll()) {
        // TODO Adrodoc 28.04.2019: Move this into an event listener in the program
        spellEntity.getProgram().replacePlayer(oldMultiPlayer, newMultiPlayer);
      }

      addWolPacketHandler(newMultiPlayer);
    }
  }

  @SubscribeEvent
  public void onPlayerLoggedIn(PlayerLoggedInEvent evt) {
    EntityPlayer player = evt.getPlayer();
    if (player instanceof EntityPlayerMP) {
      EntityPlayerMP multiPlayer = (EntityPlayerMP) player;
      addWolPacketHandler(multiPlayer);
    }
  }

  private void addWolPacketHandler(EntityPlayerMP player) {
    NetworkManager networkManager = player.connection.getNetworkManager();
    Channel channel = networkManager.channel();
    ChannelPipeline pipeline = channel.pipeline();
    WolChannelInboundHandlerAdapter handler = pipeline.get(WolChannelInboundHandlerAdapter.class);
    if (handler == null) {
      if (pipeline.get(VANILLA_PACKET_HANDLER_NAME) != null) {
        handler = new WolChannelInboundHandlerAdapter(player);
        pipeline.addBefore(VANILLA_PACKET_HANDLER_NAME, WOL_PACKET_HANDLER_NAME, handler);
      } else {
        LOGGER.error("Can't add WolPacketHandler: vanilla packet handler '"
            + VANILLA_PACKET_HANDLER_NAME + "' not found!");
        throw new RuntimeException("Can't add WolPacketHandler!");
      }
    } else {
      // This is essential when a player respawns.
      handler.setPlayer(player);
    }
  }
}
