package net.wizardsoflua.wol.profile;

import java.util.Collections;
import java.util.Deque;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.wol.menu.CommandAction;
import net.wizardsoflua.wol.menu.MenuEntry;

public class PrintRequireAction extends MenuEntry implements CommandAction {

  private final WizardsOfLua wol;

  public PrintRequireAction() {
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
      String module = wol.getProfiles().getProfile(player);
      if (module != null) {
        sender.sendMessage(getMessage(module));
      } else {
        // TODO I18n
        sender.sendMessage(new WolAnnouncementMessage("required module is not set"));
      }
    } else {
      // TODO I18n
      throw new CommandException("Only players can execute this command!");
    }
  }

  public static ITextComponent getMessage(String module) {
    return new WolAnnouncementMessage(String.format("require = \"%s\"", module));
  }

}
