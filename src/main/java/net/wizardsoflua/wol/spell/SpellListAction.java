package net.wizardsoflua.wol.spell;

import java.util.Collections;
import java.util.Deque;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.wol.menu.CommandAction;
import net.wizardsoflua.wol.menu.MenuEntry;

public class SpellListAction extends MenuEntry implements CommandAction {

  public SpellListAction() {}

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      Deque<String> argList, BlockPos targetPos) {
    if (argList.size() == 1) {
      return getMatchingTokens(argList.poll(), "all");
    }
    return Collections.emptyList();
  }

  @Override
  public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
    String selector = argList.poll();
    if (selector == null || "all".equals(selector)) {
      Iterable<SpellEntity> spells = WizardsOfLua.instance.getSpellRegistry().getAll();
      ITextComponent message = format(spells);
      sender.sendMessage(message);
    } else {
      // TODO I18n
      throw new CommandException("Illegal spell selector: %s!", selector);
    }
  }

  private ITextComponent format(Iterable<SpellEntity> spells) {
    // TODO I18n
    WolAnnouncementMessage result = new WolAnnouncementMessage("Active spells:\n");
    for (SpellEntity spell : spells) {
      TextComponentString name = new TextComponentString(spell.getSid() + ": ");
      name.setStyle((new Style()).setColor(TextFormatting.DARK_GREEN));
      String description = spell.getProgram().getCode();
      if (description.length() > 40) {
        description = description.substring(0, 40) + "...";
      }
      TextComponentString codeMsg = new TextComponentString(description + "\n");
      codeMsg.setStyle((new Style()).setColor(TextFormatting.WHITE));
      result.appendSibling(name);
      result.appendSibling(codeMsg);
    }
    return result;
  }

}
