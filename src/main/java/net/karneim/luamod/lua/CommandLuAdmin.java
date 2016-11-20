package net.karneim.luamod.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.karneim.luamod.LuaMod;
import net.karneim.luamod.credentials.AccessTokenCredentials;
import net.karneim.luamod.credentials.Credentials;
import net.karneim.luamod.credentials.UsernamePasswordCredentials;
import net.karneim.luamod.gist.GitHubTool;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.ForgeHooks;

public class CommandLuAdmin extends CommandBase {

  private static final String CMD_NAME = "luadmin";
  private static final String MSG_USAGE = "commands.luadm.usage";

  private static final String UNPAUSE = "unpause";
  private static final String PAUSE = "pause";
  private static final String ALL = "all";
  private static final String KILL = "kill";
  private static final String LIST = "list";
  private static final String CLEAR = "clear";
  private static final String LOGIN = "login";
  private static final String TOKEN = "token";
  private static final String SET = "set";
  private static final String UNSET = "unset";
  private static final String DEFAULT = "default";
  private static final String USER = "user";
  private static final String TICKSLIMIT = "tickslimit";
  private static final String PROCESS = "process";
  private static final String CACHE = "cache";
  private static final String GITHUB = "github";
  private static final String PROFILE = "profile";


  private final LuaMod mod;
  private final List<String> aliases = new ArrayList<String>();

  public CommandLuAdmin() {
    aliases.add(CMD_NAME);
    aliases.add("luadm");
    mod = LuaMod.instance;
  }

  @Override
  public String getCommandName() {
    return CMD_NAME;
  }

  @Override
  public int getRequiredPermissionLevel() {
    // return 2;
    return 0;
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return MSG_USAGE;
  }

  @Override
  public List getCommandAliases() {
    return aliases;
  }

  @Override
  public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender,
      String[] args, @Nullable BlockPos pos) {
    if (args.length > 0) {
      if (args.length <= 0) {
        return Collections.<String>emptyList();
      }
      if (args.length <= 1) {
        return getListOfStringsMatchingLastWord(args,
            asList(PROFILE, GITHUB, CACHE, PROCESS, TICKSLIMIT));
      }
      if (PROFILE.equals(args[0])) {
        if (args.length <= 2) {
          return getListOfStringsMatchingLastWord(args, asList(USER, DEFAULT));
        }
        if (args.length <= 3) {
          return getListOfStringsMatchingLastWord(args, asList(SET, UNSET));
        }
      }
      if (GITHUB.equals(args[0])) {
        if (args.length <= 2) {
          return getListOfStringsMatchingLastWord(args, asList(USER, DEFAULT));
        }
        if (args.length <= 3) {
          return getListOfStringsMatchingLastWord(args, asList(LOGIN, TOKEN));
        }
        if (args.length <= 4) {
          return getListOfStringsMatchingLastWord(args, asList(SET, CLEAR));
        }
      }
      if (CACHE.equals(args[0])) {
        if (args.length <= 2) {
          return getListOfStringsMatchingLastWord(args, asList(CLEAR));
        }
      }
      if (PROCESS.equals(args[0])) {
        if (args.length <= 2) {
          return getListOfStringsMatchingLastWord(args, asList(KILL, LIST, PAUSE, UNPAUSE));
        }
        if (args.length <= 3) {
          return getListOfStringsMatchingLastWord(args, asList(getProcessNames(), ALL));
        }
      }
      if (TICKSLIMIT.equals(args[0])) {
        if (args.length <= 2) {
          return getListOfStringsMatchingLastWord(args, asList(SET));
        }
        if (args.length <= 3) {
          return getListOfStringsMatchingLastWord(args,
              asList("100", "1000", "10000", "100000", "1000000"));
        }
      }
    }

    return Collections.<String>emptyList();
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    if (!sender.getEntityWorld().isRemote) {
      if (args.length > 0) {
        String section = getArg(args, 0, "section");
        if (PROFILE.equals(section)) {
          String scope = getArg(args, 1, "scope");
          if (USER.equals(scope)) {
            String action = getArg(args, 2, "action", false);
            if (action == null) {
              ITextComponent message = ForgeHooks.newChatWithLinks(getUserProfileUrl(sender));
              sender.addChatMessage(message);
              return;
            } else if (SET.equals(action)) {
              String url = getArg(args, 3, "url");
              setUserProfileUrl(sender, url);
              return;
            } else if (UNSET.equals(action)) {
              setUserProfileUrl(sender, null);
              return;
            }
          }
          if (DEFAULT.equals(scope)) {
            String action = getArg(args, 2, "action", false);
            if (action == null) {
              ITextComponent message = ForgeHooks.newChatWithLinks(getDefaultProfileUrl());
              sender.addChatMessage(message);
              return;
            } else if (SET.equals(action)) {
              String url = getArg(args, 3, "url");
              setDefaultProfileUrl(sender, url);
              return;
            } else if (UNSET.equals(action)) {
              setDefaultProfileUrl(sender, null);
              return;
            }
          }
        }
        if (GITHUB.equals(section)) {
          String scope = getArg(args, 1, "scope");
          if (USER.equals(scope)) {
            String type = getArg(args, 2, "type");
            if (LOGIN.equals(type)) {
              String action = getArg(args, 3, "action");
              if (SET.equals(action)) {
                String user = getArg(args, 4, USER);
                String pw = getArg(args, 5, "password");
                setGithubLogin(sender, user, pw);
                return;
              }
            }
            if (TOKEN.equals(type)) {
              String action = getArg(args, 3, "action");
              if (SET.equals(action)) {
                String token = getArg(args, 4, TOKEN);
                setGithubToken(sender, token);
                return;
              }
            }
          }
          if (DEFAULT.equals(scope)) {
            String type = getArg(args, 2, "type");
            if (LOGIN.equals(type)) {
              String action = getArg(args, 3, "action");
              if (SET.equals(action)) {
                String user = getArg(args, 4, USER);
                String pw = getArg(args, 5, "password");
                setDefaultGithubLogin(sender, user, pw);
                return;
              }
            }
            if (TOKEN.equals(type)) {
              String action = getArg(args, 3, "action");
              if (SET.equals(action)) {
                String token = getArg(args, 4, TOKEN);
                setDefaultGithubToken(sender, token);
                return;
              }
            }
          }
        }
        if (CACHE.equals(section)) {
          String action = getArg(args, 1, "action");
          if (CLEAR.equals(action)) {
            clearCache(sender);
            return;
          }
        }
        if (PROCESS.equals(section)) {
          String action = getArg(args, 1, "action");
          if (KILL.equals(action)) {
            String pid = getArg(args, 2, "pid");
            if (ALL.equals(pid)) {
              killAll(sender);
              return;
            } else {
              kill(sender, pid);
              return;
            }
          }
          if (LIST.equals(action)) {
            listProcesses(sender);
            return;
          }
          if (PAUSE.equals(action)) {
            String pid = getArg(args, 2, "pid");
            if (ALL.equals(pid)) {
              pauseAll(sender);
              return;
            } else {
              pause(sender, pid);
              return;
            }
          }
          if (UNPAUSE.equals(action)) {
            String pid = getArg(args, 2, "pid");
            if (ALL.equals(pid)) {
              unpauseAll(sender);
              return;
            } else {
              unpause(sender, pid);
              return;
            }
          }
        }
        if (TICKSLIMIT.equals(section)) {
          @Nullable
          String action = getArg(args, 1, "action", false);
          if (action == null) {
            sender.addChatMessage(new TextComponentString(
                "Max. Lua ticks per game tick: " + LuaMod.instance.getDefaultTicksLimit()));
            return;
          } else if (SET.equals(action)) {
            Long value = getArgAsLong(args, 2, "value");
            LuaMod.instance.setDefaultTicksLimit(value);
            return;
          }
        }
      }
      sender.addChatMessage(new TextComponentString("Command not supported!"));
    }
  }

  private void setGithubToken(ICommandSender sender, String token) throws CommandException {
    try {
      if (sender == null || sender.getCommandSenderEntity() == null) {
        throw new CommandException("Command sender is not a player!");
      }
      UUID uuid = sender.getCommandSenderEntity().getUniqueID();
      GitHubTool.checkToken(token);
      mod.getCredentialsStore().storeCredentials("GitHub", uuid.toString(),
          new AccessTokenCredentials(token));
      sender.addChatMessage(new TextComponentString("Successfully authenticated with GitHub."));
    } catch (IOException e) {
      throw new CommandException(
          "Can't authenticate with GitHub! Caught exception with message: %s!", e.getMessage());
    }
  }

  private void setDefaultGithubToken(ICommandSender sender, String token) throws CommandException {
    try {
      GitHubTool.checkToken(token);
      mod.getCredentialsStore().storeCredentials("GitHub", "default",
          new AccessTokenCredentials(token));
      sender.addChatMessage(new TextComponentString("Successfully authenticated with GitHub."));
    } catch (IOException e) {
      throw new CommandException(
          "Can't authenticate with GitHub! Caught exception with message: %s!", e.getMessage());
    }
  }

  private void setGithubLogin(ICommandSender sender, String username, String password)
      throws CommandException {
    try {
      if (sender == null || sender.getCommandSenderEntity() == null) {
        throw new CommandException("Command sender is not a player!");
      }
      UUID uuid = sender.getCommandSenderEntity().getUniqueID();
      GitHubTool.checklogin(username, password);
      mod.getCredentialsStore().storeCredentials("GitHub", uuid.toString(),
          new UsernamePasswordCredentials(username, password));
      sender.addChatMessage(new TextComponentString("Successfully authenticated with GitHub."));
    } catch (IOException e) {
      throw new CommandException(
          "Can't authenticate with GitHub! Caught exception with message: %s!", e.getMessage());
    }
  }

  private void setDefaultGithubLogin(ICommandSender sender, String username, String password)
      throws CommandException {
    try {
      GitHubTool.checklogin(username, password);
      mod.getCredentialsStore().storeCredentials("GitHub", "default",
          new UsernamePasswordCredentials(username, password));
      sender.addChatMessage(new TextComponentString("Successfully authenticated with GitHub."));
    } catch (IOException e) {
      throw new CommandException(
          "Can't authenticate with GitHub! Caught exception with message: %s!", e.getMessage());
    }
  }

  private void clearCache(ICommandSender sender) throws CommandException {
    try {
      LuaMod.instance.getLuaCache().clear();
      sender.addChatMessage(new TextComponentString("Cache cleared!"));
    } catch (IOException e) {
      throw new CommandException("Can't clear lua cache! Caught exception with message: %s!",
          e.getMessage());
    }
  }

  private String getUserProfileUrl(ICommandSender sender) {
    Entity owner = sender.getCommandSenderEntity();
    return mod.getProfileUrls().getProfileUrl(owner);
  }

  private void setUserProfileUrl(ICommandSender sender, @Nullable String profileUrl)
      throws CommandException {
    try {
      Entity owner = sender.getCommandSenderEntity();
      if (profileUrl != null) {
        // Check if profile url is accessible and can be loaded
        Credentials credentials = getCredentials(sender);
        mod.getGistRepo().load(credentials, profileUrl);
      }
      mod.getProfileUrls().setProfileUrl(owner, profileUrl);
      sender.addChatMessage(new TextComponentString("Gist successfully loaded."));
    } catch (IOException e) {
      throw new CommandException("Can't load gist with id %s! Caught exception with message: %s!",
          profileUrl, e.getMessage());
    }
  }

  private String getDefaultProfileUrl() {
    return mod.getProfileUrls().getDefaultProfileUrl();
  }

  private void setDefaultProfileUrl(ICommandSender sender, @Nullable String profileUrl)
      throws CommandException {
    try {
      Entity owner = sender.getCommandSenderEntity();
      if (profileUrl != null) {
        // Check if profile url is accessible and can be loaded
        Credentials credentials = getCredentials(sender);
        mod.getGistRepo().load(credentials, profileUrl);
      }
      mod.getProfileUrls().setDefaultProfileUrl(profileUrl);
      sender.addChatMessage(new TextComponentString("Gist successfully loaded."));
    } catch (IOException e) {
      throw new CommandException("Can't load gist with id %s! Caught exception with message: %s!",
          profileUrl, e.getMessage());
    }
  }

  private @Nullable Credentials getCredentials(@Nullable ICommandSender sender) {
    if (sender == null) {
      return null;
    }
    Entity entity = sender.getCommandSenderEntity();
    if (entity == null) {
      return null;
    }
    UUID id = entity.getUniqueID();
    if (id == null) {
      return null;
    }
    return mod.getCredentialsStore().retrieveCredentials("GitHub", id.toString());
  }

  private List<String> getProcessNames() {
    return mod.getProcessRegistry().getNames();
  }

  private void killAll(ICommandSender sender) {
    mod.getProcessRegistry().killAll();
    sender.addChatMessage(new TextComponentString("Killed all lua processes."));
  }

  private void kill(ICommandSender sender, String pid) {
    boolean success = mod.getProcessRegistry().kill(pid);
    if (success) {
      sender.addChatMessage(new TextComponentString("Killed lua process."));
    } else {
      sender.addChatMessage(new TextComponentString("Lua process not found."));
    }
  }

  private void listProcesses(ICommandSender sender) {
    String lines = mod.getProcessRegistry().list();
    sender.addChatMessage(new TextComponentString(String.format("Lua process list:\n%s", lines)));
  }

  private void unpauseAll(ICommandSender sender) {
    mod.getProcessRegistry().unpauseAll();
    sender.addChatMessage(new TextComponentString("Unpaused all lua processes."));
  }

  private void unpause(ICommandSender sender, String pid) {
    boolean success = mod.getProcessRegistry().unpause(pid);
    if (success) {
      sender.addChatMessage(new TextComponentString("Unpaused lua process."));
    } else {
      sender.addChatMessage(new TextComponentString("Lua process not found."));
    }
  }

  private void pauseAll(ICommandSender sender) {
    mod.getProcessRegistry().pauseAll();
    sender.addChatMessage(new TextComponentString("Paused all lua processes."));
  }

  private void pause(ICommandSender sender, String pid) {
    boolean success = mod.getProcessRegistry().pause(pid);
    if (success) {
      sender.addChatMessage(new TextComponentString("Paused lua process."));
    } else {
      sender.addChatMessage(new TextComponentString("Lua process not found."));
    }
  }


  private String getArg(String[] args, int idx, String name) throws CommandException {
    String result = getArg(args, idx, name, true);
    return checkNotNull(result);
  }

  private @Nullable String getArg(String[] args, int idx, String name, boolean mandatory)
      throws CommandException {
    if (idx >= args.length) {
      if (mandatory) {
        throw new CommandException("Missing %s. argument containing the %s!", idx, name);
      } else {
        return null;
      }
    }
    return args[idx];
  }

  private Long getArgAsLong(String[] args, int idx, String name) throws CommandException {
    return checkNotNull(getArgAsLong(args, idx, name, true));
  }

  private @Nullable Long getArgAsLong(String[] args, int idx, String name, boolean mandatory)
      throws CommandException {
    @Nullable
    String str = getArg(args, idx, name, mandatory);
    if (str == null) {
      return null;
    }
    try {
      return Long.parseLong(str);
    } catch (NumberFormatException e) {
      throw new CommandException("Bad value for %s. argument supplied for %s! Not a number!", idx,
          name);
    }
  }

  private List<String> asList(String... string) {
    return Arrays.asList(string);
  }

  private List<String> asList(List<String> list, String... string) {
    List<String> result = list;
    for (String str : string) {
      result.add(str);
    }
    return result;
  }

}
