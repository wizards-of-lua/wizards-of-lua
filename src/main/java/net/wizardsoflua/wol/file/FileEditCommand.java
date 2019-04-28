package net.wizardsoflua.wol.file;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static java.util.Objects.requireNonNull;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraftforge.common.ForgeHooks.newChatWithLinks;
import java.net.URL;
import javax.inject.Inject;
import com.google.auto.service.AutoService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.EntityPlayer;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.extension.server.spi.CommandRegisterer;
import net.wizardsoflua.file.LuaFileRepository;

public class FileEditCommand implements CommandRegisterer, Command<CommandSource> {
  @AutoService(CommandRegisterer.class)
  public static class PersonalFileEditCommand extends FileEditCommand {
    public PersonalFileEditCommand() {
      super(FileSection.PERSONAL);
    }
  }

  @AutoService(CommandRegisterer.class)
  public static class SharedFileEditCommand extends FileEditCommand {
    public SharedFileEditCommand() {
      super(FileSection.SHARED);
    }
  }

  private static final String FILE_ARGUMENT = "file";

  @Inject
  private LuaFileRepository fileRepo;

  private final FileSection section;

  public FileEditCommand(FileSection section) {
    this.section = requireNonNull(section, "section");
  }

  @Override
  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("wol")//
            .then(section.getCommandLiteral()//
                .then(literal("edit")//
                    .then(argument(FILE_ARGUMENT, string())//
                        .executes(this)))));
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    String file = StringArgumentType.getString(context, FILE_ARGUMENT);

    CommandSource source = context.getSource();
    EntityPlayer player = source.asPlayer();
    URL url = null;
    try {
      if (section == FileSection.PERSONAL) {
        url = fileRepo.getFileEditURL(player, file);
      } else if (section == FileSection.SHARED) {
        url = fileRepo.getSharedFileEditURL(file);
      }
    } catch (IllegalArgumentException e) {
      throw new CommandSyntaxException(null, new LiteralMessage(e.getMessage()));
    }
    WolAnnouncementMessage message = new WolAnnouncementMessage("Click here to edit: ");
    message.appendSibling(newChatWithLinks(url.toExternalForm(), false));
    source.sendFeedback(message, true);
    return Command.SINGLE_SUCCESS;
  }

  // private static final int MAX_NUM_FILES = 500;
  // @Override
  // public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
  // Deque<String> argList, BlockPos targetPos) {
  // String name = argList.poll();
  //
  // Entity entity = sender.getCommandSenderEntity();
  // if (entity instanceof EntityPlayer) {
  // EntityPlayer player = (EntityPlayer) entity;
  // List<String> files = wol.getFileRepository().getLuaFilenames(player);
  // return getMatchingTokens(name, files.subList(0, Math.min(files.size(), MAX_NUM_FILES)));
  // }
  // return Collections.emptyList();
  // }
  //
  // @Override
  // public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
  // String name = argList.poll();
  //
  // Entity entity = sender.getCommandSenderEntity();
  // if (entity instanceof EntityPlayer) {
  // EntityPlayer player = (EntityPlayer) entity;
  // URL url;
  // try {
  // url = wol.getFileRepository().getFileEditURL(player, name);
  // } catch (IllegalArgumentException e) {
  // throw new CommandException(e.getMessage());
  // }
  // WolAnnouncementMessage message = new WolAnnouncementMessage("Click here to edit: ");
  // message.appendSibling(newChatWithLinks(url.toExternalForm(), false));
  // sender.sendMessage(message);
  // } else {
  // throw new CommandException("Only players can use this command!");
  // }
  // }

}
