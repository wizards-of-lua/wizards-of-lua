package net.wizardsoflua.wol;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.wol.autorequire.PrintAutoRequireAction;
import net.wizardsoflua.wol.autorequire.SetAutoRequireAction;
import net.wizardsoflua.wol.autorequire.UnsetAutoRequireAction;
import net.wizardsoflua.wol.luatickslimit.PrintLuaTicksLimitAction;
import net.wizardsoflua.wol.luatickslimit.SetLuaTicksLimitAction;
import net.wizardsoflua.wol.menu.CommandAction;
import net.wizardsoflua.wol.menu.Menu;
import net.wizardsoflua.wol.sharedautorequire.PrintSharedAutoRequireAction;
import net.wizardsoflua.wol.sharedautorequire.SetSharedAutoRequireAction;
import net.wizardsoflua.wol.sharedautorequire.UnsetSharedAutoRequireAction;
import net.wizardsoflua.wol.spell.SpellBreakAction;
import net.wizardsoflua.wol.spell.SpellListAction;

public class WolCommand extends CommandBase {
  private static final String CMD_NAME = "wol";

  /**
   * Re-Tokenize the arguments by taking quoted strings into account.
   */
  private static final Pattern TOKEN = Pattern.compile("\"([^\"]*)\"|(\\S+)");

  private final List<String> aliases = new ArrayList<String>();

  private final Menu menu = new WolMenu();

  public WolCommand() {
    aliases.add(CMD_NAME);
  }

  class WolMenu extends Menu {
    WolMenu() {
      put("spell", new SpellMenu());
      put("luaTicksLimit", new LuaTicksLimitMenu());
      put("autoRequire", new AutoRequireMenu());
      put("sharedAutoRequire", new SharedAutoRequireMenu());
    }
  }
  class SpellMenu extends Menu {
    SpellMenu() {
      put("list", new SpellListAction());
      put("break", new SpellBreakAction());
    }
  }
  class LuaTicksLimitMenu extends Menu {
    LuaTicksLimitMenu() {
      put(new PrintLuaTicksLimitAction());
      put("set", new SetLuaTicksLimitAction());
    }
  }
  class AutoRequireMenu extends Menu {
    AutoRequireMenu() {
      put(new PrintAutoRequireAction());
      put("set", new SetAutoRequireAction());
      put("unset", new UnsetAutoRequireAction());
    }
  }
  class SharedAutoRequireMenu extends Menu {
    SharedAutoRequireMenu() {
      put(new PrintSharedAutoRequireAction());
      put("set", new SetSharedAutoRequireAction());
      put("unset", new UnsetSharedAutoRequireAction());
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
