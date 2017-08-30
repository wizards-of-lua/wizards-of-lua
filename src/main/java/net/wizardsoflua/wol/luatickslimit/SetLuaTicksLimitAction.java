package net.wizardsoflua.wol.luatickslimit;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.wol.WolCommandAction;

public class SetLuaTicksLimitAction implements WolCommandAction {

  private String limit;

  public SetLuaTicksLimitAction(String limit) {
    this.limit = limit;
  }

  @Override
  public void execute(ICommandSender sender) throws CommandException {
    int luaTicksLimit = Integer.parseInt(limit);
    WizardsOfLua.instance.getConfig().setLuaTicksLimit(luaTicksLimit);
  }

}
