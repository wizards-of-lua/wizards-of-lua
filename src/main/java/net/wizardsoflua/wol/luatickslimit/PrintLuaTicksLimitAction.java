package net.wizardsoflua.wol.luatickslimit;

import java.util.Collections;
import java.util.Deque;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.wol.menu.CommandAction;
import net.wizardsoflua.wol.menu.MenuEntry;

public class PrintLuaTicksLimitAction extends MenuEntry implements CommandAction {

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      Deque<String> argList, BlockPos targetPos) {
    return Collections.emptyList();
  }

  @Override
  public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
    int luaTicksLimit = WizardsOfLua.instance.getConfig().getGeneralConfig().getLuaTicksLimit();
    WolAnnouncementMessage message = new WolAnnouncementMessage("luaTicksLimit = " + luaTicksLimit);
    sender.sendMessage(message);
  }


}
