package net.wizardsoflua.testenv.client;

import net.minecraft.util.text.ITextComponent;
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
    ITextComponent message = evt.getMessage();
    String txt = message.getUnformattedText();
    WolTestEnvironment.instance.getPacketDispatcher().sendToServer(
        new ClientChatReceivedMessage(txt));
  }
}
