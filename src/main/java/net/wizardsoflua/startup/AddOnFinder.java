package net.wizardsoflua.startup;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.Iterators;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.wizardsoflua.WolAnnouncementMessage;

public class AddOnFinder {

  private static final String WOL_AUTO_STARTUP = "Wol-Auto-Startup";
  private static final String MANIFEST_FILE = "META-INF/MANIFEST.MF";

  private final StartupModuleFinder startupModuleFinder = new StartupModuleFinder();
  private List<String> startupModules;

  public interface Context {
    Logger getLogger();

    MinecraftServer getServer();
  }

  private final Context context;

  public AddOnFinder(Context context) {
    this.context = checkNotNull(context, "context==null!");
  }

  private List<String> findStartupModules() {
    List<String> result = new ArrayList<>();
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Enumeration<URL> resources = classLoader.getResources(MANIFEST_FILE);
      Iterators.forEnumeration(resources).forEachRemaining(res -> {
        try {
          Manifest mf = new Manifest(res.openStream());
          if (isWolAddOn(mf)) {
            Map<String, String> env = new HashMap<>();
            try (FileSystem zipfs = FileSystems.newFileSystem(res.toURI(), env)) {
              Path rootDir = zipfs.getPath("/");
              sendMessage(
                  format("Searching WoL add-on at %s for startup modules", zipfs.toString()),
                  context.getServer());
              List<String> modules2 = startupModuleFinder.findStartupModulesIn(rootDir);
              result.addAll(modules2);
            }
          }
        } catch (IOException | URISyntaxException ex) {
          sendException(format(
              "Error while searching WoL add-ons for startup modules: can't read manifest in %s properly!",
              res.toExternalForm()), ex, context.getServer());
        }
      });
    } catch (IOException e) {
      sendException(
          format("Error while searching the classpath for manifest files: %s", e.getMessage()), e,
          context.getServer());
    }
    return result;
  }

  public List<String> getStartupModules() {
    if (startupModules == null) {
      startupModules = findStartupModules();
    }
    return startupModules;
  }

  private boolean isWolAddOn(Manifest manifest) {
    Attributes attr = manifest.getMainAttributes();
    String value = attr.getValue(WOL_AUTO_STARTUP);
    return value != null && Boolean.parseBoolean(value);
  }

  private void sendException(String message, Throwable t, ICommandSender commandSender) {
    context.getLogger().error(message, t);
    String stackTrace = getStackTrace(t);
    WolAnnouncementMessage txt = new WolAnnouncementMessage(message);
    TextComponentString details = new TextComponentString(stackTrace);
    txt.setStyle((new Style()).setColor(TextFormatting.RED).setBold(Boolean.valueOf(true)));
    txt.appendSibling(details);
    commandSender.sendMessage(txt);
  }

  private void sendMessage(String message, ICommandSender sender) {
    WolAnnouncementMessage txt = new WolAnnouncementMessage(message);
    sender.sendMessage(txt);
  }

  private String getStackTrace(Throwable throwable) {
    StringWriter writer = new StringWriter();
    throwable.printStackTrace(new PrintWriter(writer));
    String result = writer.toString();
    if (result.length() > 200) {
      result = result.substring(0, 200) + "...";
    }
    return result;
  }
}
