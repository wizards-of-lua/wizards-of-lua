package net.wizardsoflua.lua.module.system;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;

import com.google.auto.service.AutoService;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaFunctionDoc;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Config;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.api.resource.LuaScheduler;
import net.wizardsoflua.extension.spell.api.resource.LuaTypes;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.extension.util.LuaTableExtension;
import net.wizardsoflua.lua.function.NamedFunctionAnyArg;

@GenerateLuaModuleTable
@GenerateLuaDoc(name = SystemModule.NAME, subtitle = "Interacting with the Server's OS")
@AutoService(SpellExtension.class)
public class SystemModule extends LuaTableExtension {
  public static final String NAME = "System";
  @Resource
  private LuaConverters converters;
  @Resource
  private LuaScheduler scheduler;

  private boolean enabled;
  private Path scriptDir;
  private long scriptTimeoutMillis;

  private @Nullable Process currentProcess;
  private @Nullable String currentCommand;
  private long started = Long.MAX_VALUE;

  public void init(@Resource Config config) {
    enabled = config.getScriptGatewayConfig().isEnabled();
    scriptDir = config.getScriptGatewayConfig().getScriptDir();
    scriptTimeoutMillis = config.getScriptGatewayConfig().getScriptTimeoutMillis();
    scheduler.addPauseContext(this::shouldPause);
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table getTable() {
    return new SystemModuleTable<>(this, converters);
  }

  public boolean shouldPause() {
    return enabled && currentProcess != null && currentProcess.isAlive() && !isTimeoutReached();
  }

  @net.wizardsoflua.annotation.LuaFunction(name = ExecuteFunction.NAME)
  @LuaFunctionDoc(returnType = LuaTypes.NIL, args = {"name", "arg..."})
  class ExecuteFunction extends NamedFunctionAnyArg {
    public static final String NAME = "execute";

    @Override
    public String getName() {
      return NAME;
    }

    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      List<String> command = converters.toJavaList(String.class, args, getName());
      execute(command);
      executeFunction(context);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      executeFunction(context);
    }

    private void executeFunction(ExecutionContext context) throws ResolvedControlThrowable {
      try {
        scheduler.pauseIfRequested(context);
      } catch (UnresolvedControlThrowable e) {
        throw e.resolve(ExecuteFunction.this, null);
      }

      ExecutionResult result = getExecutionResult();
      context.getReturnBuffer().setTo(result.exitValue, result.response);
    }
  }

  public void execute(Collection<String> command) {
    if (!enabled) {
      throw new IllegalStateException(
          "Can't execute command! ScriptGateway is disabled in config.");
    }
    if (currentProcess != null) {
      throw new IllegalStateException(
          "Can't execute command! Already waiting for a command to terminate.");
    }
    if (command.isEmpty()) {
      throw new IllegalArgumentException("Can't execute empty command!");
    }
    try {
      List<String> commandList = toCommandList(command);
      currentCommand = String.join(" ", command);
      currentProcess = new ProcessBuilder(commandList).directory(scriptDir.toFile()).start();
      started = System.currentTimeMillis();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public ExecutionResult getExecutionResult() {
    boolean alive = currentProcess.isAlive();
    try {
      if (alive) {
        if (!isTimeoutReached()) {
          throw new IllegalStateException("Can't get execution result! Command is still running!");
        }
        currentProcess.destroy();
        throw new RuntimeException(
            String.format("Command '%s' did not terminate before timeout.", currentCommand));
      } else {
        try {
          InputStream in = currentProcess.getInputStream();
          int exitValue = currentProcess.exitValue();
          String response = IOUtils.toString(in, StandardCharsets.UTF_8.name());
          return new ExecutionResult(exitValue, response);
        } catch (IOException e) {
          throw new RuntimeException("Error while accessing execution result!", e);
        }
      }
    } finally {
      currentProcess = null;
      currentCommand = null;
      started = Long.MAX_VALUE;
    }
  }

  private boolean isTimeoutReached() {
    return started + scriptTimeoutMillis < System.currentTimeMillis();
  }

  private ArrayList<String> toCommandList(Collection<String> command) {
    ArrayList<String> result = new ArrayList<>(command);
    String scriptName = toFilename(result.get(0));
    result.set(0, scriptName);
    return result;
  }

  private String toFilename(String name) {
    Path commandFile = scriptDir.resolve(name).normalize();
    if (!commandFile.startsWith(scriptDir)) {
      throw new IllegalArgumentException(
          String.format("Illegal command! Execution of file '%s' not allowed!", commandFile));
    }
    if (!Files.exists(commandFile)) {
      throw new IllegalArgumentException(
          String.format("Invalid command! File '%s' found!", commandFile));
    }
    return commandFile.toString();
  }
}
