package net.wizardsoflua.testenv.net;

import java.net.InetSocketAddress;
import com.google.auto.service.AutoService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConnecting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

@AutoService(NetworkMessage.class)
public class ReconnectMessage implements NetworkMessage {
  @Override
  public void decode(PacketBuffer buffer) {}

  @Override
  public void encode(PacketBuffer buffer) {}

  @Override
  public void handle(NetworkEvent.Context context) {
    DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> ClientProxy.handle(context));
  }

  @OnlyIn(Dist.CLIENT)
  private static class ClientProxy {
    private static void handle(NetworkEvent.Context context) {
      context.enqueueWork(() -> {
        Minecraft minecraft = Minecraft.getInstance();
        NetworkManager networkManager = minecraft.player.connection.getNetworkManager();
        networkManager.closeChannel(null); // disconnect

        InetSocketAddress remoteAddress = (InetSocketAddress) networkManager.getRemoteAddress();
        GuiScreen parent = minecraft.currentScreen;
        String hostName = remoteAddress.getHostName();
        int port = remoteAddress.getPort();
        minecraft.displayGuiScreen(new GuiConnecting(parent, minecraft, hostName, port)); // reconnect
      });
    }
  }

  // @Override
  // public void handleClientSide(EntityPlayer player) {
  // System.out.println("reconnect");
  //
  // NetHandlerPlayClient c = ((EntityPlayerSP) player).connection;
  // NetworkManager nm = c.getNetworkManager();
  // InetSocketAddress addr = (InetSocketAddress) nm.channel().remoteAddress();
  //
  // // disconnect
  // Minecraft.getMinecraft().addScheduledTask(() -> {
  // nm.closeChannel(null);
  // });
  //
  // // connect
  // new Thread(() -> {
  // sleep(1000);
  // Minecraft.getMinecraft().addScheduledTask(() -> {
  // String ip = addr.getHostString();
  // int port = addr.getPort();
  // try {
  // connect(ip, port);
  // } catch (UnknownHostException e) {
  // throw new RuntimeException(e);
  // }
  // });
  // }).start();
  // }
  //
  // // @SideOnly(Side.CLIENT)
  // private void connect(String ip, int port) throws UnknownHostException {
  // InetAddress inetaddress = InetAddress.getByName(ip);
  // Minecraft minecraft = Minecraft.getMinecraft();
  // boolean isUsingNativeTransport = minecraft.gameSettings.isUsingNativeTransport();
  // GuiScreen previousGuiScreen = null;
  // // The following code is copied from
  // // net.minecraft.client.multiplayer.GuiConnecting.connect(String, int)
  // NetworkManager networkManager =
  // NetworkManager.createNetworkManagerAndConnect(inetaddress, port, isUsingNativeTransport);
  // networkManager
  // .setNetHandler(new NetHandlerLoginClient(networkManager, minecraft, previousGuiScreen));
  // networkManager.sendPacket(new C00Handshake(ip, port, EnumConnectionState.LOGIN, true));
  // networkManager.sendPacket(new CPacketLoginStart(minecraft.getSession().getProfile()));
  // }
  //
  // private void sleep(long millis) {
  // try {
  // Thread.sleep(millis);
  // } catch (InterruptedException e) {
  // throw new RuntimeException(e);
  // }
  // }
  //
}
