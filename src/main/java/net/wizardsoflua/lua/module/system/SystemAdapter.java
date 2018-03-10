package net.wizardsoflua.lua.module.system;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class SystemAdapter {

  private final boolean enabled;
  private final long scriptTimeoutMillis;
  private final Path scriptDir;

  private Process currentProcess;
  private String currentCommand;
  private long started = Long.MAX_VALUE;

  public SystemAdapter(boolean enabled, long scriptTimeoutMillis, File scriptDir) {
    this.enabled = enabled;
    this.scriptTimeoutMillis = scriptTimeoutMillis;
    this.scriptDir = scriptDir.toPath().normalize();

  }

  public boolean shouldPause() {
    return enabled && currentProcess != null && currentProcess.isAlive() && !isTimeoutReached();
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
    return (started + scriptTimeoutMillis < System.currentTimeMillis());
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
