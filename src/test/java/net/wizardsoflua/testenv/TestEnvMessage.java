package net.wizardsoflua.testenv;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class TestEnvMessage extends TextComponentTranslation {

  public TestEnvMessage(String message) {
    super("chat.type.announcement", new Object[] {"WoL-TestEnv"});
    ITextComponent details = new TextComponentString(message);
    details.setStyle((new Style()).setColor(TextFormatting.WHITE));
    this.appendSibling(details);
    setStyle((new Style()).setColor(TextFormatting.GOLD));
  }
  
  public TestEnvMessage() {
    super("chat.type.announcement", new Object[] {"WoL-TestEnv"});
    setStyle((new Style()).setColor(TextFormatting.GOLD));
  }

}
