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

public class FileDeleteAction extends MenuEntry implements CommandAction {
  private static final int MAX_NUM_FILES = 500;

  private final WizardsOfLua wol;

  public FileDeleteAction(WizardsOfLua wol) {
    this.wol = wol;
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      Deque<String> argList, BlockPos targetPos) {
    String name = argList.poll();
    Entity entity = sender.getCommandSenderEntity();
    if (entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) entity;
      List<String> files = wol.getFileRegistry().getLuaFilenames(player);
      return getMatchingTokens(name, files.subList(0, Math.min(files.size(), MAX_NUM_FILES)));
    }
    return Collections.emptyList();
  }

  @Override
  public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
    String name = argList.poll();
    Entity entity = sender.getCommandSenderEntity();
    if (entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) entity;
      try {
        wol.getFileRegistry().deleteFile(player, name);
      } catch (IllegalArgumentException e) {
        throw new CommandException(e.getMessage());
      }
      WolAnnouncementMessage message = new WolAnnouncementMessage(name + " deleted.");
      sender.sendMessage(message);
    } else {
      throw new CommandException("Only players can use this command!");
    }
  }

}
