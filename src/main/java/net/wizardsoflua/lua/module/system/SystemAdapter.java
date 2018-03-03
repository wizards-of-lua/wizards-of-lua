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
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;

public class SystemAdapter {

  private final boolean enabled;
  private final long scriptTimeoutMillis;
  private final Path scriptDir;

  public SystemAdapter(boolean enabled, long scriptTimeoutMillis, File scriptDir) {
    this.enabled = enabled;
    this.scriptTimeoutMillis = scriptTimeoutMillis;
    this.scriptDir = scriptDir.toPath().normalize();
  }

  public ExecutionResult execute(Collection<String> command) {
    if (!enabled) {
      throw new IllegalStateException(
          "Can't execute command! ScriptGateway is disabled in config.");
    }
    if (command.isEmpty()) {
      throw new IllegalArgumentException("Can't execute empty command!");
    }
    try {
      List<String> commandList = toCommandList(command);

      Process p = new ProcessBuilder(commandList).directory(scriptDir.toFile()).start();
      boolean terminated = p.waitFor(scriptTimeoutMillis, TimeUnit.MILLISECONDS);
      if (!terminated) {
        p.destroy();
        throw new RuntimeException(
            String.format("Command '%s' did not terminate before timeout.", command));
      } else {
        InputStream in = p.getInputStream();
        int exitValue = p.exitValue();
        String response = IOUtils.toString(in, StandardCharsets.UTF_8.name());
        return new ExecutionResult(exitValue, response);
      }
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private ArrayList<String> toCommandList(Collection<String> command) {
    ArrayList<String> result = new ArrayList<>(command);
    String scriptName = toFilename(result.get(0));
    result.set(0, scriptName);
    return result;
  }

  private String toFilename(String name) {
    Path commandFile = scriptDir.resolve(name);
    if (!Files.exists(commandFile)) {
      throw new IllegalArgumentException(
          String.format("Invalid command! File '%s' found!", commandFile));
    }
    return commandFile.toString();
  }

}
