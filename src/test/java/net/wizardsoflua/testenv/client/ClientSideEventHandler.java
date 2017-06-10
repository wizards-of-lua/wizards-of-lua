package net.wizardsoflua.testenv.client;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.wizardsoflua.testenv.WolTestEnvironment;
import net.wizardsoflua.testenv.net.ClientChatReceivedMessage;

@SideOnly(Side.CLIENT)
public class ClientSideEventHandler {
  
  @SubscribeEvent
  public void onEvent(ClientChatReceivedEvent evt) {
    WolTestEnvironment.instance.getPacketPipeline().sendToServer(
        new ClientChatReceivedMessage(evt.getMessage().getUnformattedComponentText()));
  }
}
