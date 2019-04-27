package net.wizardsoflua.wol.file;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;

public class FileMoveCommand implements Command<CommandSource> {
  private static final String FILE_ARGUMENT = "file";
  private static final String NEW_FILE_ARGUMENT = "newfile"; // TODO are spaces allowed?

  private final WizardsOfLua wol;
  private final FileSection section;

  public FileMoveCommand(WizardsOfLua wol, FileSection section) {
    this.wol = checkNotNull(wol, "wol==null!");
    this.section = checkNotNull(section, "section==null!");
  }

  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("wol")//
            .then(section.getCommandLiteral()//
                .then(literal("move")//
                    .then(argument(FILE_ARGUMENT, string()) //
                        .then(argument(NEW_FILE_ARGUMENT, string()) //
                            .executes(this))))));
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    String name = StringArgumentType.getString(context, FILE_ARGUMENT);
    String newName = StringArgumentType.getString(context, NEW_FILE_ARGUMENT);

    CommandSource source = context.getSource();
    EntityPlayer player = source.asPlayer();

    if (name != null && newName != null) {
      try {
        if (section == FileSection.PERSONAL) {
          wol.getFileRepository().moveFile(player, name, newName);
        } else if (section == FileSection.SHARED) {
          wol.getFileRepository().moveSharedFile(name, newName);
        }
      } catch (IllegalArgumentException e) {
        throw newCommandException(e.getMessage());
      }
      WolAnnouncementMessage message = new WolAnnouncementMessage(name + " moved to " + newName);
      source.sendFeedback(message, true);
      return Command.SINGLE_SUCCESS;
    } else {
      WolAnnouncementMessage message = new WolAnnouncementMessage(
          "Error - Can't move! To move a file please specify old name and new name");
      source.sendFeedback(message, true);
      return 0; // no success
    }
  }

  private CommandException newCommandException(String string) {
    return new CommandException(new TextComponentString(string));
  }

  // @Override
  // public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
  // Deque<String> argList, BlockPos targetPos) {
  // String name = argList.poll();
  // if (argList.isEmpty()) {
  // Entity entity = sender.getCommandSenderEntity();
  // if (entity instanceof EntityPlayer) {
  // EntityPlayer player = (EntityPlayer) entity;
  // List<String> files = wol.getFileRepository().getLuaFilenames(player);
  // return getMatchingTokens(name, files.subList(0, Math.min(files.size(), MAX_NUM_FILES)));
  // }
  // }
  // return Collections.emptyList();
  // }
  //
  // @Override
  // public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
  // String name = argList.poll();
  // String newName = argList.poll();
  // Entity entity = sender.getCommandSenderEntity();
  // if (entity instanceof EntityPlayer) {
  // EntityPlayer player = (EntityPlayer) entity;
  // if (name != null && newName != null) {
  // try {
  // wol.getFileRepository().moveFile(player, name, newName);
  // } catch (IllegalArgumentException e) {
  // throw new CommandException(e.getMessage());
  // }
  // WolAnnouncementMessage message = new WolAnnouncementMessage(name + " moved to " + newName);
  // sender.sendMessage(message);
  // } else {
  // WolAnnouncementMessage message = new WolAnnouncementMessage(
  // "Error - Can't move! To move a file please specify old name and new name");
  // sender.sendMessage(message);
  // }
  // } else {
  // throw new CommandException("Only players can use this command!");
  // }
  // }

}
