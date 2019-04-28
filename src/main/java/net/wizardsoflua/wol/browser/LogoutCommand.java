package net.wizardsoflua.wol.browser;

import static net.minecraft.command.Commands.literal;
import javax.inject.Inject;
import com.google.auto.service.AutoService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.config.WolConfig;
import net.wizardsoflua.extension.server.spi.CommandRegisterer;
import net.wizardsoflua.file.Crypto;

@AutoService(CommandRegisterer.class)
public class LogoutCommand implements CommandRegisterer, Command<CommandSource> {
  private final Crypto crypto = new Crypto();
  @Inject
  private WolConfig config;

  @Override
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
      config.getOrCreateWizardConfig(player.getUniqueID()).setRestApiKey(password);
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
