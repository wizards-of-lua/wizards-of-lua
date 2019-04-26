package net.wizardsoflua.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static net.wizardsoflua.WizardsOfLua.LOGGER;
import static net.wizardsoflua.lua.table.TableUtils.getAsOptional;
import java.io.File;
import java.nio.file.Path;
import net.sandius.rembulan.Table;

public class ScriptGatewayConfig {

  public interface Context {
    File getWolConfigDir();

    void save();
  }

  private boolean enabled = false;
  private int timeoutMillis = 2000;
  private String dir = "scripts";
  private final File dirFile;

  private final Context context;

  public ScriptGatewayConfig(Context context) {
    this.context = checkNotNull(context, "context==null!");
    dirFile = tryToCreateDir(new File(this.context.getWolConfigDir(), dir));
  }

  public ScriptGatewayConfig(Table table, Context context) {
    this.context = checkNotNull(context, "context==null!");
    enabled = getAsOptional(Boolean.class, table, "enabled").orElse(enabled);
    timeoutMillis = getAsOptional(Integer.class, table, "timeoutMillis").orElse(timeoutMillis);
    dir = getAsOptional(String.class, table, "dir").orElse(dir);
    dirFile = tryToCreateDir(new File(context.getWolConfigDir(), dir));
  }

  public Table writeTo(Table table) {
    table.rawset("enabled", enabled);
    table.rawset("timeoutMillis", timeoutMillis);
    table.rawset("dir", dir);
    return table;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    context.save();
  }

  public int getTimeoutMillis() {
    return timeoutMillis;
  }

  public Path getDir() {
    return dirFile.toPath().normalize();
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

}
