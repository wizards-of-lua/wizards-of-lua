package net.wizardsoflua.wol.browser;

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
import net.wizardsoflua.file.Crypto;
import net.wizardsoflua.wol.menu.CommandAction;
import net.wizardsoflua.wol.menu.MenuEntry;

public class LogoutAction extends MenuEntry implements CommandAction {

  private final WizardsOfLua wol;
  private final Crypto crypto = new Crypto();

  public LogoutAction(WizardsOfLua wol) {
    this.wol = wol;
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
      String password = crypto.createRandomPassword();
      wol.getConfig().getOrCreateWizardConfig(player.getUniqueID()).setRestApiKey(password);
      WolAnnouncementMessage message =
          new WolAnnouncementMessage("Your web browser is logged out.");
      sender.sendMessage(message);
    }
  }

}
