package net.wizardsoflua.testenv;

import org.assertj.core.api.Assertions;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;

public class TestDataFactory extends Assertions {

  protected ServerChatEvent newServerChatEvent(EntityPlayerMP player, String message) {
    ITextComponent component = new TextComponentString(message);
    return new ServerChatEvent(player, message, component);
  }
  
}
