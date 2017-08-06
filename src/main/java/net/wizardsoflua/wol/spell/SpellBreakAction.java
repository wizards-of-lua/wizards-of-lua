package net.wizardsoflua.wol.spell;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.wol.WolAnnouncementMessage;
import net.wizardsoflua.wol.WolCommandAction;

public class SpellBreakAction implements WolCommandAction {

  private final String selector;

  public SpellBreakAction(String selector) {
    this.selector = selector;
  }

  @Override
  public void execute(ICommandSender sender) throws CommandException {
    if ("all".equals(selector)) {
      WizardsOfLua.instance.getSpellRegistry().breakAll();
      sender.getEntityWorld().getMinecraftServer().getPlayerList()
          .sendMessage(new WolAnnouncementMessage("Broke all spells."));
    } else {
      throw new CommandException("Illegal spell selector: %s!", selector);
    }
  }

}
