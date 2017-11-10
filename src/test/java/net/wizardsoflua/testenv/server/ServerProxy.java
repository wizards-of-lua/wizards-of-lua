package net.wizardsoflua.testenv.server;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.wizardsoflua.testenv.CommonProxy;
import net.wizardsoflua.testenv.WolTestEnvironment;
import net.wizardsoflua.testenv.log4j.Log4j2ForgeEventBridge;
import net.wizardsoflua.testenv.net.ConfigMessage;

@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy {

  public static final String NET_MINECRAFT_LOGGER = "net.minecraft";
  private final Log4j2ForgeEventBridge log4jEventBridge =
      new Log4j2ForgeEventBridge(NET_MINECRAFT_LOGGER);

  @Override
  public void onInit(FMLInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.register(WolTestEnvironment.instance.getEventRecorder());
    log4jEventBridge.activate();
  }

  @SubscribeEvent
  public void onEvent(PlayerLoggedInEvent evt) {
    //if (WolTestEnvironment.instance.getTestPlayer() == null) {
      EntityPlayerMP player = (EntityPlayerMP) evt.player;
      WolTestEnvironment.instance.setTestPlayer(player);
      makeOperator(player);
      ConfigMessage message = new ConfigMessage(WolTestEnvironment.VERSION);
      WolTestEnvironment.instance.getPacketDispatcher().sendTo(message, player);
    //}
  }
  
  @SubscribeEvent
  public void onEvent(PlayerRespawnEvent evt) {
    EntityPlayerMP player = (EntityPlayerMP) evt.player;
    WolTestEnvironment.instance.setTestPlayer(player);
    makeOperator(player);
    ConfigMessage message = new ConfigMessage(WolTestEnvironment.VERSION);
    WolTestEnvironment.instance.getPacketDispatcher().sendTo(message, player);
  }

  @SubscribeEvent
  public void onEvent(PlayerLoggedOutEvent evt) {
    EntityPlayerMP testPlayer = WolTestEnvironment.instance.getTestPlayer();
    if (testPlayer != null && testPlayer == evt.player) {
      WolTestEnvironment.instance.setTestPlayer(null);
    }
  }

  private void makeOperator(EntityPlayerMP player) {
    MinecraftServer server = WolTestEnvironment.instance.getServer();
    GameProfile gameprofile =
        server.getPlayerProfileCache().getGameProfileForUsername(player.getName());
    server.getPlayerList().addOp(gameprofile);
  }

}
