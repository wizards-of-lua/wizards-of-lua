package net.wizardsoflua.wol.spell;

import java.util.Collection;
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
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.wol.menu.CommandAction;
import net.wizardsoflua.wol.menu.MenuEntry;

public class SpellBreakAction extends MenuEntry implements CommandAction {
  private static final String BASE_USAGE = "Usage: /wol spell break";
  private static final String ALL = "all";
  private static final String BY_SID = "bySid";
  private static final String BY_NAME = "byName";
  private static final String BY_OWNER = "byOwner";

  private final WizardsOfLua wol;

  public SpellBreakAction(WizardsOfLua wol) {
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
      breakAll(sender);
    } else if (BY_SID.equalsIgnoreCase(option)) {
      String sidString = argList.poll();
      breakBySid(sender, sidString);
    } else if (BY_NAME.equalsIgnoreCase(option)) {
      // TODO support names with white spaces!
      String name = argList.poll();
      breakByName(sender, name);
    } else if (BY_OWNER.equalsIgnoreCase(option)) {
      String ownerName = argList.poll();
      breakByOwnerName(sender, ownerName);
    } else if (option == null) {
      breakByOwner(sender);
    } else {
      // TODO I18n
      throw new CommandException("Illegal spell break option: %s!", option);
    }
  }

  private void breakAll(ICommandSender sender) throws CommandException {
    Collection<SpellEntity> spells = wol.getSpellRegistry().getAll();
    breakSpells(sender, spells);
  }

  private void breakBySid(ICommandSender sender, String sidString) throws CommandException {
    if (sidString == null) {
      throw new CommandException(BASE_USAGE + " " + BY_SID + " <sid>");
    }
    long sid = CommandBase.parseLong(sidString);
    breakSpells(sender, spell -> sid == spell.getSid());
  }

  private void breakByName(ICommandSender sender, String name) throws CommandException {
    if (name == null) {
      throw new CommandException(BASE_USAGE + " " + BY_NAME + " <name>");
    }
    breakSpells(sender, spell -> name.equals(spell.getName()));
  }

  private void breakByOwnerName(ICommandSender sender, String ownerName) throws CommandException {
    if (ownerName == null) {
      throw new CommandException(BASE_USAGE + " " + BY_OWNER + " <owner>");
    }
    breakSpells(sender, spell -> {
      Entity owner = spell.getOwnerEntity();
      return owner != null && ownerName.equals(owner.getName());
    });
  }

  private void breakByOwner(ICommandSender sender) throws CommandException {
    breakSpells(sender, spell -> sender.equals(spell.getOwner()));
  }

  private void breakSpells(ICommandSender sender, Predicate<SpellEntity> predicate)
      throws CommandException {
    Iterable<SpellEntity> spells = wol.getSpellRegistry().get(predicate);
    int count = breakSpells(sender, spells);
    if (count == 0) {
      throw new CommandException("No matching spells found!");
    }
  }

  private int breakSpells(ICommandSender sender, Iterable<SpellEntity> spells)
      throws CommandException {
    int count = 0;
    for (SpellEntity spell : spells) {
      spell.setDead();
      count++;
    }
    if (count == 1) {
      // TODO I18n
      send(sender, "Broke 1 spell");
    } else if (count > 1) {
      // TODO I18n
      send(sender, "Broke " + count + " spells");
    }
    return count;
  }

  private void send(ICommandSender sender, String message) {
    sender.getEntityWorld().getMinecraftServer().getPlayerList()
        .sendMessage(new WolAnnouncementMessage(message));
  }
}
