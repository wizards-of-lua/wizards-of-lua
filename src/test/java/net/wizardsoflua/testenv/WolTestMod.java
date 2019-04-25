package net.wizardsoflua.testenv;

import static java.util.Optional.ofNullable;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.testenv.log4j.Log4j2ForgeEventBridge;
import net.wizardsoflua.testenv.net.ClientChatReceivedMessage;
import net.wizardsoflua.testenv.net.WolTestPacketChannel;

@Mod(WolTestMod.MODID)
@EventBusSubscriber(bus = Bus.MOD)
public class WolTestMod {
  public static final String MODID = "wol-test";

  private WolTestPacketChannel packetChannel;
  private EventRecorder eventRecorder;
  private Log4j2ForgeEventBridge log4jEventBridge;

  public WolTestMod() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
  }

  public void setup(FMLCommonSetupEvent event) {
    packetChannel = new WolTestPacketChannel();
    MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);

    eventRecorder = new EventRecorder();
    MinecraftForge.EVENT_BUS.register(getEventRecorder());

    MinecraftForge.EVENT_BUS.register(new MainForgeEventBusListener());

    log4jEventBridge = new Log4j2ForgeEventBridge(Log4j2ForgeEventBridge.NET_MINECRAFT_LOGGER);
    log4jEventBridge.activate();
  }

  public void onServerStarting(FMLServerStartingEvent event) {
    MinecraftServer server = event.getServer();
    CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
    new TestCommand(this, server).register(dispatcher);
  }

  private class MainForgeEventBusListener {
    @SubscribeEvent
    public void onEvent(ClientChatReceivedEvent evt) {
      ITextComponent message = evt.getMessage();
      String txt = ofNullable(message).map(it -> it.getString()).orElse(null);
      packetChannel.sendToServer(new ClientChatReceivedMessage(txt));
    }
  }

  public WizardsOfLua getWol() {
    // FIXME Adrodoc 24.04.2019: Proper synchronisation using mod event bus
    return WizardsOfLua.instance;
  }

  public WolTestPacketChannel getPacketChannel() {
    return packetChannel;
  }

  public EventRecorder getEventRecorder() {
    return eventRecorder;
  }
}
