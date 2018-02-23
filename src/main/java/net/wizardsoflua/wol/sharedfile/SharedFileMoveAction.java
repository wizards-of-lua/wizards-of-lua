package net.wizardsoflua.wol.sharedfile;

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

public class SharedFileMoveAction extends MenuEntry implements CommandAction {
  private static final int MAX_NUM_FILES = 500;

  private final WizardsOfLua wol;

  public SharedFileMoveAction(WizardsOfLua wol) {
    this.wol = wol;
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      Deque<String> argList, BlockPos targetPos) {
    String name = argList.poll();
    if (argList.isEmpty()) {
      List<String> files = wol.getFileRegistry().getSharedLuaFilenames();
      return getMatchingTokens(name, files.subList(0, Math.min(files.size(), MAX_NUM_FILES)));
    }
    return Collections.emptyList();
  }

  @Override
  public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
    String name = argList.poll();
    String newName = argList.poll();
    if (name != null && newName != null) {
      try {
        wol.getFileRegistry().moveSharedFile(name, newName);
      } catch (IllegalArgumentException e) {
        throw new CommandException(e.getMessage());
      }
      WolAnnouncementMessage message = new WolAnnouncementMessage(name + " moved to " + newName);
      sender.sendMessage(message);
    } else {
      WolAnnouncementMessage message = new WolAnnouncementMessage(
          "Error - Can't move! To move a file please specify old name and new name");
      sender.sendMessage(message);
    }
  }

}
