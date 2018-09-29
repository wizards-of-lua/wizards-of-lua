package net.wizardsoflua.lua.module.system;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;

import com.google.auto.service.AutoService;

import net.sandius.rembulan.LuaRuntimeException;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaFunctionDoc;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Config;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.api.resource.LuaScheduler;
import net.wizardsoflua.extension.spell.api.resource.LuaTypes;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.filesystem.PathUtil;
import net.wizardsoflua.lua.extension.LuaTableExtension;
import net.wizardsoflua.lua.function.NamedFunctionAnyArg;

@AutoService(SpellExtension.class)
@GenerateLuaModuleTable
@GenerateLuaDoc(name = SystemModule.NAME, subtitle = "Interacting with the Server's OS")
public class SystemModule extends LuaTableExtension {
  public static final String NAME = "System";
  @Resource
  private LuaConverters converters;
  @Resource
  private LuaScheduler scheduler;
  @Resource
  private WizardsOfLua wizardsOfLua;

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

  @LuaFunction
  public Collection<String> listFiles(String path) {
    try {
      FileSystem fileSystem = wizardsOfLua.getWorldFileSystem();
      Path pathObj = fileSystem.getPath(path);
      if (!Files.exists(pathObj)) {
        throw new LuaRuntimeException(String.format("%s does not exist!", path));
      }
      if (!Files.isDirectory(pathObj)) {
        throw new LuaRuntimeException(String.format("%s is not a directory!", path));
      } else {
        List<String> list =
            Files.list(pathObj).map(p -> p.getFileName().toString()).collect(Collectors.toList());
        return list;
      }
    } catch (IOException e) {
      throw new LuaRuntimeException(e);
    }
  }

  @LuaFunction
  public boolean isDir(String path) {
    FileSystem fileSystem = wizardsOfLua.getWorldFileSystem();
    Path pathObj = fileSystem.getPath(path);
    return Files.exists(pathObj) && Files.isDirectory(pathObj);
  }

  @LuaFunction
  public boolean isFile(String path) {
    FileSystem fileSystem = wizardsOfLua.getWorldFileSystem();
    Path pathObj = fileSystem.getPath(path);
    return Files.exists(pathObj) && Files.isRegularFile(pathObj);
  }

  @LuaFunction
  public boolean makeDir(String path) {
    FileSystem fileSystem = wizardsOfLua.getWorldFileSystem();
    Path pathObj = fileSystem.getPath(path);
    if (Files.exists(pathObj) && Files.isRegularFile(pathObj)) {
      return false;
    }
    try {
      Files.createDirectories(pathObj);
      return Files.exists(pathObj) && Files.isDirectory(pathObj);
    } catch (IOException ex) {
      throw new LuaRuntimeException(ex);
    }
  }

  @LuaFunction
  public boolean delete(String path) {
    FileSystem fileSystem = wizardsOfLua.getWorldFileSystem();
    Path pathObj = fileSystem.getPath(path);
    try {
      return Files.deleteIfExists(pathObj) && !Files.exists(pathObj);
    } catch (IOException ex) {
      throw new LuaRuntimeException(ex);
    }
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
    Path commandFile = PathUtil.toPath(scriptDir, name);
    if (!Files.exists(commandFile)) {
      throw new IllegalArgumentException(
          String.format("Invalid command! File '%s' found!", commandFile));
    }
    return commandFile.toString();
  }
}
