package net.wizardsoflua.lua.module.system;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import com.google.auto.service.AutoService;
import net.sandius.rembulan.LuaRuntimeException;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaFunctionDoc;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Config;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.api.resource.LuaScheduler;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.filesystem.PathUtil;
import net.wizardsoflua.filesystem.WolServerFileSystem;
import net.wizardsoflua.lua.extension.LuaTableExtension;
import net.wizardsoflua.lua.function.NamedFunctionAnyArg;

/**
 * The <span class="notranslate">System</span> module provides functions for interacting with the
 * server's operating system.
 */
@AutoService(SpellExtension.class)
@GenerateLuaModuleTable
@GenerateLuaDoc(name = SystemModule.NAME, subtitle = "Interacting with the Server's OS")
public class SystemModule extends LuaTableExtension {
  public static final String NAME = "System";
  @Resource
  private LuaConverters converters;
  @Resource
  private LuaScheduler scheduler;
  @Inject
  private WolServerFileSystem fileSystem;

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

  /**
   * The <span class="notranslate">'listFiles'</span> function returns a table with the names of all
   * files that exist inside the directory at the given path. The path is interpreted relative to
   * the server's world folder.
   *
   * #### Example
   *
   * Printing the names of all files inside the "region" folder of the server's world folder.
   *
   * <code>
   * local path = '/region'
   * local names = System.listFiles(path)
   * for _,name in pairs(names) do
   *   print(name)
   * end
   * </code>
   *
   * #### Example
   *
   * Getting the names of all files inside server's world folder.
   *
   * <code>
   * local names = System.listFiles('/')
   * </code>
   */
  @LuaFunction
  public Collection<String> listFiles(String path) {
    try {
      Path pathObj = fileSystem.getPath(path);
      if (!Files.exists(pathObj)) {
        throw new LuaRuntimeException(String.format("%s does not exist!", path));
      }
      if (!Files.isDirectory(pathObj)) {
        throw new LuaRuntimeException(String.format("%s is not a directory!", path));
      } else {
        try (Stream<Path> stream = Files.list(pathObj)) {
          return stream.map(p -> p.getFileName().toString()).sorted().collect(Collectors.toList());
        }
      }
    } catch (IOException e) {
      throw new LuaRuntimeException(e);
    }
  }

  /**
   * The <span class="notranslate">'isDir'</span> function checks whether the given path points to a
   * directory (in contrast to a regular file). The path is interpreted relative to the server's
   * world folder.
   *
   * #### Example
   *
   * Printing the file type of the file "some/file" inside the server's world folder.
   *
   * <code>
   * local path = '/some/file'
   * if System.isFile(path) then
   *   print(string.format('% is a regular file',path))
   * end
   * if System.isDir(path) then
   *   print(string.format('% is a directory',path))
   * end
   * </code>
   */
  @LuaFunction
  public boolean isDir(String path) {
    Path pathObj = fileSystem.getPath(path);
    return Files.exists(pathObj) && Files.isDirectory(pathObj);
  }

  /**
   * The <span class="notranslate">'isFile'</span> function checks whether the given path points to
   * a regular file (in contrast to a directory). The path is interpreted relative to the server's
   * world folder.
   *
   * #### Example
   *
   * Printing the file type of the file "some/file" inside the server's world folder.
   *
   * <code>
   * local path = '/some/file'
   * if System.isFile(path) then
   *   print(string.format('% is a regular file',path))
   * end
   * if System.isDir(path) then
   *   print(string.format('% is a directory',path))
   * end
   * </code>
   */
  @LuaFunction
  public boolean isFile(String path) {
    Path pathObj = fileSystem.getPath(path);
    return Files.exists(pathObj) && Files.isRegularFile(pathObj);
  }

  /**
   * The <span class="notranslate">'makeDir'</span> function creates a new directory with the given
   * path if it did not already exist. The path is interpreted relative to the server's world
   * folder. This function returns true if the directory already existed or if it has been be
   * created.
   *
   * #### Example
   *
   * Creating the directory "some/dir" in the server's world folder.
   *
   * <code>
   * local created = System.makeDir('/some/dir')
   * if not created then
   *   error('Could not create directory')
   * end
   * </code>
   */
  @LuaFunction
  public boolean makeDir(String path) {
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

  /**
   * The <span class="notranslate">'delete'</span> function deletes the file with the given path.
   * The path is interpreted relative to the server's world folder. This function returns true if
   * the file did exist and has been deleted.
   *
   * Please note that deleting a directory is only supported if its empty.
   *
   * #### Example
   *
   * Deleting the file "some-file-to-delete.txt" from the server's world folder.
   *
   * <code>
   * System.delete('/some-file-to-delete.txt')
   * </code>
   *
   */
  @LuaFunction
  public boolean delete(String path) {
    Path pathObj = fileSystem.getPath(path);
    try {
      return Files.deleteIfExists(pathObj) && !Files.exists(pathObj);
    } catch (IOException ex) {
      throw new LuaRuntimeException(ex);
    }
  }

  /**
   * The <span class="notranslate">'move'</span> function moves or renames the file with the given
   * path so that the resulting file is accessible by the given new path. The path is interpreted
   * relative to the server's world folder. This function returns true if the operation was
   * successful.
   *
   * #### Example
   *
   * Renaming the file "aaa.txt" to "bbb.txt"
   *
   * <code>
   * System.move('aaa.txt','bbb.txt')
   * </code>
   *
   */
  @LuaFunction
  public boolean move(String path, String newPath) {
    Path pathObj = fileSystem.getPath(path);
    Path newPathObj = fileSystem.getPath(newPath);
    boolean isFile = Files.isRegularFile(pathObj);
    boolean isDir = Files.isDirectory(pathObj);
    try {
      Path target = Files.move(pathObj, newPathObj, StandardCopyOption.ATOMIC_MOVE,
          StandardCopyOption.REPLACE_EXISTING);
      return Files.exists(target)
          && (isFile == Files.isRegularFile(target) || isDir == Files.isDirectory(target));
    } catch (IOException ex) {
      throw new LuaRuntimeException(ex);
    }
  }

  /**
   * The <span class="notranslate">'execute'</span> function invokes the program with the given name
   * and the given arguments on the server's operating system. Please note that you only can execute
   * programs living inside the server's [script gateway
   * directory](../../configuration-file#scriptgateway). Please note that this is a blocking call.
   * The spell will resume its execution only after the program has terminated.
   *
   * #### Example
   *
   * Calling the "echo.sh" shell script from the server's script gateway directory.
   *
   * <code>
   * exitcode, result = System.execute('echo.sh','some argument')
   * print('exitcode', exitcode)
   * print('result', result)
   * </code>
   */
  @net.wizardsoflua.annotation.LuaFunction(name = ExecuteFunction.NAME)
  @LuaFunctionDoc(returnType = "'number', 'string'", args = {"name", "arg..."})
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
