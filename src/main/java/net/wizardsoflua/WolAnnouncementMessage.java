package net.wizardsoflua.wol;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class WolAnnouncementMessage extends TextComponentTranslation {

  public WolAnnouncementMessage(String message) {
    super("chat.type.announcement", new Object[] {"WoL"});
    ITextComponent details = new TextComponentString(message);
    details.setStyle((new Style()).setColor(TextFormatting.WHITE));
    this.appendSibling(details);
    setStyle((new Style()).setColor(TextFormatting.GOLD));
  }
  
  public WolAnnouncementMessage() {
    super("chat.type.announcement", new Object[] {"WoL"});
    setStyle((new Style()).setColor(TextFormatting.GOLD));
  }

}
