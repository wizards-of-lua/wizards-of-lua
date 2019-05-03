package net.wizardsoflua.lua.classes.logger;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;

import org.apache.logging.log4j.Level;

import com.google.auto.service.AutoService;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.lib.StringLib;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaFunctionDoc;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.api.resource.LuaTypes;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.LuaInstance;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.function.NamedFunctionAnyArg;

/**
 * The <span class="notranslate">Logger</span> class supports writing log messages into the server's
 * log file.
 *
 * The log messages can be of the following severity: error, warn, info, debug, and trace.
 *
 * The server's log files are found inside the server's ```logs``` folder. By default "error",
 * "warn", and "info" messages go into the files "latest.log" and "debug.log", while "debug" and
 * "trace" messages only go into "debug.log".
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = LoggerClass.NAME)
@GenerateLuaClassTable(instance = LoggerClass.Instance.class)
@GenerateLuaDoc(subtitle = "Logging Messages")
public final class LoggerClass extends BasicLuaClass<WolLogger, LoggerClass.Instance<WolLogger>> {
  public static final String NAME = "Logger";
  @Resource
  private LuaConverters converters;

  @Override
  protected Table createRawTable() {
    return new LoggerClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<WolLogger>> toLuaInstance(WolLogger javaInstance) {
    return new LoggerClassInstanceTable<>(new Instance<>(javaInstance), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends WolLogger> extends LuaInstance<D> {
    public Instance(D delegate) {
      super(delegate);
    }

    /**
     * The <span class="notranslate">'error'</span> function writes the given error message into the
     * server's log file if the log level is at least 'error', prefixed with this logger's name.
     *
     * Optionally you can provide some message arguments that will be formatted into the final
     * message ([see
     * string.format()](https://www.lua.org/manual/5.3/manual.html#pdf-string.format)).
     *
     * #### Example
     *
     * Printing an error message into the server's log file, prefixed with the category label
     * "my-logger".
     *
     * <code>
     * local logger = Loggers.get("my-logger")
     * logger:error("Some error message")
     * </code>
     *
     * #### Example
     *
     * Printing a formatted error message into the server's log file, prefixed with the category
     * label "my-logger.
     *
     * <code>
     * local logger = Loggers.get("my-logger")
     * logger:error("Some error message with some value %s", value)
     * </code>
     */
    @LuaFunction(name = ErrorFunction.NAME)
    @LuaFunctionDoc(returnType = LuaTypes.NIL, args = {"message", "arg..."})
    static class ErrorFunction extends AbstractLogFunction {
      public static final String NAME = "error";

      public ErrorFunction(LoggerClass luaClass) {
        super(luaClass, NAME, Level.ERROR);
      }
    }

    /**
     * The <span class="notranslate">'warn'</span> function writes the given warning message into
     * the server's log file if the log level is at least 'warn', prefixed with this logger's name.
     *
     * Optionally you can provide some message arguments that will be formatted into the final
     * message ([see
     * string.format()](https://www.lua.org/manual/5.3/manual.html#pdf-string.format)).
     *
     * #### Example
     *
     * Printing an error message into the server's log file, prefixed with the category label
     * "my-logger".
     *
     * <code>
     * local logger = Loggers.get("my-logger")
     * logger:warn("Some warning message")
     * </code>
     *
     * #### Example
     *
     * Printing a formatted warning message into the server's log file, prefixed with the category
     * label "my-logger.
     *
     * <code>
     * local logger = Loggers.get("my-logger")
     * logger:warn("Some warning message with some value %s", value)
     * </code>
     */
    @LuaFunction(name = WarnFunction.NAME)
    @LuaFunctionDoc(returnType = LuaTypes.NIL, args = {"message", "arg..."})
    static class WarnFunction extends AbstractLogFunction {
      public static final String NAME = "warn";

      public WarnFunction(LoggerClass luaClass) {
        super(luaClass, NAME, Level.WARN);
      }
    }

    /**
     * The <span class="notranslate">'info'</span> function writes the given information message
     * into the server's log file if the log level is at least 'info', prefixed with this logger's
     * name.
     *
     * Optionally you can provide some message arguments that will be formatted into the final
     * message ([see
     * string.format()](https://www.lua.org/manual/5.3/manual.html#pdf-string.format)).
     *
     * #### Example
     *
     * Printing an info message into the server's log file, prefixed with the category label
     * "my-logger".
     *
     * <code>
     * local logger = Loggers.get("my-logger")
     * logger:info("Some info message")
     * </code>
     *
     * #### Example
     *
     * Printing a formatted info message into the server's log file, prefixed with the category
     * label "my-logger.
     *
     * <code>
     * local logger = Loggers.get("my-logger")
     * logger:info("Some info message with some value %s", value)
     * </code>
     */
    @LuaFunction(name = InfoFunction.NAME)
    @LuaFunctionDoc(returnType = LuaTypes.NIL, args = {"message", "arg..."})
    static class InfoFunction extends AbstractLogFunction {
      public static final String NAME = "info";

      public InfoFunction(LoggerClass luaClass) {
        super(luaClass, NAME, Level.INFO);
      }
    }

    /**
     * The <span class="notranslate">'debug'</span> function writes the given debug message into the
     * server's log file if the log level is at least 'debug', prefixed with this logger's name.
     *
     * Optionally you can provide some message arguments that will be formatted into the final
     * message ([see
     * string.format()](https://www.lua.org/manual/5.3/manual.html#pdf-string.format)).
     *
     * #### Example
     *
     * Printing an debug message into the server's log file, prefixed with the category label
     * "my-logger".
     *
     * <code>
     * local logger = Loggers.get("my-logger")
     * logger:debug("Some debug message")
     * </code>
     *
     * #### Example
     *
     * Printing a formatted debug message into the server's log file, prefixed with the category
     * label "my-logger.
     *
     * <code>
     * local logger = Loggers.get("my-logger")
     * logger:debug("Some debug message with some value %s", value)
     * </code>
     */
    @LuaFunction(name = DebugFunction.NAME)
    @LuaFunctionDoc(returnType = LuaTypes.NIL, args = {"message", "arg..."})
    static class DebugFunction extends AbstractLogFunction {
      public static final String NAME = "debug";

      public DebugFunction(LoggerClass luaClass) {
        super(luaClass, NAME, Level.DEBUG);
      }
    }

    /**
     * The <span class="notranslate">'trace'</span> function writes the given tracing message into
     * the server's log file if the log level is at least 'trace', prefixed with this logger's name.
     *
     * Optionally you can provide some message arguments that will be formatted into the final
     * message ([see
     * string.format()](https://www.lua.org/manual/5.3/manual.html#pdf-string.format)).
     *
     * #### Example
     *
     * Printing an tracing message into the server's log file, prefixed with the category label
     * "my-logger".
     *
     * <code>
     * local logger = Loggers.get("my-logger")
     * logger:trace("Some tracing message")
     * </code>
     *
     * #### Example
     *
     * Printing a formatted traceing message into the server's log file, prefixed with the category
     * label "my-logger.
     *
     * <code>
     * local logger = Loggers.get("my-logger")
     * logger:trace("Some traceing message with some value %s", value)
     * </code>
     */
    @LuaFunction(name = TraceFunction.NAME)
    @LuaFunctionDoc(returnType = LuaTypes.NIL, args = {"message", "arg..."})
    static class TraceFunction extends AbstractLogFunction {
      public static final String NAME = "trace";

      public TraceFunction(LoggerClass luaClass) {
        super(luaClass, NAME, Level.TRACE);
      }
    }

    abstract static class AbstractLogFunction extends NamedFunctionAnyArg {
      private final LoggerClass luaClass;
      private final String name;
      private final Level level;

      public AbstractLogFunction(LoggerClass luaClass, String name, Level level) {
        this.luaClass = requireNonNull(luaClass, "luaClass == null!");
        this.name = name;
        this.level = level;
      }

      @Override
      public String getName() {
        return name;
      }

      public Level getLevel() {
        return level;
      }

      @Override
      public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
        Object arg1 = getArg(1, args);
        Instance<?> self = luaClass.converters.toJava(Instance.class, arg1, 1, "self", NAME);
        Object arg2 = getArg(2, args);
        String message = luaClass.converters.toJava(String.class, arg2, 2, "message", getName());

        if (self.delegate.matchesLevel(level)) {
          if (args.length > 2) {
            // format the message
            Object[] format = Arrays.copyOfRange(args, 1, args.length);
            StringLib.format().invoke(context, format);
            message = String.valueOf(context.getReturnBuffer().get0());
          }
          self.delegate.log(level, message);
        }
        context.getReturnBuffer().setTo();
      }
    }

    private static Object getArg(int i, Object[] args) {
      return args.length < i ? null : args[i - 1];
    }
  }

}
