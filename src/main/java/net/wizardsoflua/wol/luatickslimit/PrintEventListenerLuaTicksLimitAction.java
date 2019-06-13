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

public class PrintEventListenerLuaTicksLimitAction extends MenuEntry implements CommandAction {
  private final WizardsOfLua wol;

  public PrintEventListenerLuaTicksLimitAction(WizardsOfLua wol) {
    this.wol = wol;
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      Deque<String> argList, BlockPos targetPos) {
    return Collections.emptyList();
  }

  @Override
  public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
    long eventListenerLuaTicksLimit =
        wol.getConfig().getGeneralConfig().getEventListenerLuaTicksLimit();
    WolAnnouncementMessage message =
        new WolAnnouncementMessage("eventListenerLuaTicksLimit = " + eventListenerLuaTicksLimit);
    sender.sendMessage(message);
  }
}
