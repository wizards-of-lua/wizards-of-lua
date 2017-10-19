package net.wizardsoflua.wol.sharedautorequire;

import java.util.Collections;
import java.util.Deque;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.wol.menu.CommandAction;
import net.wizardsoflua.wol.menu.MenuEntry;

public class PrintSharedAutoRequireAction extends MenuEntry implements CommandAction {

  private final WizardsOfLua wol;

  public PrintSharedAutoRequireAction() {
    wol = WizardsOfLua.instance;
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      Deque<String> argList, BlockPos targetPos) {
    return Collections.emptyList();
  }

  @Override
  public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
    Entity entity = sender.getCommandSenderEntity();
    String module = wol.getProfiles().getSharedProfile();
    if (module != null) {
      sender.sendMessage(getMessage(module));
    } else {
      // TODO I18n
      sender.sendMessage(new WolAnnouncementMessage("sharedAutoRequire is not set"));
    }
  }

  public static ITextComponent getMessage(String module) {
    return new WolAnnouncementMessage(String.format("sharedAutoRequire = \"%s\"", module));
  }

}
