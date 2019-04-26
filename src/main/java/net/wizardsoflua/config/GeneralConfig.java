package net.wizardsoflua.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static net.wizardsoflua.WizardsOfLua.LOGGER;
import static net.wizardsoflua.lua.table.TableUtils.getAsOptional;
import java.io.File;
import javax.annotation.Nullable;
import net.sandius.rembulan.Table;

public class GeneralConfig {

  public interface Context {
    void save();

    File getWolConfigDir();
  }

  public static final int MIN_EVENT_LISTENER_LUA_TICKS_LIMIT = 1000;
  public static final int MAX_EVENT_LISTENER_LUA_TICKS_LIMIT = 10000000;
  public static final int MIN_LUA_TICKS_LIMIT = 1000;
  public static final int MAX_LUA_TICKS_LIMIT = 10000000;

  /**
   * Max. number of Lua ticks a spell can run per game tick [range: 1000 ~ 10000000, default: 50000]
   */
  private int luaTicksLimit = 50000;
  /**
   * Max. number of Lua ticks a event listener can run per event [range: 1000 ~ 10000000, default:
   * 50000]
   */
  private int eventListenerLuaTicksLimit = 50000;
  /**
   * Shows the about message to the player at first login [default: true]
   */
  private boolean showAboutMessage = true;

  private String luaLibDirHome = "libs";
  private final File luaLibDirHomeFile;
  private String sharedLibDir = "shared";
  private final File sharedLibDirFile;
  private String gitHubAccessToken = "";

  private final Context context;

  public GeneralConfig(Context context) {
    this.context = checkNotNull(context, "context==null!");
    luaLibDirHomeFile = tryToCreateDir(new File(context.getWolConfigDir(), luaLibDirHome));
    sharedLibDirFile = tryToCreateDir(new File(luaLibDirHomeFile, sharedLibDir));
  }

  public GeneralConfig(Table table, Context context) {
    this.context = checkNotNull(context, "context==null!");
    setLuaTicksLimit(
        getAsOptional(Integer.class, table, "luaTicksLimit").orElse(luaTicksLimit).intValue(),
        false);
    showAboutMessage = getAsOptional(Boolean.class, table, "showAboutMessage")
        .orElse(showAboutMessage).booleanValue();
    luaLibDirHome = getAsOptional(String.class, table, "luaLibDirHome").orElse(luaLibDirHome);
    sharedLibDir = getAsOptional(String.class, table, "sharedLibDir").orElse(sharedLibDir);
    luaLibDirHomeFile = tryToCreateDir(new File(context.getWolConfigDir(), luaLibDirHome));
    sharedLibDirFile = tryToCreateDir(new File(luaLibDirHomeFile, sharedLibDir));
    gitHubAccessToken =
        getAsOptional(String.class, table, "gitHubAccessToken").orElse(gitHubAccessToken);
  }

  public Table writeTo(Table table) {
    table.rawset("luaTicksLimit", luaTicksLimit);
    table.rawset("eventListenerLuaTickLimit", eventListenerLuaTicksLimit);
    table.rawset("showAboutMessage", showAboutMessage);
    table.rawset("luaLibDirHome", luaLibDirHome);
    table.rawset("sharedLibDir", sharedLibDir);
    table.rawset("gitHubAccessToken", gitHubAccessToken);
    return table;
  }

  public int getLuaTicksLimit() {
    return luaTicksLimit;
  }

  public int setLuaTicksLimit(int luaTicksLimit) {
    return setLuaTicksLimit(luaTicksLimit, true);
  }

  private int setLuaTicksLimit(int luaTicksLimit, boolean save) {
    this.luaTicksLimit = clamp(luaTicksLimit, MIN_LUA_TICKS_LIMIT, MAX_LUA_TICKS_LIMIT);
    if (save) {
      context.save();
    }
    return this.luaTicksLimit;
  }

  public int getEventListenerLuaTicksLimit() {
    return eventListenerLuaTicksLimit;
  }

  public int setEventListenerLuaTicksLimit(int eventListenerLuaTicksLimit) {
    return setEventListenerLuaTicksLimit(eventListenerLuaTicksLimit, true);
  }

  private int setEventListenerLuaTicksLimit(int eventListenerLuaTicksLimit, boolean save) {
    this.eventListenerLuaTicksLimit = clamp(eventListenerLuaTicksLimit,
        MIN_EVENT_LISTENER_LUA_TICKS_LIMIT, MAX_EVENT_LISTENER_LUA_TICKS_LIMIT);
    if (save) {
      context.save();
    }
    return this.eventListenerLuaTicksLimit;
  }

  private int clamp(int value, int min, int max) {
    return Math.min(max, Math.max(min, value));
  }

  public boolean isShowAboutMessage() {
    return showAboutMessage;
  }

  public void setShowAboutMessage(boolean showAboutMessage) {
    this.showAboutMessage = showAboutMessage;
    context.save();
  }

  public File getLuaLibDirHome() {
    return luaLibDirHomeFile;
  }

  public File getSharedLibDir() {
    return sharedLibDirFile;
  }

  private File tryToCreateDir(File dir) {
    if (!dir.exists()) {
      if (!dir.mkdirs()) {
        LOGGER.warn(format("Couldn't create directory at %s because of an unknown reason!",
            dir.getAbsolutePath()));
      }
    }
    return dir;
  }

  public @Nullable String getGitHubAccessToken() {
    return gitHubAccessToken.equals("") ? null : gitHubAccessToken;
  }

}
