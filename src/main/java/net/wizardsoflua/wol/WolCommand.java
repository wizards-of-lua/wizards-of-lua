package net.wizardsoflua.wol;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.wol.luatickslimit.PrintLuaTicksLimitAction;
import net.wizardsoflua.wol.luatickslimit.SetLuaTicksLimitAction;
import net.wizardsoflua.wol.menu.CommandAction;
import net.wizardsoflua.wol.menu.Menu;
import net.wizardsoflua.wol.spell.SpellBreakAction;
import net.wizardsoflua.wol.spell.SpellListAction;

public class WolCommand extends CommandBase {
  private static final String CMD_NAME = "wol";
  private final List<String> aliases = new ArrayList<String>();

  private final Menu menu = new WolMenu();

  public WolCommand() {
    aliases.add(CMD_NAME);
  }

  class WolMenu extends Menu {
    WolMenu() {
      put("spell", new SpellMenu());
      put("luaTicksLimit", new LuaTicksLimitMenu());
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

  private Deque<String> newArrayDeque(String[] args) {
    ArrayDeque<String> result = new ArrayDeque<>();
    for (String arg : args) {
      result.add(arg);
    }
    return result;
  }

}
