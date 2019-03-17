package net.wizardsoflua.wol.browser;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.minecraft.command.Commands.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.file.Crypto;

public class LogoutCommand implements Command<CommandSource> {
  private final WizardsOfLua wol;
  private final Crypto crypto = new Crypto();

  public LogoutCommand(WizardsOfLua wol) {
    this.wol = checkNotNull(wol, "wol==null!");
  }

  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("wol")//
            .then(literal("browser")//
                .then(literal("logout")//
                    .executes(this))));
  }

  @Override
  public int run(CommandContext<CommandSource> context) {
    CommandSource source = context.getSource();
    Entity entity = source.getEntity();
    if (entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) entity;
      String password = crypto.createRandomPassword();
      wol.getConfig().getOrCreateWizardConfig(player.getUniqueID()).setRestApiKey(password);
      WolAnnouncementMessage message =
          new WolAnnouncementMessage("Your web browser is logged out.");
      source.sendFeedback(message, true);
    }
    return Command.SINGLE_SUCCESS;
  }

  // @Override
  // public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
  // Deque<String> argList, BlockPos targetPos) {
  // return Collections.emptyList();
  // }
  //
  // @Override
  // public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
  // Entity entity = sender.getCommandSenderEntity();
  // if (entity instanceof EntityPlayer) {
  // EntityPlayer player = (EntityPlayer) entity;
  // String password = crypto.createRandomPassword();
  // wol.getConfig().getOrCreateWizardConfig(player.getUniqueID()).setRestApiKey(password);
  // WolAnnouncementMessage message =
  // new WolAnnouncementMessage("Your web browser is logged out.");
  // sender.sendMessage(message);
  // }
  // }

}
