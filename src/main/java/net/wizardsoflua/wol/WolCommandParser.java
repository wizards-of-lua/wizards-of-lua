package net.wizardsoflua.wol;

import java.util.ArrayDeque;
import java.util.Deque;

import net.minecraft.command.CommandException;
import net.wizardsoflua.wol.luatickslimit.PrintLuaTicksLimitAction;
import net.wizardsoflua.wol.luatickslimit.SetLuaTicksLimitAction;
import net.wizardsoflua.wol.spell.SpellBreakAction;
import net.wizardsoflua.wol.spell.SpellListAction;

public class WolCommandParser {

  public WolCommandAction parse(String[] args) throws CommandException {
    Deque<String> argList = newArrayDeque(args);
    if (argList.isEmpty()) {
      throw new CommandException("Missing WoL command section!");
    }
    String section = argList.pop();
    switch (section) {
      case "spell":
        return newSpellAction(argList);
      case "luaTicksLimit":
        return newLuaTicksLimitAction(argList);
      default:
        throw new CommandException("Unknown WoL command section: %s!", section);
    }
  }

  private WolCommandAction newLuaTicksLimitAction(Deque<String> argList) throws CommandException {
    if (argList.isEmpty()) {
      return new PrintLuaTicksLimitAction();
    }
    String action = argList.pop();
    switch (action) {
      case "set":
        return new SetLuaTicksLimitAction(argList.pop());
      default:
        throw new CommandException("Illegal spell action: %s!", action);
    }
  }

  private WolCommandAction newSpellAction(Deque<String> argList) throws CommandException {
    if (argList.isEmpty()) {
      throw new CommandException("Missing spell action!");
    }
    String action = argList.pop();
    switch (action) {
      case "break":
        return new SpellBreakAction(argList.pop());
      case "list":
        return new SpellListAction(argList.peek());
      default:
        throw new CommandException("Illegal spell action: %s!", action);
    }
  }

  private Deque<String> newArrayDeque(String[] args) {
    ArrayDeque<String> result = new ArrayDeque<>();
    for (String arg : args) {
      result.add(arg);
    }
    return result;
  }

}
