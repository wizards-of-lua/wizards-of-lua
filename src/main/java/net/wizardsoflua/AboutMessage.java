package net.wizardsoflua;

import static net.minecraftforge.common.ForgeHooks.newChatWithLinks;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class AboutMessage {
  public interface Context {
    boolean shouldShowAboutMessage();

    String getUrl();

    String getVersion();

    String getRecommendedVersion();
  }

  private final Context context;
  private final Set<UUID> notifiedPlayers = new HashSet<>();

  public AboutMessage(Context context) {
    this.context = context;
  }

  @Override
  public String toString() {
    return getTextComponent().getUnformattedText();
  }

  @SubscribeEvent
  public void onEvent(PlayerLoggedInEvent event) {
    if (context.shouldShowAboutMessage()) {
      if (notifiedPlayers.add(event.player.getUniqueID())) {
        event.player.sendMessage(getTextComponent());

        String recommendedVersion = context.getRecommendedVersion();
        if (recommendedVersion != null) {
          TextComponentString component =
              new TextComponentString("New version " + recommendedVersion + " available!");
          component.setStyle((new Style()).setColor(TextFormatting.GREEN));
          event.player.sendMessage(new WolAnnouncementMessage(component));
        }

        WolAnnouncementMessage instructions = new WolAnnouncementMessage("See instructions at ");
        ITextComponent link = newChatWithLinks(context.getUrl());
        link.getStyle().setColor(TextFormatting.YELLOW);
        instructions.appendSibling(link);

        event.player.sendMessage(instructions);
      }
    }
  }

  public ITextComponent getTextComponent() {
    WolAnnouncementMessage result = new WolAnnouncementMessage("Powered by the ");

    TextComponentString modName = new TextComponentString("Wizards of Lua");
    modName.setStyle((new Style()).setColor(TextFormatting.GOLD));
    result.appendSibling(modName);

    TextComponentString version = new TextComponentString(" - " + context.getVersion());
    version.setStyle((new Style()).setColor(TextFormatting.WHITE));
    result.appendSibling(version);
    return result;
  }

}
