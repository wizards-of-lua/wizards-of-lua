package net.wizardsoflua.testenv;

import static java.util.Optional.ofNullable;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.extension.InjectionScope;
import net.wizardsoflua.imc.TypedImc;
import net.wizardsoflua.imc.WizardsOfLuaConsumer;
import net.wizardsoflua.testenv.log4j.Log4j2ForgeEventBridge;
import net.wizardsoflua.testenv.net.ClientChatReceivedMessage;
import net.wizardsoflua.testenv.net.WolTestPacketChannel;

@Mod(WolTestMod.MODID)
@EventBusSubscriber(bus = Bus.MOD)
public class WolTestMod {
  public static final String MODID = "wol-test";

  private WizardsOfLua wol;
  private EventRecorder eventRecorder;
  private Log4j2ForgeEventBridge log4jEventBridge;

  public WolTestMod() {
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    bus.addListener(this::setup);
    bus.addListener(this::setupClient);
    bus.addListener(this::enqueueImcMessages);
  }

  private void setup(FMLCommonSetupEvent event) {
    WolTestPacketChannel.initialize();
    MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);

    eventRecorder = new EventRecorder();
    MinecraftForge.EVENT_BUS.register(getEventRecorder());

    log4jEventBridge = new Log4j2ForgeEventBridge();
    log4jEventBridge.activate();
  }

  private void onServerStarting(FMLServerStartingEvent event) {
    MinecraftServer server = event.getServer();
    InjectionScope serverScope = wol.provideServerScope(server);
    CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
    new TestCommand(this, serverScope).register(dispatcher);
  }

  private void setupClient(FMLClientSetupEvent event) {
    MinecraftForge.EVENT_BUS.addListener(this::onClientChatReceived);
  }

  private void onClientChatReceived(ClientChatReceivedEvent evt) {
    ITextComponent message = evt.getMessage();
    String txt = ofNullable(message).map(it -> it.getString()).orElse(null);
    WolTestPacketChannel.sendToServer(new ClientChatReceivedMessage(txt));
  }

  private void enqueueImcMessages(InterModEnqueueEvent event) {
    TypedImc.sendTo(WizardsOfLua.MODID, WizardsOfLuaConsumer.class, it -> wol = it);
  }

  public EventRecorder getEventRecorder() {
    return eventRecorder;
  }
}
