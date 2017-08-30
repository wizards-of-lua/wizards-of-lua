package net.wizardsoflua.wol.spell;

import java.util.Collections;
import java.util.Deque;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.wol.menu.CommandAction;
import net.wizardsoflua.wol.menu.MenuEntry;

public class SpellBreakAction extends MenuEntry implements CommandAction {

  public SpellBreakAction() {}

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      Deque<String> argList, BlockPos targetPos) {
    if (argList.size() == 1) {
      return getMatchingTokens(argList.peek(), Lists.newArrayList("all"));
    }
    return Collections.emptyList();
  }

  @Override
  public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
    String selector = argList.peekFirst();
    if ("all".equals(selector)) {
      WizardsOfLua.instance.getSpellRegistry().breakAll();
      // TODO I18n
      sender.getEntityWorld().getMinecraftServer().getPlayerList()
          .sendMessage(new WolAnnouncementMessage("Broke all spells"));
    } else {
      // TODO I18n
      throw new CommandException("Illegal spell selector: %s!", selector);
    }
  }


}
