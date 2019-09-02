package net.wizardsoflua.wol.file;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static java.util.Objects.requireNonNull;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import javax.inject.Inject;
import com.google.auto.service.AutoService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.extension.server.spi.CommandRegisterer;
import net.wizardsoflua.file.LuaFileRepository;
public class FileMoveCommand implements CommandRegisterer, Command<CommandSource> {
  @AutoService(CommandRegisterer.class)
  public static class PersonalFileMoveCommand extends FileMoveCommand {
    public PersonalFileMoveCommand() {
      super(FileSection.PERSONAL);
    }
  }

  @AutoService(CommandRegisterer.class)
  public static class SharedFileMoveCommand extends FileMoveCommand {
    public SharedFileMoveCommand() {
      super(FileSection.SHARED);
    }
  }

  private static final String FILE_ARGUMENT = "from-file to-file";

  @Inject
  private LuaFileRepository fileRepo;

  private final FileSection section;

  public FileMoveCommand(FileSection section) {
    this.section = requireNonNull(section, "section");
  }

  @Override
  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("wol")//
            .then(section.getCommandLiteral()//
                .then(literal("move")//
                    .then(argument(FILE_ARGUMENT, greedyString()) //
                        .executes(this)))));
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    CommandSource source = context.getSource();
    EntityPlayer player = source.asPlayer();
    
    String arg = StringArgumentType.getString(context, FILE_ARGUMENT);
    if ( !arg.contains(" ")) {
	  WolAnnouncementMessage message = new WolAnnouncementMessage(
	    "Error - Can't move! To move a file please specify old name and new name");
	  source.sendErrorMessage(message);
	  return 0; // no success
    }
    
    String[] parts = arg.split(" +");
    if (parts.length!=2) {
	  WolAnnouncementMessage message = new WolAnnouncementMessage(
		"Error - Can't move! To move a file please specify old name and new name");
	  source.sendErrorMessage(message);
	  return 0; // no success
    }
    String name = parts[0];
    String newName = parts[1];

    if (name != null && newName != null) {
      try {
        if (section == FileSection.PERSONAL) {
          fileRepo.moveFile(player, name, newName);
        } else if (section == FileSection.SHARED) {
          fileRepo.moveSharedFile(name, newName);
        }
      } catch (IllegalArgumentException e) {
        throw newCommandException(e.getMessage());
      }
      WolAnnouncementMessage message = new WolAnnouncementMessage(name + " moved to " + newName);
      source.sendFeedback(message, false);
      return Command.SINGLE_SUCCESS;
    } else {
      WolAnnouncementMessage message = new WolAnnouncementMessage(
          "Error - Can't move! To move a file please specify old name and new name");
      source.sendErrorMessage(message);
      return 0; // no success
    }
  }

  private CommandException newCommandException(String string) {
    return new CommandException(new TextComponentString(string));
  }
}
