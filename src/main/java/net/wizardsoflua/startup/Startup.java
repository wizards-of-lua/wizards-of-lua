package net.wizardsoflua.startup;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static net.wizardsoflua.CommandSourceUtils.sendAndLogFeedback;
import static net.wizardsoflua.WizardsOfLua.LOGGER;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.config.WolConfig;
import net.wizardsoflua.extension.api.inject.PostConstruct;
import net.wizardsoflua.extension.api.inject.PreDestroy;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.server.api.ServerScoped;
import net.wizardsoflua.lua.module.print.PrintRedirector.PrintReceiver;
import net.wizardsoflua.spell.SpellEntityFactory;

@ServerScoped
public class Startup {
  private static final Comparator<String> MODULE_COMPARATOR = new Comparator<String>() {
    @Override
    public int compare(String o1, String o2) {
      int levels1 = StringUtils.countMatches(o1, ".");
      int levels2 = StringUtils.countMatches(o2, ".");
      return levels1 - levels2;
    }
  };

  private final StartupModuleFinder startupModuleFinder = new StartupModuleFinder();
  @Resource
  private MinecraftServer server;
  @Inject
  private AddOnFinder addOnFinder;
  @Inject
  private SpellEntityFactory spellEntityFactory;
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
  public void onServerStarted(FMLServerStartedEvent event) {
    MinecraftServer server = event.getServer();
    if (this.server == server) {
      CommandSource commandSource = server.getCommandSource();
      runStartupSequence(commandSource);
    }
  }

  public void runStartupSequence(CommandSource source) {
    checkNotNull(source, "source == null!");
    sendMessage("Running startup sequence...", source);
    Path sharedLibDir = config.getSharedLibDir().toPath();
    try {
      List<String> modules = merge(startupModuleFinder.findStartupModulesIn(sharedLibDir),
          addOnFinder.getStartupModules());
      launchModules(source, modules);
    } catch (IOException e) {
      sendException(format("Error while searching %s for startup modules", sharedLibDir), e,
          source);
    }
    // FIXME also catch oher exception in order to show them to the caller?
  }

  private List<String> merge(List<String> modules1, List<String> modules2) {
    List<String> result = Lists.newArrayList(modules1);
    result.addAll(modules2);
    Collections.sort(result, MODULE_COMPARATOR);
    return result.stream().distinct().collect(Collectors.toList());
  }

  private void launchModules(CommandSource source, List<String> modules) {
    for (String module : modules) {
      launchModule(source, module);
    }
  }

  private void launchModule(CommandSource source, String module) {
    sendMessage(format("Launching module '%s'", module), source);
    String code = format("require('%s')", module);

    PrintReceiver printReceiver = message -> {
      sendAndLogFeedback(source, new TextComponentString(message));
    };

    spellEntityFactory.create(source, printReceiver, code);
  }

  private void sendException(String message, Throwable t, CommandSource source) {
    LOGGER.error(message, t);
    String stackTrace = getStackTrace(t);
    WolAnnouncementMessage txt = new WolAnnouncementMessage(message);
    TextComponentString details = new TextComponentString(stackTrace);
    txt.setStyle(new Style().setColor(TextFormatting.RED).setBold(Boolean.valueOf(true)));
    txt.appendSibling(details);
    sendErrorMessage(source, txt);
  }

  private String getStackTrace(Throwable throwable) {
    String result = Throwables.getStackTraceAsString(throwable);
    if (result.length() > 200) {
      result = result.substring(0, 200) + "...";
    }
    return result;
  }

  private void sendMessage(String message, CommandSource source) {
    sendFeedback(source, new WolAnnouncementMessage(message));
  }

  private void sendFeedback(CommandSource source, ITextComponent message) {
    source.sendFeedback(message, false);
    LOGGER.info(message.getString());
  }

  private void sendErrorMessage(CommandSource source, ITextComponent message) {
    source.sendErrorMessage(message);
    LOGGER.error(message.getString());
  }
}
