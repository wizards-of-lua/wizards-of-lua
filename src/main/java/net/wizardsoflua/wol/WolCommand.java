package net.wizardsoflua.wol;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.minecraftforge.common.ForgeHooks.newChatWithLinks;
import static net.wizardsoflua.config.GeneralConfig.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Logger;

import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.arguments.ParticleArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.WorldServer;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.config.GeneralConfig;
import net.wizardsoflua.file.Crypto;
import net.wizardsoflua.gist.GistFile;
import net.wizardsoflua.gist.RateLimit;
import net.wizardsoflua.gist.RequestRateLimitExceededException;
import net.wizardsoflua.wol.browser.LoginAction;
import net.wizardsoflua.wol.browser.LogoutAction;
import net.wizardsoflua.wol.file.FileDeleteAction;
import net.wizardsoflua.wol.file.FileEditAction;
import net.wizardsoflua.wol.file.FileMoveAction;
import net.wizardsoflua.wol.file.FileSection;
import net.wizardsoflua.wol.gist.GistGetAction;
import net.wizardsoflua.wol.gist.GistGetAction.FileRef;
import net.wizardsoflua.wol.luatickslimit.PrintEventListenerLuaTicksLimitAction;
import net.wizardsoflua.wol.luatickslimit.PrintLuaTicksLimitAction;
import net.wizardsoflua.wol.luatickslimit.SetEventListenerLuaTicksLimitAction;
import net.wizardsoflua.wol.luatickslimit.SetLuaTicksLimitAction;
import net.wizardsoflua.wol.menu.CommandAction;
import net.wizardsoflua.wol.menu.Menu;
import net.wizardsoflua.wol.pack.PackExportAction;
import net.wizardsoflua.wol.sharedfile.SharedFileDeleteAction;
import net.wizardsoflua.wol.sharedfile.SharedFileEditAction;
import net.wizardsoflua.wol.sharedfile.SharedFileMoveAction;
import net.wizardsoflua.wol.spell.SpellBreakAction;
import net.wizardsoflua.wol.spell.SpellListAction;
import net.wizardsoflua.wol.startup.StartupAction;

public class WolCommand {

  private static final String CMD_NAME = "wol";
  private static final String LIMIT_ARGUMENT = "limit";
  private static final String FILE_ARGUMENT = "file";
  private static final String URL_ARGUMENT = "url";
  private static final String DIRECTORY_ARGUMENT = "directory";

  public static void register(CommandDispatcher<CommandSource> dispatcher, WizardsOfLua wol) {
    new WolCommand(wol).register(dispatcher);

  }

  private final WizardsOfLua wol;
  private final Crypto crypto = new Crypto();

  public WolCommand(WizardsOfLua wol) {
    wol = checkNotNull(wol, "wol==null!");
  }

  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(literal("wol")//
        .then(literal("browser")//
            .then(literal("login").executes(this::login))//
            .then(literal("logout").executes(this::logout))//
        )//
        .then(literal("eventListenerLuaTicksLimit")//
            .executes(this::showEventListenerLuaTicksLimit)//
            .then(literal("set").then(setEventListenerLuaTicksLimitCommand()))//
        )//
        .then(literal("file")//
            .then(literal("delete").then(fileDeleteCommand()))//
            .then(literal("edit").then(fileEditCommand()))//
            .then(literal("gist").then(literal("get").then(fileGistCommand())))//
            .then(literal("move").then(fileMoveCommand()))//
        )//
        .then(literal("luaTicksLimit")//
            .executes(this::showLuaTicksLimit)//
            .then(literal("set").then(setLuaTicksLimitCommand()))//
    )//
    );


    LiteralArgumentBuilder builder = literal(CMD_NAME).requires((source) -> {
      return source.getServer().isSinglePlayer() || source.hasPermissionLevel(2);
    });
    builder.then(Commands.argument("browser", StringArgumentType.word()));

    dispatcher.register((LiteralArgumentBuilder) builder.executes((ctx) -> {
      CommandSource source = (CommandSource) ctx.getSource();
      WorldServer world = source.getWorld();
      long seed = world.getSeed();
      ITextComponent itextcomponent = TextComponentUtils.wrapInSquareBrackets(
          new TextComponentString(String.valueOf(seed)).applyTextStyle((style) -> {
            style.setColor(TextFormatting.GREEN)
                .setClickEvent(
                    new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.valueOf(seed)))
                .setInsertion(String.valueOf(seed));
          }));
      source.sendFeedback(
          new TextComponentTranslation("commands.seed.success", new Object[] {itextcomponent}),
          false);
      return (int) seed;
    }));
  }

  private int login(CommandContext<CommandSource> context) {
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

  private int logout(CommandContext<CommandSource> context) {
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

  private int showEventListenerLuaTicksLimit(CommandContext<CommandSource> context) {
    int eventListenerLuaTicksLimit =
        wol.getConfig().getGeneralConfig().getEventListenerLuaTicksLimit();
    WolAnnouncementMessage message =
        new WolAnnouncementMessage("eventListenerLuaTicksLimit = " + eventListenerLuaTicksLimit);
    CommandSource source = context.getSource();
    source.sendFeedback(message, true);
    return Command.SINGLE_SUCCESS;
  }

  private RequiredArgumentBuilder<CommandSource, Integer> setEventListenerLuaTicksLimitCommand() {
    return argument(LIMIT_ARGUMENT,
        integer(MIN_EVENT_LISTENER_LUA_TICKS_LIMIT, MIN_EVENT_LISTENER_LUA_TICKS_LIMIT))
            .executes(this::setEventListenerLuaTicksLimit);
  }

  private int setEventListenerLuaTicksLimit(CommandContext<CommandSource> context) {
    int limit = IntegerArgumentType.getInteger(context, LIMIT_ARGUMENT);
    limit = wol.getConfig().getGeneralConfig().setEventListenerLuaTicksLimit(limit);
    // TODO I18n
    WolAnnouncementMessage message =
        new WolAnnouncementMessage("eventListenerLuaTicksLimit has been updated to " + limit);
    context.getSource().sendFeedback(message, true);
    return Command.SINGLE_SUCCESS;
  }

  // public static final SimpleCommandExceptionType ILLEGAL_PATH_EXCEPTION_TYPE = new
  // SimpleCommandExceptionType(new TextComponentTranslation("chat.type.announcement", new Object[]
  // {"WoL"}));

  private ArgumentBuilder<CommandSource, ?> fileDeleteCommand() {
    return argument(FILE_ARGUMENT, string()).executes(this::deleteFile);
  }

  private int deleteFile(CommandContext<CommandSource> context) {
    String file = StringArgumentType.getString(context, FILE_ARGUMENT);

    CommandSource source = context.getSource();
    EntityPlayer player = source.asPlayer();
    try {
      wol.getFileRepository().deleteFile(player, file);
    } catch (IllegalArgumentException e) {
      throw new CommandSyntaxException(null, new LiteralMessage(e.getMessage()));
    }
    WolAnnouncementMessage message = new WolAnnouncementMessage(file + " deleted.");
    source.sendFeedback(message, true);
    return Command.SINGLE_SUCCESS;
  }

  private ArgumentBuilder<CommandSource, ?> fileEditCommand() {
    return argument(FILE_ARGUMENT, string()).executes(this::editFile);
  }

  private int editFile(CommandContext<CommandSource> context) {
    String file = StringArgumentType.getString(context, FILE_ARGUMENT);

    CommandSource source = context.getSource();
    EntityPlayer player = source.asPlayer();
    URL url;
    try {
      url = wol.getFileRepository().getFileEditURL(player, file);
    } catch (IllegalArgumentException e) {
      throw new CommandSyntaxException(null, new LiteralMessage(e.getMessage()));
    }
    WolAnnouncementMessage message = new WolAnnouncementMessage("Click here to edit: ");
    message.appendSibling(newChatWithLinks(url.toExternalForm(), false));
    source.sendFeedback(message, true);
    return Command.SINGLE_SUCCESS;
  }

  private ArgumentBuilder<CommandSource, ?> fileGistCommand() {
    return argument(URL_ARGUMENT, string())
        .then(argument(DIRECTORY_ARGUMENT, string()).executes(this::gistFile));
  }

  private int gistFile(CommandContext<CommandSource> context) {
    String url = StringArgumentType.getString(context, URL_ARGUMENT);
    String directory = StringArgumentType.getString(context, DIRECTORY_ARGUMENT);

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
        boolean existed = wol.getFileRepository().exists(fileReference.fullPath);
        wol.getFileRepository().saveLuaFile(fileReference.fullPath, content);
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
    return Command.SINGLE_SUCCESS;
  }

  private ArgumentBuilder<CommandSource, ?> fileMoveCommand() {
    // TODO Auto-generated method stub
    return null;
  }

  private int showLuaTicksLimit(CommandContext<CommandSource> context) {
    int luaTicksLimit = wol.getConfig().getGeneralConfig().getLuaTicksLimit();
    WolAnnouncementMessage message = new WolAnnouncementMessage("luaTicksLimit = " + luaTicksLimit);
    CommandSource source = context.getSource();
    source.sendFeedback(message, true);
    return Command.SINGLE_SUCCESS;
  }

  private RequiredArgumentBuilder<CommandSource, Integer> setLuaTicksLimitCommand() {
    return argument(LIMIT_ARGUMENT, integer(MIN_LUA_TICKS_LIMIT, MIN_LUA_TICKS_LIMIT))
        .executes(this::setLuaTicksLimit);
  }

  private int setLuaTicksLimit(CommandContext<CommandSource> context) {
    int limit = IntegerArgumentType.getInteger(context, LIMIT_ARGUMENT);
    limit = wol.getConfig().getGeneralConfig().setLuaTicksLimit(limit);
    // TODO I18n
    WolAnnouncementMessage message =
        new WolAnnouncementMessage("luaTicksLimit has been updated to " + limit);
    context.getSource().sendFeedback(message, true);
    return Command.SINGLE_SUCCESS;
  }


  /**
   * Pattern used to re-tokenize the arguments by taking quoted strings into account.
   */
  private static final Pattern TOKEN = Pattern.compile("\"([^\"]*)\"|(\\S+)");

  private final List<String> aliases = new ArrayList<>();

  private final WizardsOfLua wol;
  private final Logger logger;
  private final Menu menu;

  public WolCommand(WizardsOfLua wol, Logger logger) {
    this.wol = wol;
    this.logger = logger;
    menu = new WolMenu();
    aliases.add(CMD_NAME);
  }

  class WolMenu extends Menu {
    WolMenu() {
      put("browser", new BrowserMenu());
      put("eventListenerLuaTicksLimit", new EventListenerLuaTicksLimitMenu());
      put("file", new FileMenu());
      put("luaTicksLimit", new LuaTicksLimitMenu());
      put("pack", new PackMenu());
      put("shared-file", new SharedFileMenu());
      put("spell", new SpellMenu());
      put("startup", new StartupAction(wol));
    }
  }
  class FileMenu extends Menu {
    FileMenu() {
      put("delete", new FileDeleteAction(wol));
      put("edit", new FileEditAction(wol));
      put("gist", new GistMenu(FileSection.PERSONAL));
      put("move", new FileMoveAction(wol));
    }
  }
  class GistMenu extends Menu {
    GistMenu(FileSection section) {
      put("get", new GistGetAction(wol, logger, section));
    }
  }
  class SharedFileMenu extends Menu {
    SharedFileMenu() {
      put("delete", new SharedFileDeleteAction(wol));
      put("edit", new SharedFileEditAction(wol));
      put("gist", new GistMenu(FileSection.SHARED));
      put("move", new SharedFileMoveAction(wol));
    }
  }
  class SpellMenu extends Menu {
    SpellMenu() {
      put("list", new SpellListAction(wol));
      put("break", new SpellBreakAction(wol));
    }
  }
  class LuaTicksLimitMenu extends Menu {
    LuaTicksLimitMenu() {
      put(new PrintLuaTicksLimitAction(wol));
      put("set", new SetLuaTicksLimitAction(wol));
    }
  }
  class EventListenerLuaTicksLimitMenu extends Menu {
    EventListenerLuaTicksLimitMenu() {
      put(new PrintEventListenerLuaTicksLimitAction(wol));
      put("set", new SetEventListenerLuaTicksLimitAction(wol));
    }
  }
  class BrowserMenu extends Menu {
    BrowserMenu() {
      put("login", new LoginAction(wol));
      put("logout", new LogoutAction(wol));
    }
  }
  class PackMenu extends Menu {
    PackMenu() {
      put("export", new PackExportAction(wol));
    }
  }

  @Override
  public String getName() {
    return CMD_NAME;
  }

  @Override
  public String getUsage(ICommandSender sender) {
    // TODO return usage
    return "";
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
      String[] args, BlockPos targetPos) {
    return menu.getTabCompletions(server, sender, newArrayDeque(args), targetPos);
  }

  /**
   * Return the required permission level for this command.
   */
  @Override
  public int getRequiredPermissionLevel() {
    // TODO add real permission checking somewhere
    return 2;
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    Deque<String> argList = newArrayDeque(args);
    CommandAction action = menu.getAction(server, sender, argList);
    action.execute(sender, argList);
  }

  private Deque<String> newArrayDeque(String[] args) throws IllegalArgumentException {
    ArrayDeque<String> result = new ArrayDeque<>();
    if (args == null || args.length == 0) {
      return result;
    }
    String all = Joiner.on(" ").join(args);
    Matcher m = TOKEN.matcher(all);
    String next = null;
    while (m.find()) {
      if (m.group(1) != null) {
        next = m.group(1);
      } else {
        next = m.group(2);
      }
      if (next != null) {
        if (next.contains("\"")) {
          // TODO I18n
          throw new IllegalArgumentException("Unmatched quotes!");
        }
        result.add(next);
      }
    }
    if (args[args.length - 1].isEmpty()) {
      result.add("");
    }
    return result;
  }

}
