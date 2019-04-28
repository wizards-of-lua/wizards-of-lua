package net.wizardsoflua.wol.pack;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraftforge.common.ForgeHooks.newChatWithLinks;
import java.net.URL;
import javax.annotation.Nullable;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TextComponentString;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.WolServer;

public class PackExportCommand implements Command<CommandSource> {
  private static final String DIRECTORY_ARGUMENT = "directory";

  private final WolServer wol;

  public PackExportCommand(WolServer wol) {
    this.wol = checkNotNull(wol, "wol==null!");
  }

  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(literal("wol")//
        .then(literal("pack")//
            .then(literal("export") //
                .then(argument(DIRECTORY_ARGUMENT, string()) //
                    .executes(this)))));
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    @Nullable // TODO make it optional
    String directory = StringArgumentType.getString(context, DIRECTORY_ARGUMENT);
    CommandSource source = context.getSource();
    URL url;
    try {
      url = wol.getFileRepository().getSpellPackExportURL(directory);
    } catch (IllegalArgumentException e) {
      throw new CommandException(new TextComponentString(e.getMessage()));
    }
    WolAnnouncementMessage message = new WolAnnouncementMessage("Click here to download: ");
    message.appendSibling(newChatWithLinks(url.toExternalForm(), false));
    source.sendFeedback(message, true);
    return Command.SINGLE_SUCCESS;
  }


  // @Override
  // public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
  // Deque<String> argList, BlockPos targetPos) {
  // String name = argList.poll();
  //
  // Entity entity = sender.getCommandSenderEntity();
  // if (entity instanceof EntityPlayer) {
  // List<String> files = wol.getFileRepository().getToplevelSharedDirectoryNames();
  // return getMatchingTokens(name, files.subList(0, Math.min(files.size(), MAX_NUM_FILES)));
  // }
  // return Collections.emptyList();
  // }

}
