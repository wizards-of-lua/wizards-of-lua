package net.wizardsoflua.testenv;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.testenv.log4j.Log4j2ForgeEventBridge;
import net.wizardsoflua.testenv.net.ClientChatReceivedMessage;
import net.wizardsoflua.testenv.net.WolTestPacketChannel;

@Mod(WolTestEnvironment.MODID)
public class WolTestEnvironment {
  public static final String MODID = "wol-testenv";
  public static final String VERSION = WizardsOfLua.VERSION;

  // TODO Adrodoc 22.04.2019: Nicht statische Variante wäre besser
  public static WolTestEnvironment instance;

  private static final Logger logger = LogManager.getLogger();
  private final EventRecorder eventRecorder = new EventRecorder();
  private volatile WolTestPacketChannel packetChannel;
  private AtomicReference<EntityPlayerMP> testPlayer = new AtomicReference<>();

  private MinecraftServer server;

  private final Log4j2ForgeEventBridge log4jEventBridge =
      new Log4j2ForgeEventBridge(Log4j2ForgeEventBridge.NET_MINECRAFT_LOGGER);

  public WolTestEnvironment() {
    instance = this;
    FMLJavaModLoadingContext.get().getModEventBus().register(new ModSpecificEventBusHandling());
    MinecraftForge.EVENT_BUS.register(new MainForgeEventBusListener());
    MinecraftForge.EVENT_BUS.register(WolTestEnvironment.instance.getEventRecorder());
    log4jEventBridge.activate();
  }

  private class ModSpecificEventBusHandling {
    @SubscribeEvent
    public void onFmlCommonSetup(FMLCommonSetupEvent event) {
      packetChannel = new WolTestPacketChannel();
    }
  }
  private class MainForgeEventBusListener {
    @SubscribeEvent
    public void onFmlServerStarting(FMLServerStartingEvent event) {
      server = checkNotNull(event.getServer());
      CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
      new TestCommand().register(dispatcher);
    }

    @SubscribeEvent
    public void onEvent(ClientChatReceivedEvent evt) {
      ITextComponent message = evt.getMessage();
      String txt = ofNullable(message).map(it -> it.getString()).orElse(null);
      getPacketChannel().sendToServer(new ClientChatReceivedMessage(txt));
    }

    @SubscribeEvent
    public void onEvent(PlayerLoggedInEvent evt) {
      EntityPlayerMP player = (EntityPlayerMP) evt.getPlayer();
      setTestPlayer(player);
      MinecraftServer server = getServer();
      server.getPlayerList().addOp(player.getGameProfile());
    }

    @SubscribeEvent
    public void onEvent(PlayerRespawnEvent evt) {
      EntityPlayerMP player = (EntityPlayerMP) evt.getPlayer();
      setTestPlayer(player);
      MinecraftServer server = getServer();
      server.getPlayerList().addOp(player.getGameProfile());
    }

    @SubscribeEvent
    public void onEvent(PlayerLoggedOutEvent evt) {
      EntityPlayerMP testPlayer = getTestPlayer();
      if (testPlayer != null && testPlayer == evt.getPlayer()) {
        setTestPlayer(null);
      }
    }
  }

  public WolTestPacketChannel getPacketChannel() {
    return packetChannel;
  }

  public MinecraftServer getServer() {
    return server;
  }

  public WizardsOfLua getWol() {
    return WizardsOfLua.instance;
  }

  public @Nullable EntityPlayerMP getTestPlayer() {
    return testPlayer.get();
  }

  public void setTestPlayer(EntityPlayerMP player) {
    if (player != null) {
      logger.info("Setting testPlayer to Player: " + player.getName().getString());
    } else {
      logger.info("Setting testPlayer to null");
    }
    testPlayer.set(player);
  }

  public EventRecorder getEventRecorder() {
    return eventRecorder;
  }

  public void runAndWait(Runnable runnable) {
    MinecraftServer server = getServer();
    ListenableFuture<Object> future = server.addScheduledTask(runnable);
    try {
      future.get(30, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      throw new RuntimeException(e);
    }
  }

  public <V> V runAndWait(Callable<V> callable) {
    MinecraftServer server = getServer();
    ListenableFuture<V> future = server.callFromMainThread(callable);
    try {
      return future.get(30, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      throw new RuntimeException(e);
    }
  }
}
