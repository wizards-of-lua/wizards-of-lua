package net.wizardsoflua.wol.file;

import java.util.Collections;
import java.util.Deque;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.wol.menu.CommandAction;
import net.wizardsoflua.wol.menu.MenuEntry;

public class FileMoveAction extends MenuEntry implements CommandAction {
  private static final int MAX_NUM_FILES = 500;

  private final WizardsOfLua wol;

  public FileMoveAction(WizardsOfLua wol) {
    this.wol = wol;
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      Deque<String> argList, BlockPos targetPos) {
    String name = argList.poll();
    if (argList.isEmpty()) {
      Entity entity = sender.getCommandSenderEntity();
      if (entity instanceof EntityPlayer) {
        EntityPlayer player = (EntityPlayer) entity;
        List<String> files = wol.getFileRepository().getLuaFilenames(player);
        return getMatchingTokens(name, files.subList(0, Math.min(files.size(), MAX_NUM_FILES)));
      }
    }
    return Collections.emptyList();
  }

  @Override
  public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
    String name = argList.poll();
    String newName = argList.poll();
    Entity entity = sender.getCommandSenderEntity();
    if (entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) entity;
      if (name != null && newName != null) {
        try {
          wol.getFileRepository().moveFile(player, name, newName);
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
    } else {
      throw new CommandException("Only players can use this command!");
    }
  }

}
