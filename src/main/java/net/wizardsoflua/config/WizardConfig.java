package net.wizardsoflua.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static net.wizardsoflua.lua.table.TableUtils.getAs;
import static net.wizardsoflua.lua.table.TableUtils.getAsOptional;

import java.io.File;
import java.util.UUID;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.lua.module.luapath.AddPathFunction;

public class WizardConfig {

  public interface Context {
    File getLuaLibDirHome();

    void save();
  }

  private UUID id;
  private String libDir;
  private String autoRequire = "";
  private final Context context;

  public WizardConfig(Table table, Context context) {
    this.id = UUID.fromString(getAs(String.class, table, "id"));
    this.libDir = getAsOptional(String.class, table, "libDir").orElse(id.toString());
    this.autoRequire = getAsOptional(String.class, table, "autoRequire").orElse(autoRequire);
    this.context = checkNotNull(context, "context==null!");
  }

  public WizardConfig(UUID id, Context context) {
    this.id = id;
    this.context = checkNotNull(context, "context==null!");
    this.libDir = id.toString();

    File dir = getLibDir();
    if (dir.exists() && !dir.isDirectory()) {
      throw new IllegalStateException(
          format("Illegal libDir. %s is not a directory!", dir.getAbsolutePath()));
    }
    if (!dir.exists()) {
      if (!dir.mkdirs()) {
        WizardsOfLua.instance.logger.warn(format(
            "Couldn't create libDir at %s because of an unknown reason!", dir.getAbsolutePath()));
      }
    }
  }

  public UUID getId() {
    return id;
  }

  public File getLibDir() {
    return new File(context.getLuaLibDirHome(), libDir);
  }

  public @Nullable String getAutoRequire() {
    if ("".equals(autoRequire)) {
      return null;
    }
    return autoRequire;
  }

  public void setAutoRequire(@Nullable String value) {
    if (value == null) {
      value = "";
    }
    this.autoRequire = value;
    context.save();
  }

  public Table writeTo(Table table) {
    table.rawset("id", id.toString());
    table.rawset("libDir", libDir);
    table.rawset("autoRequire", autoRequire);
    return table;
  }

  public String getLibDirPathElement() {
    return getLibDir().getAbsolutePath() + File.separator + AddPathFunction.LUA_EXTENSION_WILDCARD;
  }

}
