package net.wizardsoflua.wol.luatickslimit;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.wol.WolCommandAction;

public class PrintLuaTicksLimitAction implements WolCommandAction {

  @Override
  public void execute(ICommandSender sender) throws CommandException {
    int luaTicksLimit = WizardsOfLua.instance.getConfig().getLuaTicksLimit();
    WolAnnouncementMessage message = new WolAnnouncementMessage("luaTicksLimit: " + luaTicksLimit);
    sender.sendMessage(message);
  }

}
