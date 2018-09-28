package net.wizardsoflua.startup;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.Logger;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.wizardsoflua.WolAnnouncementMessage;

public class Startup {

  public interface Context extends AddOnFinder.Context {
    Path getSharedLibDir();

    MinecraftServer getServer();

    Logger getLogger();
  }

  private final Context context;
  private final AddOnFinder addOnFinder;
  private final StartupModuleFinder startupModuleFinder = new StartupModuleFinder();

  public Startup(Context context) {
    this.context = checkNotNull(context, "context==null!");
    addOnFinder = new AddOnFinder(context);
  }

  public void runStartupSequence(ICommandSender aSender) {
    ICommandSender sender = wrap(checkNotNull(aSender, "sender==null!"));
    sender.sendMessage(new WolAnnouncementMessage("Running startup sequence"));
    Path sharedLibDir = context.getSharedLibDir();
    try {
      List<String> sharedStartupModules = startupModuleFinder.findStartupModulesIn(sharedLibDir);
      executeModules(sender, addOnFinder.getStartupModules());
      executeModules(sender, sharedStartupModules);
    } catch (IOException e) {
      sendException(format("Error while searching %s for startup modules", sharedLibDir), e,
          sender);
    }
  }

  private void executeModules(ICommandSender sender, List<String> modules) {
    for (String module : modules) {
      context.getLogger().debug(format("Executing module '%s'", module));
      executeModule(sender, module);
    }
  }

  private void executeModule(ICommandSender sender, String module) {
    String cmd = format("/lua require('%s')", module);
    context.getServer().getCommandManager().executeCommand(sender, cmd);
  }

  private ICommandSender wrap(final ICommandSender sender) {
    checkNotNull(sender, "sender==null!");
    return new ICommandSender() {

      @Override
      public void sendMessage(ITextComponent component) {
        MinecraftServer server = context.getServer();
        if (sender == server) {
          sender.sendMessage(component);
        } else {
          server.sendMessage(component);
          sender.sendMessage(component);
        }
      }

      @Override
      public MinecraftServer getServer() {
        return context.getServer();
      }

      @Override
      public String getName() {
        return context.getServer().getName();
      }

      @Override
      public World getEntityWorld() {
        return context.getServer().getEntityWorld();
      }

      @Override
      public boolean canUseCommand(int permLevel, String commandName) {
        return context.getServer().canUseCommand(permLevel, commandName);
      }
    };
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
