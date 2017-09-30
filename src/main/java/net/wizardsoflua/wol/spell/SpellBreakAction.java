package net.wizardsoflua.wol.spell;

import java.util.Collections;
import java.util.Deque;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.wol.menu.CommandAction;
import net.wizardsoflua.wol.menu.MenuEntry;

public class SpellBreakAction extends MenuEntry implements CommandAction {
  private static final String ALL = "all";
  private static final String BY_SID = "bySid";
  private static final String BY_NAME = "byName";
  private static final String BY_OWNER = "byOwner";

  private final WizardsOfLua wol;

  public SpellBreakAction() {
    wol = WizardsOfLua.instance;
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      Deque<String> argList, BlockPos targetPos) {
    if (argList.size() == 1) {
      return getMatchingTokens(argList.poll(), ALL, BY_NAME, BY_OWNER, BY_SID);
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
      WizardsOfLua.instance.getSpellRegistry().breakAll();
      // TODO I18n
      sender.getEntityWorld().getMinecraftServer().getPlayerList()
          .sendMessage(new WolAnnouncementMessage("Broke all spells"));
    } else if (BY_SID.equalsIgnoreCase(option)) {
      String sidString = argList.poll();
      // TODO throw command exception if value is not an integer or null
      int sid = Integer.parseInt(sidString);
      boolean found = WizardsOfLua.instance.getSpellRegistry().breakBySid(sid);
      if (found) {
        // TODO I18n
        sender.getEntityWorld().getMinecraftServer().getPlayerList()
            .sendMessage(new WolAnnouncementMessage(String.format("Broke %s spell", 1)));
      } else {
        throw new CommandException("No matching spell found!");
      }
    } else if (BY_NAME.equalsIgnoreCase(option)) {
      String name = argList.poll();
      // TODO support names with white spaces!
      // TODO throw command exception if value is null
      int count = WizardsOfLua.instance.getSpellRegistry().breakByName(name);
      if (count == 1) {
        // TODO I18n
        sender.getEntityWorld().getMinecraftServer().getPlayerList()
            .sendMessage(new WolAnnouncementMessage(String.format("Broke %s spell", count)));
      } else if (count > 1) {
        // TODO I18n
        sender.getEntityWorld().getMinecraftServer().getPlayerList()
            .sendMessage(new WolAnnouncementMessage(String.format("Broke %s spells", count)));
      } else {
        throw new CommandException("No matching spells found!");
      }
    } else if (BY_OWNER.equalsIgnoreCase(option)) {
      String ownerName = argList.poll();
      // TODO throw command exception if value is null
      int count = WizardsOfLua.instance.getSpellRegistry().breakByOwner(ownerName);
      if (count == 1) {
        // TODO I18n
        sender.getEntityWorld().getMinecraftServer().getPlayerList()
            .sendMessage(new WolAnnouncementMessage(String.format("Broke %s spell", count)));
      } else if (count > 1) {
        // TODO I18n
        sender.getEntityWorld().getMinecraftServer().getPlayerList()
            .sendMessage(new WolAnnouncementMessage(String.format("Broke %s spells", count)));
      } else {
        throw new CommandException("No matching spells found!");
      }
    } else {
      // TODO I18n
      throw new CommandException("Illegal spell break option: %s!", option);
    }
  }

}
