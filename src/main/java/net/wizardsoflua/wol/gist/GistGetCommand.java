package net.wizardsoflua.wol.gist;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.wizardsoflua.WizardsOfLua.LOGGER;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
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
import net.wizardsoflua.WolServer;
import net.wizardsoflua.gist.GistFile;
import net.wizardsoflua.gist.RateLimit;
import net.wizardsoflua.gist.RequestRateLimitExceededException;
import net.wizardsoflua.wol.file.FileSection;

public class GistGetCommand implements Command<CommandSource> {
  private static final String URL_ARGUMENT = "url";
  private static final String DIRECTORY_ARGUMENT = "directory";

  private final WolServer wol;
  private final FileSection section;

  public GistGetCommand(WolServer wol, FileSection section) {
    this.wol = wol;
    this.section = section;
  }

  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("wol")//
            .then(section.getCommandLiteral()//
                .then(literal("gist")//
                    .then(literal("get")//
                        .then(argument(URL_ARGUMENT, string())//
                            .then(argument(DIRECTORY_ARGUMENT, string())//
                                .executes(this)))))));
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    // e.g. https://gist.github.com/yourgithubusername/yourgistid
    String url = StringArgumentType.getString(context, URL_ARGUMENT);
    @Nullable // TODO make it optional
    String directory = StringArgumentType.getString(context, DIRECTORY_ARGUMENT);

    CommandSource source = context.getSource();
    EntityPlayer player = source.asPlayer();
    if (section == FileSection.PERSONAL && player == null) {
      // TODO I18n
      throw newCommandException("Only players can use this command!");
    }

    try {
      String accessToken = wol.getConfig().getGeneralConfig().getGitHubAccessToken();
      LOGGER.info("Loading Gist " + url);
      List<GistFile> files = wol.getGistRepo().getGistFiles(url, accessToken);
      for (GistFile gistFile : files) {
        FileRef fileReference = toFileReference(player, directory, gistFile);
        String content = gistFile.content;
        boolean existed = wol.getFileRepository().exists(fileReference.fullPath);
        wol.getFileRepository().saveLuaFile(fileReference.fullPath, content);
        String action = existed ? "updated" : "created";
        WolAnnouncementMessage message =
            new WolAnnouncementMessage(fileReference.localPath + " " + action + ".");
        source.sendFeedback(message, true);
      }
      if (accessToken == null) {
        RateLimit rateLimit = wol.getGistRepo().getRateLimitRemaining(accessToken);
        if (rateLimit.remaining < 10) {
          LOGGER.warn("This server is close to exceed the GitHub request rate limit of "
              + rateLimit.limit + " calls per hour!");
        }
        LOGGER.info(String.format(
            "%s REST calls to GitHub remain until the limit of %s is reached. Consider using a GitHub access token to increase this limit.",
            rateLimit.remaining, rateLimit.limit));
      }
    } catch (RequestRateLimitExceededException e) {
      // TODO I18n
      String message = String.format(
          "Couldn't load Gist! This server's request rate limit of %s REST calls per hour to GitHub has been exceeded.",
          e.getRateLimit().limit);
      if (!e.requestWasAuthorized()) {
        message += " Consider using a GitHub access token to increase this limit.";
      }
      LOGGER.error(message);
      throw newCommandException(message);
    } catch (IOException | RuntimeException e) {
      throw newCommandException(e.getMessage());
    }
    return Command.SINGLE_SUCCESS;
  }

  private CommandException newCommandException(String string) {
    return new CommandException(new TextComponentString(string));
  }

  // @Override
  // public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
  // Deque<String> argList, BlockPos targetPos) {
  // return Collections.emptyList();
  // }
  //
  // @Override
  // public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
  // EntityPlayer player = getPlayer(sender);
  // if (section == FileSection.PERSONAL && player == null) {
  // throw new CommandException("Only players can use this command!");
  // }
  //
  // try {
  // @Nullable
  // String url = argList.poll();
  // if (url == null) {
  // String pattern = "https://gist.github.com/yourgithubusername/yourgistid";
  // throw new CommandException("Missing Gist URL. Expected something like %s", pattern);
  // }
  // @Nullable
  // String directory = argList.poll();
  //
  // String accessToken = wol.getConfig().getGeneralConfig().getGitHubAccessToken();
  // logger.info("Loading Gist " + url);
  // List<GistFile> files = wol.getGistRepo().getGistFiles(url, accessToken);
  // for (GistFile gistFile : files) {
  // FileRef fileReference = toFileReference(player, directory, gistFile);
  // String content = gistFile.content;
  // boolean existed = wol.getFileRepository().exists(fileReference.fullPath);
  // wol.getFileRepository().saveLuaFile(fileReference.fullPath, content);
  // String action = existed ? "updated" : "created";
  // WolAnnouncementMessage message =
  // new WolAnnouncementMessage(fileReference.localPath + " " + action + ".");
  // sender.sendMessage(message);
  // }
  // if (accessToken == null) {
  // RateLimit rateLimit = wol.getGistRepo().getRateLimitRemaining(accessToken);
  // if (rateLimit.remaining < 10) {
  // logger.warn("This server is close to exceed the GitHub request rate limit of "
  // + rateLimit.limit + " calls per hour!");
  // }
  // logger.info(String.format(
  // "%s REST calls to GitHub remain until the limit of %s is reached. Consider using a GitHub
  // access token to increase this limit.",
  // rateLimit.remaining, rateLimit.limit));
  // }
  // } catch (RequestRateLimitExceededException e) {
  // // TODO I18n
  // String message = String.format(
  // "Couldn't load Gist! This server's request rate limit of %s REST calls per hour to GitHub has
  // been exceeded.",
  // e.getRateLimit().limit);
  // if (!e.requestWasAuthorized()) {
  // message += " Consider using a GitHub access token to increase this limit.";
  // }
  // logger.error(message);
  // throw new CommandException(message);
  // } catch (IOException | RuntimeException e) {
  // throw new CommandException(e.getMessage());
  // }
  // }
  //
  // private @Nullable EntityPlayer getPlayer(ICommandSender sender) {
  // Entity entity = sender.getCommandSenderEntity();
  // if (entity instanceof EntityPlayer) {
  // return (EntityPlayer) entity;
  // }
  // return null;
  // }

  private FileRef toFileReference(EntityPlayer owner, @Nullable String directory,
      GistFile gistFile) {
    String localPath = directory == null ? gistFile.filename : directory + "/" + gistFile.filename;
    switch (section) {
      case PERSONAL:
        return new FileRef(localPath,
            wol.getFileRepository().getFileReferenceFor(owner, localPath));
      case SHARED:
        return new FileRef(localPath, wol.getFileRepository().getSharedFileReferenceFor(localPath));
      default:
        throw new IllegalStateException("Unexpected section: " + section);
    }
  }

  private class FileRef {
    final String localPath;
    final String fullPath;

    public FileRef(String localPath, String fullPath) {
      this.localPath = localPath;
      this.fullPath = fullPath;
    }
  }

}
