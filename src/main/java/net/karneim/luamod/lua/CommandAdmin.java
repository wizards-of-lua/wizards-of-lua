package net.karneim.luamod.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.karneim.luamod.LuaMod;
import net.karneim.luamod.credentials.AccessTokenCredentials;
import net.karneim.luamod.credentials.Credentials;
import net.karneim.luamod.credentials.Realm;
import net.karneim.luamod.credentials.UsernamePasswordCredentials;
import net.karneim.luamod.gist.GitHubTool;
import net.karneim.luamod.lua.Permissions.AutoWizardPermission;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.ForgeHooks;

public class CommandAdmin extends CommandBase {

  private static final String CMD_NAME = "admin";
  private static final String MSG_USAGE = "commands.admin.usage";

  private static final String UNPAUSE = "unpause";
  private static final String PAUSE = "pause";
  private static final String ALL = "all";
  private static final String BREAK = "break";
  private static final String LIST = "list";
  private static final String CLEAR = "clear";
  private static final String LOGIN = "login";
  private static final String TOKEN = "token";
  private static final String SET = "set";
  private static final String UNSET = "unset";
  private static final String DEFAULT = "default";
  private static final String USER = "user";
  private static final String STARTUP = "startup";
  private static final String TICKSLIMIT = "tickslimit";
  private static final String SPELL = "spell";
  private static final String CACHE = "cache";
  private static final String GITHUB = "github";
  private static final String PROFILE = "profile";
  private static final String PERMISSION = "permission";
  private static final String AUTO = "auto";
  private static final String REVOKE = "revoke";
  private static final String GRANT = "grant";

  private final LuaMod mod;
  private final List<String> aliases = new ArrayList<String>();

  public CommandAdmin() {
    aliases.add(CMD_NAME);
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
            asList(PROFILE, GITHUB, CACHE, SPELL, STARTUP, PERMISSION, TICKSLIMIT));
      }
      if (PROFILE.equals(args[0])) {
        if (args.length <= 2) {
          return getListOfStringsMatchingLastWord(args, asList(USER, DEFAULT, STARTUP));
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
      if (SPELL.equals(args[0])) {
        if (args.length <= 2) {
          return getListOfStringsMatchingLastWord(args, asList(BREAK, LIST, PAUSE, UNPAUSE));
        }
        if (args.length <= 3 && !LIST.equals(args[1])) {
          return getListOfStringsMatchingLastWord(args, asList(getSpellIds(), ALL));
        }
      }
      if (STARTUP.equals(args[0])) {
        if (args.length <= 2) {
          return getListOfStringsMatchingLastWord(args, asList(SET, UNSET));
        }
      }
      if (PERMISSION.equals(args[0])) {
        if (args.length <= 2) {
          return getListOfStringsMatchingLastWord(args, asList(AUTO, GRANT, REVOKE));
        }
        if (args.length <= 3 && AUTO.equals(args[1])) {
          return getListOfStringsMatchingLastWord(args, asList(SET));
        }
        if (args.length <= 4 && AUTO.equals(args[1]) && SET.equals(args[2])) {
          return getListOfStringsMatchingLastWord(args,
              asList(Permissions.AutoWizardPermission.names()));
        }

        if (args.length <= 3 && (GRANT.equals(args[1]) || REVOKE.equals(args[1]))) {
          return getListOfStringsMatchingLastWord(args, server.getAllUsernames());
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
          if (STARTUP.equals(scope)) {
            String action = getArg(args, 2, "action", false);
            if (action == null) {
              ITextComponent message = ForgeHooks.newChatWithLinks(getStartupProfileUrl());
              sender.addChatMessage(message);
              return;
            } else if (SET.equals(action)) {
              String url = getArg(args, 3, "url");
              setStartupProfileUrl(sender, url);
              return;
            } else if (UNSET.equals(action)) {
              setStartupProfileUrl(sender, null);
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
        if (SPELL.equals(section)) {
          String action = getArg(args, 1, "action");
          if (BREAK.equals(action)) {
            String spell = getArg(args, 2, "spell-id");
            if (ALL.equals(spell)) {
              breakAll(sender);
              return;
            } else {
              breakSpell(sender, spell);
              return;
            }
          }
          if (LIST.equals(action)) {
            listActiveSpells(sender);
            return;
          }
          if (PAUSE.equals(action)) {
            String spell = getArg(args, 2, "spell-id");
            if (ALL.equals(spell)) {
              pauseAll(sender);
              return;
            } else {
              pause(sender, spell);
              return;
            }
          }
          if (UNPAUSE.equals(action)) {
            String spell = getArg(args, 2, "spell-id");
            if (ALL.equals(spell)) {
              unpauseAll(sender);
              return;
            } else {
              unpause(sender, spell);
              return;
            }
          }
        }
        if (STARTUP.equals(section)) {
          @Nullable
          String action = getArg(args, 1, "action", false);
          if (action == null) {
            String spell = LuaMod.instance.getStartup().getSpell();
            if (spell == null) {
              spell = "<none>";
            }
            sender.addChatMessage(new TextComponentString("Startup spell: " + spell));
            return;
          } else if (SET.equals(action)) {
            String spell = getRemainingArgs(args, 2, "spell");
            LuaMod.instance.getStartup().setSpell(spell);
            return;
          } else if (UNSET.equals(action)) {
            LuaMod.instance.getStartup().setSpell(null);
            return;
          }
        }
        if (PERMISSION.equals(section)) {
          String scope = getArg(args, 1, "scope");
          if (AUTO.equals(scope)) {
            String action = getArg(args, 2, "action", false);
            if (action == null) {
              AutoWizardPermission value = mod.getPermissions().getAutoWizardPermission();
              ITextComponent message = new TextComponentString(
                  AutoWizardPermission.class.getSimpleName() + ": " + value);
              sender.addChatMessage(message);
              return;
            } else if (SET.equals(action)) {
              String mode = getArg(args, 3, "mode");
              AutoWizardPermission value = AutoWizardPermission.valueOf(mode);
              mod.getPermissions().setAutoWizardPermission(value);
              return;
            }
          }
          if (GRANT.equals(scope)) {
            ITextComponent message =
                new TextComponentString("Sorry - command currently not supported!");
            sender.addChatMessage(message);
            return;
          }
          if (REVOKE.equals(scope)) {
            ITextComponent message =
                new TextComponentString("Sorry - command currently not supported!");
            sender.addChatMessage(message);
            return;
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
      sender.addChatMessage(new TextComponentString("Unknown command!"));
    }
  }

  private void setGithubToken(ICommandSender sender, String token) throws CommandException {
    try {
      if (sender == null || sender.getCommandSenderEntity() == null) {
        throw new CommandException("Command sender is not a player!");
      }
      UUID uuid = sender.getCommandSenderEntity().getUniqueID();
      GitHubTool.checkToken(token);
      mod.getCredentialsStore().storeCredentials(Realm.GitHub, uuid.toString(),
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
      mod.getCredentialsStore().storeCredentials(Realm.GitHub, new AccessTokenCredentials(token));
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
      mod.getCredentialsStore().storeCredentials(Realm.GitHub, uuid.toString(),
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
      mod.getCredentialsStore().storeCredentials(Realm.GitHub,
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
    return toString(mod.getProfiles().getUserProfile(owner));
  }

  private void setUserProfileUrl(ICommandSender sender, @Nullable String urlStr)
      throws CommandException {
    try {
      Entity owner = sender.getCommandSenderEntity();
      URL url = toUrl(urlStr);
      if (url != null) {
        // Check if profile url is accessible and can be loaded
        Credentials credentials = getCredentials(sender);
        mod.getGistRepo().load(credentials, url);
        mod.getProfiles().setUserProfile(owner, url);
        sender.addChatMessage(new TextComponentString("Profile successfully loaded."));
      } else {
        mod.getProfiles().setUserProfile(owner, null);
        sender.addChatMessage(new TextComponentString("Profile removed."));
      }
    } catch (IOException e) {
      throw new CommandException("Can't load gist with id %s! Caught exception with message: %s!",
          urlStr, e.getMessage());
    }
  }

  private String getDefaultProfileUrl() {
    return toString(mod.getProfiles().getDefaultProfile());
  }

  private void setDefaultProfileUrl(ICommandSender sender, @Nullable String urlStr)
      throws CommandException {
    try {
      Entity owner = sender.getCommandSenderEntity();
      URL url = toUrl(urlStr);
      if (url != null) {
        // Check if profile url is accessible and can be loaded
        Credentials credentials = getCredentials(sender);
        mod.getGistRepo().load(credentials, url);
        mod.getProfiles().setDefaultProfile(url);
        sender.addChatMessage(new TextComponentString("Profile successfully loaded."));
      } else {
        mod.getProfiles().setDefaultProfile(null);
        sender.addChatMessage(new TextComponentString("Profile removed."));
      }
    } catch (IOException e) {
      throw new CommandException("Can't load gist with id %s! Caught exception with message: %s!",
          urlStr, e.getMessage());
    }
  }

  private String getStartupProfileUrl() {
    return toString(mod.getProfiles().getStartupProfile());
  }

  private void setStartupProfileUrl(ICommandSender sender, @Nullable String urlStr)
      throws CommandException {
    try {
      Entity owner = sender.getCommandSenderEntity();
      URL url = toUrl(urlStr);
      if (url != null) {
        // Check if profile url is accessible and can be loaded
        Credentials credentials = getCredentials(sender);
        mod.getGistRepo().load(credentials, url);
        mod.getProfiles().setStartupProfile(url);
        sender.addChatMessage(new TextComponentString("Profile successfully loaded."));
      } else {
        mod.getProfiles().setStartupProfile(null);
        sender.addChatMessage(new TextComponentString("Profile removed."));
      }
    } catch (IOException e) {
      throw new CommandException("Can't load gist with id %s! Caught exception with message: %s!",
          urlStr, e.getMessage());
    }
  }

  private @Nullable Credentials getCredentials(@Nullable ICommandSender sender) {
    String userId = null;
    if (sender != null) {
      Entity entity = sender.getCommandSenderEntity();
      if (entity != null && entity.getUniqueID() != null) {
        userId = entity.getUniqueID().toString();
      }
    }
    return mod.getCredentialsStore().retrieveCredentials(Realm.GitHub, userId);
  }

  private List<String> getSpellIds() {
    return mod.getSpellRegistry().getSpellIds();
  }

  private void breakAll(ICommandSender sender) {
    mod.getSpellRegistry().breakAll();
    sender.addChatMessage(new TextComponentString("Broke all spells."));
  }

  private void breakSpell(ICommandSender sender, String spellId) {
    boolean success = mod.getSpellRegistry().breakSpell(spellId);
    if (success) {
      sender.addChatMessage(new TextComponentString("Broke spell."));
    } else {
      sender.addChatMessage(new TextComponentString("Spell not found."));
    }
  }

  private void listActiveSpells(ICommandSender sender) {
    String lines = mod.getSpellRegistry().list();
    sender.addChatMessage(new TextComponentString(String.format("Active spells:\n%s", lines)));
  }

  private void unpauseAll(ICommandSender sender) {
    mod.getSpellRegistry().unpauseAll();
    sender.addChatMessage(new TextComponentString("Unpaused all spells."));
  }

  private void unpause(ICommandSender sender, String spellId) {
    boolean success = mod.getSpellRegistry().unpauseSpell(spellId);
    if (success) {
      sender.addChatMessage(new TextComponentString("Unpaused spell."));
    } else {
      sender.addChatMessage(new TextComponentString("Spell not found."));
    }
  }

  private void pauseAll(ICommandSender sender) {
    mod.getSpellRegistry().pauseAll();
    sender.addChatMessage(new TextComponentString("Paused all spells."));
  }

  private void pause(ICommandSender sender, String spellId) {
    boolean success = mod.getSpellRegistry().pauseSpell(spellId);
    if (success) {
      sender.addChatMessage(new TextComponentString("Paused spell."));
    } else {
      sender.addChatMessage(new TextComponentString("Spell not found."));
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

  private String getRemainingArgs(String[] args, int idx, String name) throws CommandException {
    return getRemainingArgs(args, idx, name, true);
  }

  private String getRemainingArgs(String[] args, int idx, String name, boolean mandatory)
      throws CommandException {
    if (idx >= args.length) {
      if (mandatory) {
        throw new CommandException("Missing %s. argument containing the %s!", idx, name);
      } else {
        return null;
      }
    }
    StringBuilder result = new StringBuilder();
    for (int i = idx; i < args.length; ++i) {
      if (result.length() > 0) {
        result.append(" ");
      }
      result.append(args[i]);
    }
    return result.toString();
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

  private @Nullable URL toUrl(@Nullable String urlStr) throws MalformedURLException {
    return urlStr == null ? null : new URL(urlStr);
  }

  private @Nullable String toString(@Nullable URL result) {
    return result == null ? "<none>" : result.toExternalForm();
  }

  private String getProfileKey(Entity entity) {
    return entity.getUniqueID().toString();
  }
}
