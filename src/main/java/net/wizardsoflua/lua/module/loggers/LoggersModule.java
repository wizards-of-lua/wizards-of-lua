package net.wizardsoflua.lua.module.loggers;

import org.apache.logging.log4j.LogManager;
import com.google.auto.service.AutoService;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.classes.logger.WolLogger;
import net.wizardsoflua.lua.extension.LuaTableExtension;

/**
 * The <span class="notranslate">Loggers</span> module provides you access to the server's
 * [Logger](../Logger) instances.
 */
@AutoService(SpellExtension.class)
@GenerateLuaModuleTable
@GenerateLuaDoc(name = LoggersModule.NAME, subtitle = "Accessing Loggers")
public class LoggersModule extends LuaTableExtension {
  public static final String NAME = "Loggers";
  @Resource
  private LuaConverters converters;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table getTable() {
    return new LoggersModuleTable<>(this, converters);
  }

  /**
   * The <span class="notranslate">'get'</span> function returns the [Logger](../Logger) with the
   * given name.
   *
   * #### Example
   *
   * Accessing the [Logger](../Logger) with the name "my-logger".
   *
   * <code>
   *  local logger = Loggers.get("my-logger")
   *  </code>
   *
   */
  @LuaFunction
  public WolLogger get(String loggerName) {
    return new WolLogger(LogManager.getLogger(loggerName));
  }

}
