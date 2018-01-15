package net.wizardsoflua.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static net.wizardsoflua.lua.table.TableUtils.getAsOptional;

import java.io.File;
import java.util.UUID;

import net.sandius.rembulan.Table;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.file.Crypto;

public class RestApiConfig {

  public interface Context {
    File getWolConfigDir();
  }

  private String hostname = "127.0.0.1";
  private int port = 60000;
  private boolean secure = false;
  private String keystore = "";
  private String keystorePassword = "";
  private String keyPassword = "";
  private UUID uuid = UUID.randomUUID();
  private String apiKey = new Crypto().createRandomPassword();
  private String webDir = "www";
  private final File webDirFile;

  private final Context context;

  public RestApiConfig(Context context) {
    this.context = checkNotNull(context, "context==null!");
    webDirFile = tryToCreateDir(new File(this.context.getWolConfigDir(), webDir));
  }

  public RestApiConfig(Table table, Context context) {
    this.context = checkNotNull(context, "context==null!");
    hostname = getAsOptional(String.class, table, "hostname").orElse(hostname);
    port = getAsOptional(Integer.class, table, "port").orElse(port);
    secure = getAsOptional(Boolean.class, table, "secure").orElse(secure);
    keystore = getAsOptional(String.class, table, "keystore").orElse(keystore);
    keystorePassword =
        getAsOptional(String.class, table, "keystorePassword").orElse(keystorePassword);
    keyPassword = getAsOptional(String.class, table, "keyPassword").orElse(keyPassword);
    webDir = getAsOptional(String.class, table, "webDir").orElse(webDir);
    webDirFile = tryToCreateDir(new File(context.getWolConfigDir(), webDir));
    uuid = UUID
        .fromString(getAsOptional(String.class, table, "uuid").orElse(uuid.toString()));
    apiKey = getAsOptional(String.class, table, "apiKey").orElse(apiKey);
  }

  public Table writeTo(Table table) {
    table.rawset("hostname", hostname);
    table.rawset("port", port);
    table.rawset("secure", secure);
    table.rawset("keystore", keystore);
    table.rawset("keystorePassword", keystorePassword);
    table.rawset("keyPassword", keyPassword);
    table.rawset("webDir", webDir);
    table.rawset("uuid", uuid.toString());
    table.rawset("apiKey", apiKey);
    return table;
  }

  public String getHostname() {
    return hostname;
  }

  public int getPort() {
    return port;
  }

  public boolean isSecure() {
    return secure;
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

  public String getProtocol() {
    if (secure) {
      return "https";
    } else {
      return "http";
    }
  }

  public String getKeyStore() {
    return keystore;
  }

  public char[] getKeyStorePassword() {
    return keystorePassword.toCharArray();
  }

  public char[] getKeyPassword() {
    return keyPassword.toCharArray();
  }

  public UUID getUuid() {
    return uuid;
  }

  public String getApiKey() {
    return apiKey;
  }

}
