package net.wizardsoflua.wol;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;

import com.google.common.base.Joiner;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.wol.browser.LoginAction;
import net.wizardsoflua.wol.browser.LogoutAction;
import net.wizardsoflua.wol.file.FileDeleteAction;
import net.wizardsoflua.wol.file.FileEditAction;
import net.wizardsoflua.wol.file.FileMoveAction;
import net.wizardsoflua.wol.file.FileSection;
import net.wizardsoflua.wol.gist.GistGetAction;
import net.wizardsoflua.wol.luatickslimit.PrintEventListenerLuaTicksLimitAction;
import net.wizardsoflua.wol.luatickslimit.PrintLuaTicksLimitAction;
import net.wizardsoflua.wol.luatickslimit.SetEventListenerLuaTicksLimitAction;
import net.wizardsoflua.wol.luatickslimit.SetLuaTicksLimitAction;
import net.wizardsoflua.wol.menu.CommandAction;
import net.wizardsoflua.wol.menu.Menu;
import net.wizardsoflua.wol.sharedfile.SharedFileDeleteAction;
import net.wizardsoflua.wol.sharedfile.SharedFileEditAction;
import net.wizardsoflua.wol.sharedfile.SharedFileMoveAction;
import net.wizardsoflua.wol.spell.SpellBreakAction;
import net.wizardsoflua.wol.spell.SpellListAction;
import net.wizardsoflua.wol.startup.StartupAction;

public class WolCommand extends CommandBase {
  private static final String CMD_NAME = "wol";

  /**
   * Pattern used to re-tokenize the arguments by taking quoted strings into account.
   */
  private static final Pattern TOKEN = Pattern.compile("\"([^\"]*)\"|(\\S+)");

  private final List<String> aliases = new ArrayList<String>();

  private final WizardsOfLua wol;
  private final Logger logger;
  private final Menu menu;

  public WolCommand(WizardsOfLua wol, Logger logger) {
    this.wol = wol;
    this.logger = logger;
    this.menu = new WolMenu();
    aliases.add(CMD_NAME);
  }

  class WolMenu extends Menu {
    WolMenu() {
      put("spell", new SpellMenu());
      put("file", new FileMenu());
      put("browser", new BrowserMenu());
      put("shared-file", new SharedFileMenu());
      put("luaTicksLimit", new LuaTicksLimitMenu());
      put("eventListenerLuaTicksLimit", new EventListenerLuaTicksLimitMenu());
      put("startup", new StartupAction(wol));
    }
  }
  class FileMenu extends Menu {
    FileMenu() {
      put("delete", new FileDeleteAction(wol));
      put("edit", new FileEditAction(wol));
      put("gist", new GistMenu(FileSection.PERSONAL));
      put("move", new FileMoveAction(wol));
    }
  }
  class GistMenu extends Menu {
    GistMenu(FileSection section) {
      put("get", new GistGetAction(wol, logger, section));
    }
  }
  class SharedFileMenu extends Menu {
    SharedFileMenu() {
      put("delete", new SharedFileDeleteAction(wol));
      put("edit", new SharedFileEditAction(wol));
      put("gist", new GistMenu(FileSection.SHARED));
      put("move", new SharedFileMoveAction(wol));
    }
  }
  class SpellMenu extends Menu {
    SpellMenu() {
      put("list", new SpellListAction(wol));
      put("break", new SpellBreakAction(wol));
    }
  }
  class LuaTicksLimitMenu extends Menu {
    LuaTicksLimitMenu() {
      put(new PrintLuaTicksLimitAction(wol));
      put("set", new SetLuaTicksLimitAction(wol));
    }
  }
  class EventListenerLuaTicksLimitMenu extends Menu {
    EventListenerLuaTicksLimitMenu() {
      put(new PrintEventListenerLuaTicksLimitAction(wol));
      put("set", new SetEventListenerLuaTicksLimitAction(wol));
    }
  }
  class BrowserMenu extends Menu {
    BrowserMenu() {
      put("login", new LoginAction(wol));
      put("logout", new LogoutAction(wol));
    }
  }

  @Override
  public String getName() {
    return CMD_NAME;
  }

  @Override
  public String getUsage(ICommandSender sender) {
    // TODO return usage
    return "";
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      String[] args, BlockPos targetPos) {
    return menu.getTabCompletions(server, sender, newArrayDeque(args), targetPos);
  }

  /**
   * Return the required permission level for this command.
   */
  @Override
  public int getRequiredPermissionLevel() {
    // TODO add real permission checking somewhere
    return 2;
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    Deque<String> argList = newArrayDeque(args);
    CommandAction action = menu.getAction(server, sender, argList);
    action.execute(sender, argList);
  }

  private Deque<String> newArrayDeque(String[] args) throws IllegalArgumentException {
    ArrayDeque<String> result = new ArrayDeque<>();
    if (args == null || args.length == 0) {
      return result;
    }
    String all = Joiner.on(" ").join(args);
    Matcher m = TOKEN.matcher(all);
    String next = null;
    while (m.find()) {
      if (m.group(1) != null) {
        next = m.group(1);
      } else {
        next = m.group(2);
      }
      if (next != null) {
        if (next.contains("\"")) {
          // TODO I18n
          throw new IllegalArgumentException("Unmatched quotes!");
        }
        result.add(next);
      }
    }
    if (args[args.length - 1].isEmpty()) {
      result.add("");
    }
    return result;
  }

}
