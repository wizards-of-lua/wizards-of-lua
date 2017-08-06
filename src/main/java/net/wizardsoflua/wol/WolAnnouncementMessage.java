package net.wizardsoflua.wol;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class WolAnnouncementMessage extends TextComponentTranslation {

  public WolAnnouncementMessage(String message) {
    super("chat.type.announcement", new Object[] {"WoL", message});
    setStyle((new Style()).setColor(TextFormatting.GOLD));
  }

}
