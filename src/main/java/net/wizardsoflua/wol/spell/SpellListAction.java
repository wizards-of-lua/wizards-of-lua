package net.wizardsoflua.wol.spell;

import javax.annotation.Nullable;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.wol.WolCommandAction;

public class SpellListAction implements WolCommandAction {

  private final @Nullable String selector;

  public SpellListAction(@Nullable String selector) {
    this.selector = selector;
  }

  @Override
  public void execute(ICommandSender sender) throws CommandException {
    if (selector == null || "all".equals(selector)) {
      Iterable<SpellEntity> spells = WizardsOfLua.instance.getSpellRegistry().getAll();
      ITextComponent message = format(spells);
      sender.sendMessage(message);
    } else {
      throw new CommandException("Illegal spell selector: %s!", selector);
    }
  }

  private ITextComponent format(Iterable<SpellEntity> spells) {
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
