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

    String getName();

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
      }
    }
  }

  public ITextComponent getTextComponent() {
    String message = String.format(
        "%s, Version %s - " + "Please note that this is an early Alpha version!"
            + " For sending suggestions and bug reports please visit ",
        context.getName(), context.getVersion());
    WolAnnouncementMessage result = new WolAnnouncementMessage(message);
    result.appendSibling(newChatWithLinks(context.getUrl()));
    String recommendedVersion = context.getRecommendedVersion();
    if (recommendedVersion != null) {
      TextComponentString component =
          new TextComponentString("\n New version " + recommendedVersion + " available!");
      component.setStyle((new Style()).setColor(TextFormatting.GREEN));
      result.appendSibling(component);
    }
    return result;
  }

}
