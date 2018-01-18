package net.wizardsoflua.wol.browser;

import static net.minecraftforge.common.ForgeHooks.newChatWithLinks;

import java.net.URL;
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

public class LoginAction extends MenuEntry implements CommandAction {

  private final WizardsOfLua wol;

  public LoginAction() {
    wol = WizardsOfLua.instance;
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      Deque<String> argList, BlockPos targetPos) {
    return Collections.emptyList();
  }

  @Override
  public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
    Entity entity = sender.getCommandSenderEntity();
    if (entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) entity;
      URL url = wol.getFileRegistry().getPasswordTokenUrl(player);
      WolAnnouncementMessage message = new WolAnnouncementMessage("Click here to log in with your web browser: ");
      message.appendSibling(newChatWithLinks(url.toExternalForm(), false));
      sender.sendMessage(message);
    }
  }

}
