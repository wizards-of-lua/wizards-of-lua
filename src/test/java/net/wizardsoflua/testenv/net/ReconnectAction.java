package net.wizardsoflua.testenv.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketLoginStart;

public class ReconnectAction extends ClientAction {

  public ReconnectAction() {}

  @Override
  protected void read(PacketBuffer buffer) throws IOException {}

  @Override
  protected void write(PacketBuffer buffer) throws IOException {}

  @Override
  public void handleClientSide(EntityPlayer player) {
    System.out.println("reconnect");

    NetHandlerPlayClient c = ((EntityPlayerSP) player).connection;
    NetworkManager nm = c.getNetworkManager();
    InetSocketAddress addr = (InetSocketAddress) nm.channel().remoteAddress();

    // disconnect
    Minecraft.getMinecraft().addScheduledTask(() -> {
      nm.closeChannel(null);
    });

    // connect
    new Thread(() -> {
      sleep(1000);
      Minecraft.getMinecraft().addScheduledTask(() -> {
        String ip = addr.getHostString();
        int port = addr.getPort();
        try {
          connect(ip, port);
        } catch (UnknownHostException e) {
          throw new RuntimeException(e);
        }
      });
    }).start();
  }

  // @SideOnly(Side.CLIENT)
  private void connect(String ip, int port) throws UnknownHostException {
    InetAddress inetaddress = InetAddress.getByName(ip);
    Minecraft minecraft = Minecraft.getMinecraft();
    boolean isUsingNativeTransport = minecraft.gameSettings.isUsingNativeTransport();
    GuiScreen previousGuiScreen = null;
    // The following code is copied from
    // net.minecraft.client.multiplayer.GuiConnecting.connect(String, int)
    NetworkManager networkManager =
        NetworkManager.createNetworkManagerAndConnect(inetaddress, port, isUsingNativeTransport);
    networkManager
        .setNetHandler(new NetHandlerLoginClient(networkManager, minecraft, previousGuiScreen));
    networkManager.sendPacket(new C00Handshake(ip, port, EnumConnectionState.LOGIN, true));
    networkManager.sendPacket(new CPacketLoginStart(minecraft.getSession().getProfile()));
  }

  private void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
