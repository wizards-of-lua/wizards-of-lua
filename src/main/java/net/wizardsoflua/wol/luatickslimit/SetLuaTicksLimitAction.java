package net.wizardsoflua.wol.luatickslimit;

import java.util.Collections;
import java.util.Deque;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.wol.menu.CommandAction;
import net.wizardsoflua.wol.menu.MenuEntry;

public class SetLuaTicksLimitAction extends MenuEntry implements CommandAction {

  public SetLuaTicksLimitAction() {}

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      Deque<String> argList, BlockPos targetPos) {
    if (argList.size() == 1) {
      return getMatchingTokens(argList.peek(),
          Lists.newArrayList("1000", "10000", "100000", "1000000", "10000000"));
    }
    return Collections.emptyList();
  }

  @Override
  public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
    String limit = argList.peek();
    if (limit != null) {
      Integer luaTicksLimit = Ints.tryParse(limit);
      if (luaTicksLimit != null) {
        luaTicksLimit = WizardsOfLua.instance.getConfig().setLuaTicksLimit(luaTicksLimit);
        // TODO I18n
        WolAnnouncementMessage message =
            new WolAnnouncementMessage("luaTicksLimit has been updated to " + luaTicksLimit);
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
