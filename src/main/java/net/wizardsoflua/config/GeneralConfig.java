package net.wizardsoflua.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.wizardsoflua.lua.table.TableUtils.getAsOptional;

import java.io.File;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.table.TableUtils;

public class GeneralConfig {

  public interface Context {
    void save();

    File getWolConfigDir();
  }

  /**
   * Max. number of Lua ticks a spell can run per game tick [range: 1000 ~ 10000000, default: 10000]
   */
  private int luaTicksLimit = 10000;
  /**
   * Shows the about message to the player at first login [default: true]
   */
  private boolean showAboutMessage = true;

  private String luaLibDirHome = "libs";
  private final File luaLibDirHomeFile;

  private final Context context;

  public GeneralConfig(Context context) {
    luaLibDirHomeFile = new File(context.getWolConfigDir(), luaLibDirHome);
    this.context = checkNotNull(context, "context==null!");
  }

  public GeneralConfig(Table table, Context context) {
    setLuaTicksLimit(
        getAsOptional(Integer.class, table, "luaTicksLimit").orElse(luaTicksLimit).intValue(),
        false);
    showAboutMessage = getAsOptional(Boolean.class, table, "showAboutMessage")
        .orElse(showAboutMessage).booleanValue();
    luaLibDirHome = TableUtils.getAsOptional(String.class, table, "luaLibDirHome").orElse(luaLibDirHome);
    this.context = checkNotNull(context, "context==null!");

    luaLibDirHomeFile = new File(context.getWolConfigDir(), luaLibDirHome);
  }

  public int getLuaTicksLimit() {
    return luaTicksLimit;
  }

  public int setLuaTicksLimit(int luaTicksLimit) {
    return setLuaTicksLimit(luaTicksLimit, true);
  }

  private int setLuaTicksLimit(int luaTicksLimit, boolean save) {
    this.luaTicksLimit = clamp(luaTicksLimit, 1000, 10000000);
    if (save) {
      context.save();
    }
    return this.luaTicksLimit;
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

  public Table writeTo(Table table) {
    table.rawset("luaTicksLimit", luaTicksLimit);
    table.rawset("showAboutMessage", showAboutMessage);
    table.rawset("luaLibDirHome", luaLibDirHome);
    return table;
  }

}
