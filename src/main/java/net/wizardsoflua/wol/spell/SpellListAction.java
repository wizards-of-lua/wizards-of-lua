package net.wizardsoflua.wol.spell;

import java.util.Collections;
import java.util.Deque;
import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
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
  private static final String BASE_USAGE = "Usage: /wol spell list";
  private static final String ALL = "all";
  private static final String BY_SID = "bySid";
  private static final String BY_NAME = "byName";
  private static final String BY_OWNER = "byOwner";

  private final WizardsOfLua wol;

  public SpellListAction(WizardsOfLua wol) {
    this.wol = wol;
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      Deque<String> argList, BlockPos targetPos) {
    if (argList.size() == 1) {
      String next = argList.poll();
      return getMatchingTokens(next, ALL, BY_NAME, BY_OWNER, BY_SID);
    }
    String filterKey = argList.poll();
    if (argList.size() == 1) {
      if (BY_OWNER.equals(filterKey)) {
        return getMatchingTokens(argList.poll(), server.getOnlinePlayerNames());
      }
      if (BY_SID.equals(filterKey)) {
        return getMatchingTokens(argList.poll(), wol.getSpellRegistry().getActiveSids());
      }
      if (BY_NAME.equals(filterKey)) {
        return getMatchingTokens(argList.poll(), wol.getSpellRegistry().getActiveNames());
      }
    }
    return Collections.emptyList();
  }

  @Override
  public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
    String option = argList.poll();
    if (ALL.equalsIgnoreCase(option)) {
      listAll(sender);
    } else if (BY_SID.equalsIgnoreCase(option)) {
      String sidString = argList.poll();
      listBySid(sender, sidString);
    } else if (BY_NAME.equalsIgnoreCase(option)) {
      // TODO support names with white spaces!
      String name = argList.poll();
      listByName(sender, name);
    } else if (BY_OWNER.equalsIgnoreCase(option)) {
      String ownerName = argList.poll();
      listByOwnerName(sender, ownerName);
    } else if (option == null) {
      listByOwner(sender);
    } else {
      // TODO I18n
      throw new CommandException("Illegal spell list option: %s!", option);
    }
  }

  private void listAll(ICommandSender sender) {
    // TODO I18n
    String message = "Active spells";
    Iterable<SpellEntity> spells = wol.getSpellRegistry().getAll();
    listSpells(sender, message, spells);
  }

  private void listBySid(ICommandSender sender, String sidString) throws CommandException {
    if (sidString == null) {
      throw new CommandException(BASE_USAGE + " " + BY_SID + " <sid>");
    }
    long sid = CommandBase.parseLong(sidString);
    // TODO I18n
    String message = "Active spells with sid " + sid;
    listSpells(sender, message, spell -> sid == spell.getSid());
  }

  private void listByName(ICommandSender sender, String name) throws CommandException {
    if (name == null) {
      throw new CommandException(BASE_USAGE + " " + BY_NAME + " <name>");
    }
    // TODO I18n
    String message = "Active spells with name '" + name + "'";
    listSpells(sender, message, spell -> name.equals(spell.getName()));
  }

  private void listByOwnerName(ICommandSender sender, String ownerName) throws CommandException {
    if (ownerName == null) {
      throw new CommandException(BASE_USAGE + " " + BY_OWNER + " <owner>");
    }
    // TODO I18n
    String message = "Active spells of " + ownerName;
    listSpells(sender, message, spell -> {
      Entity owner = spell.getOwnerEntity();
      return owner != null && ownerName.equals(owner.getName());
    });
  }

  private void listByOwner(ICommandSender sender) {
    // TODO I18n
    String message = "Your active spells";
    listSpells(sender, message, spell -> sender.equals(spell.getOwner()));
  }

  private void listSpells(ICommandSender sender, String message, Predicate<SpellEntity> predicate) {
    Iterable<SpellEntity> spells = wol.getSpellRegistry().get(predicate);
    listSpells(sender, message, spells);
  }

  private void listSpells(ICommandSender sender, String message, Iterable<SpellEntity> spells) {
    sender.sendMessage(format(message, spells));
  }

  private ITextComponent format(String message, Iterable<SpellEntity> spells) {
    WolAnnouncementMessage result = new WolAnnouncementMessage(message + ":\n");
    for (SpellEntity spell : spells) {
      TextComponentString name = new TextComponentString(spell.getSid() + ": ");
      name.setStyle(new Style().setColor(TextFormatting.DARK_GREEN));
      String description = spell.getProgram().getCode();
      int maxLength = 40;
      String ellipsis = "...";
      if (description.length() > maxLength + ellipsis.length()) {
        description = description.substring(0, maxLength) + ellipsis;
      }
      TextComponentString codeMsg = new TextComponentString(description + "\n");
      codeMsg.setStyle(new Style().setColor(TextFormatting.WHITE));
      result.appendSibling(name);
      result.appendSibling(codeMsg);
    }
    return result;
  }

}
