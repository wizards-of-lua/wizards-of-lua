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

  /**
   * Max. number of Lua ticks a spell can run per game tick [default: 50000]
   */
  private long luaTicksLimit = 50000;
  /**
   * Max. number of Lua ticks a event listener can run per event [default: 50000]
   */
  private long eventListenerLuaTicksLimit = 50000;
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
    luaTicksLimit = getAsOptional(Long.class, table, "luaTicksLimit").orElse(luaTicksLimit);
    eventListenerLuaTicksLimit = getAsOptional(Long.class, table, "eventListenerLuaTicksLimit")
        .orElse(eventListenerLuaTicksLimit);
    showAboutMessage =
        getAsOptional(Boolean.class, table, "showAboutMessage").orElse(showAboutMessage);
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

  public long getLuaTicksLimit() {
    return luaTicksLimit;
  }

  public void setLuaTicksLimit(long luaTicksLimit) {
    this.luaTicksLimit = luaTicksLimit;
    context.save();
  }

  public long getEventListenerLuaTicksLimit() {
    return eventListenerLuaTicksLimit;
  }

  public void setEventListenerLuaTicksLimit(long eventListenerLuaTicksLimit) {
    this.eventListenerLuaTicksLimit = eventListenerLuaTicksLimit;
    context.save();
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
