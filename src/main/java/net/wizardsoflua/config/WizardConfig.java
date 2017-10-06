package net.wizardsoflua.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.UUID;

import javax.annotation.Nullable;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.module.luapath.AddPathFunction;
import net.wizardsoflua.lua.table.TableUtils;

public class WizardConfig {

  public interface Context {
    File getLuaLibDirHome();

    void save();
  }

  private UUID id;
  private String libDir;
  private @Nullable String autoRequire;
  private final Context context;

  public WizardConfig(Table table, Context context) {
    this.id = UUID.fromString(TableUtils.getAs(String.class, table, "id"));
    this.libDir = TableUtils.getAs(String.class, table, "libDir");
    this.autoRequire = TableUtils.getAs(String.class, table, "autoRequire");
    this.context = checkNotNull(context, "context==null!");
  }

  public WizardConfig(UUID id, Context context) {
    this.id = id;
    this.libDir = id.toString();
    this.autoRequire = "";
    this.context = checkNotNull(context, "context==null!");
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
    return getLibDir().getAbsolutePath() + File.separator
        + AddPathFunction.LUA_EXTENSION_WILDCARD;
  }

}
