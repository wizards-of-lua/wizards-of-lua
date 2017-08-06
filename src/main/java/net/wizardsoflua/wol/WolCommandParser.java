package net.wizardsoflua.wol;

import java.util.ArrayDeque;
import java.util.Deque;

import net.minecraft.command.CommandException;
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
      default:
        throw new CommandException("Unknown WoL command section: %s!", section);
    }
  }

  private WolCommandAction newSpellAction(Deque<String> args) throws CommandException {
    if (args.isEmpty()) {
      throw new CommandException("Missing spell action!");
    }
    String action = args.pop();
    switch (action) {
      case "break":
        return new SpellBreakAction(args.pop());
      case "list":
        return new SpellListAction(args.peek());
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
