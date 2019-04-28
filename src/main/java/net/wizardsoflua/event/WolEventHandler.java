package net.wizardsoflua.event;

import static net.wizardsoflua.WizardsOfLua.LOGGER;
import javax.annotation.Nullable;
import javax.inject.Inject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
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
      return;
    }
    if (event instanceof ServerTickEvent) {
      onServerTickEvent((ServerTickEvent) event);
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

  // FIXME Adrodoc 24.04.2019: ServerTickEvent happens twice per tick,
  // once with Phase.START and once with Phase.END
  private void onServerTickEvent(ServerTickEvent event) {
    for (SpellEntity spellEntity : registry.getAll()) {
      if (spellEntity.isAlive()) {
        spellEntity.onUpdate();
      }
    }
  }

  @SubscribeEvent
  public void onPlayerRespawnEvent(PlayerRespawnEvent evt) {
    if (EffectiveSide.get() != LogicalSide.SERVER) {
      return;
    }
    EntityPlayerMP player = (EntityPlayerMP) evt.getPlayer();
    addWolPacketHandler(player);
    replacePlayerInstance(player);
  }

  @SubscribeEvent
  public void onPlayerLoggedIn(PlayerLoggedInEvent evt) {
    if (EffectiveSide.get() != LogicalSide.SERVER) {
      return;
    }
    EntityPlayerMP player = (EntityPlayerMP) evt.getPlayer();
    addWolPacketHandler(player);
    replacePlayerInstance(player);
  }

  // FIXME Adrodoc 28.04.2019: Use PlayerEvent.Clone instead of PlayerLoggedInEvent and
  // PlayerRespawnEvent
  private void replacePlayerInstance(EntityPlayerMP player) {
    for (SpellEntity spellEntity : registry.getAll()) {
      spellEntity.getProgram().replacePlayerInstance(player);
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
