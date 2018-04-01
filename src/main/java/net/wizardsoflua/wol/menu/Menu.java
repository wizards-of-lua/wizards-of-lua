package net.wizardsoflua.wol.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class Menu extends MenuEntry {

  private CommandAction rootAction;
  private Map<String, MenuEntry> entries = new HashMap<>();

  public void put(String name, MenuEntry entry) {
    entries.put(name, entry);
  }

  public void put(CommandAction action) {
    rootAction = action;
  }

  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      Deque<String> argList, BlockPos targetPos) {
    if (!argList.isEmpty()) {
      String arg = argList.pop();
      MenuEntry entry = entries.get(arg);
      if (entry != null) {
        return entry.getTabCompletions(server, sender, argList, targetPos);
      }
      Set<String> names = entries.keySet();
      return getMatchingTokens(arg, names);
    }
    return Collections.emptyList();
  }

  private List<String> sort(Set<String> set) {
    List<String> result = new ArrayList<>(set);
    Collections.sort(result);
    return result;
  }

  public CommandAction getAction(MinecraftServer server, ICommandSender sender,
      Deque<String> argList) throws CommandException {
    if (!argList.isEmpty()) {
      String arg = argList.pop();
      MenuEntry entry = entries.get(arg);
      if (entry != null) {
        if (entry instanceof Menu) {
          return ((Menu) entry).getAction(server, sender, argList);
        }
        if (entry instanceof CommandAction) {
          return (CommandAction) entry;
        }
        throw new IllegalStateException("Unexpected entry type: " + entry.getClass());
      }
      // TODO I18n
      throw new CommandException("Unexpected element! Expected one of %s, but got '%s'",
          sort(entries.keySet()), arg);
    } else if (rootAction != null) {
      return rootAction;
    }
    // TODO I18n
    throw new CommandException("Missing element! Expected one of %s!", sort(entries.keySet()));
  }

}
