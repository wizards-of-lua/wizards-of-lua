package net.wizardsoflua.wol.luatickslimit;

import java.util.Collections;
import java.util.Deque;
import java.util.List;

import com.google.common.primitives.Ints;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.wol.menu.CommandAction;
import net.wizardsoflua.wol.menu.MenuEntry;

public class SetEventListenerLuaTicksLimitAction extends MenuEntry implements CommandAction {
  private final WizardsOfLua wol;

  public SetEventListenerLuaTicksLimitAction(WizardsOfLua wol) {
    this.wol = wol;
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      Deque<String> argList, BlockPos targetPos) {
    if (argList.size() == 1) {
      return getMatchingTokens(argList.poll(), "1000", "10000", "100000", "1000000", "10000000");
    }
    return Collections.emptyList();
  }

  @Override
  public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
    String limit = argList.poll();
    if (limit != null) {
      Integer eventListenerLuaTicksLimit = Ints.tryParse(limit);
      if (eventListenerLuaTicksLimit != null) {
        eventListenerLuaTicksLimit = wol.getConfig().getGeneralConfig()
            .setEventListenerLuaTicksLimit(eventListenerLuaTicksLimit);
        // TODO I18n
        WolAnnouncementMessage message = new WolAnnouncementMessage(
            "eventListenerLuaTicksLimit has been updated to " + eventListenerLuaTicksLimit);
        sender.sendMessage(message);
      } else {
        // TODO I18n
        throw new CommandException("No integer value!");
      }
    } else {
      // TODO I18n
      throw new CommandException("Missing integer value!");
    }
  }
}
