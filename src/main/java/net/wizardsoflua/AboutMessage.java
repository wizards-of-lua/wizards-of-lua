package net.wizardsoflua;

import static net.minecraftforge.common.ForgeHooks.newChatWithLinks;
import static net.wizardsoflua.WizardsOfLua.LOGGER;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.CheckResult;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.forgespi.language.IModInfo;
import net.wizardsoflua.config.WolConfig;
import net.wizardsoflua.extension.api.inject.PostConstruct;
import net.wizardsoflua.extension.api.inject.PreDestroy;

@Singleton
public class AboutMessage {
  private final Set<UUID> notifiedPlayers = new HashSet<>();
  @Inject
  private WolConfig config;

  @PostConstruct
  private void postConstruct() {
    MinecraftForge.EVENT_BUS.register(this);
  }

  @PreDestroy
  private void preDestroy() {
    MinecraftForge.EVENT_BUS.unregister(this);
  }

  @SubscribeEvent
  public void onFmlServerStarted(FMLServerStartedEvent event) {
    LOGGER.info(getTextComponent());
  }

  @SubscribeEvent
  public void onEvent(PlayerLoggedInEvent event) {
    if (config.getGeneralConfig().isShowAboutMessage()) {
      EntityPlayer player = event.getPlayer();
      if (notifiedPlayers.add(player.getUniqueID())) {
        player.sendMessage(getTextComponent());

        String recommendedVersion = getRecommendedVersion();
        if (recommendedVersion != null) {
          TextComponentString component =
              new TextComponentString("New version " + recommendedVersion + " available!");
          component.setStyle(new Style().setColor(TextFormatting.GREEN));
          player.sendMessage(new WolAnnouncementMessage(component));
        }

        WolAnnouncementMessage instructions = new WolAnnouncementMessage("See instructions at ");
        ITextComponent link = newChatWithLinks(WizardsOfLua.URL);
        link.getStyle().setColor(TextFormatting.YELLOW);
        instructions.appendSibling(link);

        player.sendMessage(instructions);
      }
    }
  }

  public ITextComponent getTextComponent() {
    WolAnnouncementMessage result = new WolAnnouncementMessage("Powered by the ");

    TextComponentString modName = new TextComponentString("Wizards of Lua");
    modName.setStyle(new Style().setColor(TextFormatting.GOLD));
    result.appendSibling(modName);

    TextComponentString version = new TextComponentString(" - " + WizardsOfLua.VERSION);
    version.setStyle(new Style().setColor(TextFormatting.WHITE));
    result.appendSibling(version);
    return result;
  }

  private @Nullable String getRecommendedVersion() {
    String result = null;
    IModInfo modInfo = ModList.get().getModFileById(WizardsOfLua.MODID).getMods().get(0);
    CheckResult checkResult = VersionChecker.getResult(modInfo);
    VersionChecker.Status status = checkResult.status;
    if (status == VersionChecker.Status.OUTDATED || status == VersionChecker.Status.BETA_OUTDATED) {
      result = checkResult.target.toString();
    }
    return result;
  }
}
