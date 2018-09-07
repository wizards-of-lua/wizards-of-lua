package net.wizardsoflua.addon;

import static java.lang.String.format;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.Iterators;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.wizardsoflua.WolAnnouncementMessage;

/**
 * The {@link AddOnLauncher} casts startup spells defined by WoL add-ons.
 * 
 * <p>
 * It searches all manifest files in the class path for {@value #WOL_STARTUP_MODULE_KEY} entries. If
 * found, a lua command with the following code is executed: <code> 
 * {@value #LUA_COMMAND}
 * </code> where %s is replaced with the entry's value.
 */
public class AddOnLauncher {
  private static final String WOL_STARTUP_MODULE_KEY = "Wol-Startup-Module";
  private static final String MANIFEST_FILE = "META-INF/MANIFEST.MF";
  private static final String LUA_COMMAND = "/lua require('%s')";

  public interface Context {

    MinecraftServer getServer();

    Logger getLogger();
  }

  private final Context context;

  public AddOnLauncher(Context context) {
    this.context = context;
  }

  public void execute() {
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Enumeration<URL> resources = classLoader.getResources(MANIFEST_FILE);
      Iterators.forEnumeration(resources).forEachRemaining(res -> {
        try {
          Manifest mf = new Manifest(res.openStream());
          handle(mf);
        } catch (IOException ex) {
          printException(format("Error in WoL Add-On Launcher: can't read manifest %s properly!",
              res.toExternalForm()), ex);
        } catch (Throwable t) {
          printException(
              format("Error in WoL Add-On Launcher: can't execute startup spell defined by %s!",
                  res.toExternalForm()),
              t);
        }
      });
    } catch (IOException e) {
      printException(
          format("Error in WoL Add-On Launcher: an unexpected error occured: %s", e.getMessage()),
          e);
    }
  }

  private void handle(Manifest manifest) {
    Attributes attr = manifest.getMainAttributes();
    String module = attr.getValue(WOL_STARTUP_MODULE_KEY);
    if (module != null) {
      checkModuleSyntax(module);
      MinecraftServer server = context.getServer();
      server.getCommandManager().executeCommand(server, format(LUA_COMMAND, module));
    }
  }

  private void checkModuleSyntax(String module) {
    if (module.contains("'") || module.contains("\"") || module.contains("[")
        || module.contains("]")) {
      throw new IllegalArgumentException(format(
          "Manifest entry '%s' with value '%s' contains illegal characters! Quotation marks are not allowed.",
          WOL_STARTUP_MODULE_KEY, module));
    }
    if (module.contains(" ") || module.contains("\t") || module.contains("\n")) {
      throw new IllegalArgumentException(format(
          "Manifest entry '%s' with value '%s' contains illegal characters! White spaces are not allowed.",
          WOL_STARTUP_MODULE_KEY, module));
    }
  }


  private void printException(String message, Throwable t) {
    context.getLogger().error(message, t);
    String stackTrace = getStackTrace(t);
    WolAnnouncementMessage txt = new WolAnnouncementMessage(message);
    TextComponentString details = new TextComponentString(stackTrace);
    txt.setStyle((new Style()).setColor(TextFormatting.RED).setBold(Boolean.valueOf(true)));
    txt.appendSibling(details);
    context.getServer().sendMessage(txt);
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
