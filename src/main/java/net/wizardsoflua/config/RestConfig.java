package net.wizardsoflua.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static net.wizardsoflua.lua.table.TableUtils.getAsOptional;

import java.io.File;

import net.sandius.rembulan.Table;
import net.wizardsoflua.WizardsOfLua;

public class RestConfig {

  public interface Context {
    File getWolConfigDir();
  }

  private String hostname = "127.0.0.1";
  private int port = 60000;
  private String webDir = "www";
  private final File webDirFile;

  private final Context context;

  public RestConfig(Context context) {
    this.context = checkNotNull(context, "context==null!");
    webDirFile = tryToCreateDir(new File(this.context.getWolConfigDir(), webDir));
  }

  public RestConfig(Table table, Context context) {
    this.context = checkNotNull(context, "context==null!");
    hostname = getAsOptional(String.class, table, "hostname").orElse(hostname);
    port = getAsOptional(Integer.class, table, "port").orElse(port);
    webDir = getAsOptional(String.class, table, "webDir").orElse(webDir);
    webDirFile = tryToCreateDir(new File(context.getWolConfigDir(), webDir));
  }

  public Table writeTo(Table table) {
    table.rawset("hostname", hostname);
    table.rawset("port", port);
    table.rawset("webDir", webDir);
    return table;
  }

  public String getHostname() {
    return hostname;
  }

  public int getPort() {
    return port;
  }

  public File getWebDir() {
    return webDirFile;
  }

  private File tryToCreateDir(File dir) {
    if (!dir.exists()) {
      if (!dir.mkdirs()) {
        WizardsOfLua.instance.logger
            .warn(format("Couldn't create directory at %s because of an unknown reason!",
                dir.getAbsolutePath()));
      }
    }
    return dir;
  }

}
