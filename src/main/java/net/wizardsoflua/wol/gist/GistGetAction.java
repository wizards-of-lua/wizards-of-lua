package net.wizardsoflua.wol.gist;

import java.io.IOException;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Logger;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.gist.GistFile;
import net.wizardsoflua.gist.RateLimit;
import net.wizardsoflua.gist.RequestRateLimitExceededException;
import net.wizardsoflua.wol.file.FileSection;
import net.wizardsoflua.wol.menu.CommandAction;
import net.wizardsoflua.wol.menu.MenuEntry;

public class GistGetAction extends MenuEntry implements CommandAction {

  private final WizardsOfLua wol;
  private final Logger logger;
  private final FileSection section;

  public GistGetAction(WizardsOfLua wol, Logger logger, FileSection section) {
    this.wol = wol;
    this.logger = logger;
    this.section = section;
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      Deque<String> argList, BlockPos targetPos) {
    return Collections.emptyList();
  }

  @Override
  public void execute(ICommandSender sender, Deque<String> argList) throws CommandException {
    EntityPlayer player = getPlayer(sender);
    if (section == FileSection.PERSONAL && player == null) {
      throw new CommandException("Only players can use this command!");
    }

    try {
      @Nullable
      String url = argList.poll();
      if (url == null) {
        String pattern = "https://gist.github.com/yourgithubusername/yourgistid";
        throw new CommandException("Missing Gist URL. Expected something like %s", pattern);
      }
      @Nullable
      String directory = argList.poll();

      String accessToken = wol.getConfig().getGeneralConfig().getGitHubAccessToken();
      logger.info("Loading Gist " + url);
      List<GistFile> files = wol.getGistRepo().getGistFiles(url, accessToken);
      for (GistFile gistFile : files) {
        FileRef fileReference = toFileReference(player, directory, gistFile);
        String content = gistFile.content;
        boolean existed = wol.getFileRegistry().exists(fileReference.fullPath);
        wol.getFileRegistry().saveLuaFile(fileReference.fullPath, content);
        String action = existed ? "updated" : "created";
        WolAnnouncementMessage message =
            new WolAnnouncementMessage(fileReference.localPath + " " + action + ".");
        sender.sendMessage(message);
      }
      if (accessToken == null) {
        RateLimit rateLimit = wol.getGistRepo().getRateLimitRemaining(accessToken);
        if (rateLimit.remaining < 10) {
          logger.warn("This server is close to exceed the GitHub request rate limit of "
              + rateLimit.limit + " calls per hour!");
        }
        logger.info(String.format(
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
      logger.error(message);
      throw new CommandException(message);
    } catch (IOException | RuntimeException e) {
      throw new CommandException(e.getMessage());
    }
  }

  private @Nullable EntityPlayer getPlayer(ICommandSender sender) {
    Entity entity = sender.getCommandSenderEntity();
    if (entity instanceof EntityPlayer) {
      return (EntityPlayer) entity;
    }
    return null;
  }

  private FileRef toFileReference(EntityPlayer owner, @Nullable String directory,
      GistFile gistFile) {
    String localPath = directory == null ? gistFile.filename : directory + "/" + gistFile.filename;
    switch (section) {
      case PERSONAL:
        return new FileRef(localPath, wol.getFileRegistry().getFileReferenceFor(owner, localPath));
      case SHARED:
        return new FileRef(localPath, wol.getFileRegistry().getSharedFileReferenceFor(localPath));
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
