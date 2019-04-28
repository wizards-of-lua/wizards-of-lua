package net.wizardsoflua.wol.browser;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.minecraft.command.Commands.literal;
import static net.minecraftforge.common.ForgeHooks.newChatWithLinks;
import java.net.URL;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.WolServer;

public class LoginCommand implements Command<CommandSource> {

  private final WolServer wol;

  public LoginCommand(WolServer wol) {
    this.wol = checkNotNull(wol, "wol==null!");
  }

  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(getNode());
  }

  public LiteralArgumentBuilder<CommandSource> getNode() {
    return literal("wol")//
        .then(literal("browser")//
            .then(literal("login")//
                .executes(this)));
  }

  @Override
  public int run(CommandContext<CommandSource> context) {
    CommandSource source = context.getSource();
    Entity entity = source.getEntity();
    if (entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) entity;
      URL url = wol.getFileRepository().getPasswordTokenUrl(player);
      WolAnnouncementMessage message =
          new WolAnnouncementMessage("Click here to log in with your web browser: ");
      message.appendSibling(newChatWithLinks(url.toExternalForm(), false));
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
  // URL url = wol.getFileRepository().getPasswordTokenUrl(player);
  // WolAnnouncementMessage message =
  // new WolAnnouncementMessage("Click here to log in with your web browser: ");
  // message.appendSibling(newChatWithLinks(url.toExternalForm(), false));
  // sender.sendMessage(message);
  // }
  // }

}
