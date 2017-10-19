package net.wizardsoflua.wol.sharedautorequire;

import java.util.Collections;
import java.util.Deque;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.wol.menu.CommandAction;
import net.wizardsoflua.wol.menu.MenuEntry;

public class SetSharedAutoRequireAction extends MenuEntry implements CommandAction {

  private final WizardsOfLua wol;

  public SetSharedAutoRequireAction() {
    wol = WizardsOfLua.instance;
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      Deque<String> argList, BlockPos targetPos) {
    return Collections.emptyList();
  }

  @Override
  public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
    String module = argList.poll();
    if (module != null) {
      wol.getProfiles().setSharedProfile(module);
      sender.sendMessage(PrintSharedAutoRequireAction.getMessage(module));
    } else {
      // TODO I18n
      throw new CommandException("Missing module!");
    }
  }

}
