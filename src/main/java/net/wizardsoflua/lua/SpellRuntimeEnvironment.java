package net.wizardsoflua.lua;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;

import javax.annotation.Nullable;

import net.sandius.rembulan.env.RuntimeEnvironment;
import net.sandius.rembulan.env.RuntimeEnvironments;

public class SpellRuntimeEnvironment implements RuntimeEnvironment {

  private RuntimeEnvironment delegate = RuntimeEnvironments.system();
  private final @Nullable File luaDir;
  private final String luaPath;

  public SpellRuntimeEnvironment(@Nullable File luaDir) {
    this.luaDir = luaDir;
    if (luaDir != null) {
      if (!luaDir.exists()) {
        if (!luaDir.mkdirs()) {
          throw new IllegalArgumentException(
              String.format("Can't create directory %s", luaDir.getAbsolutePath()));
        }
      }
    }
    if (luaDir.exists()) {
      luaPath = luaDir.getAbsolutePath() + "/?.lua";
    } else {
      luaPath = "";
    }
  }

  @Override
  public InputStream standardInput() {
    return delegate.standardInput();
  }

  @Override
  public OutputStream standardOutput() {
    return delegate.standardOutput();
  }

  @Override
  public OutputStream standardError() {
    return delegate.standardError();
  }

  @Override
  public FileSystem fileSystem() {
    return delegate.fileSystem();
  }

  @Override
  public String getEnv(String name) {
    // We make sure that lua modules are loaded from this mod's config dir only
    // TODO ensure that the "package.path" variable can not be changed by users during runtime
    if ("LUA_PATH".equals(name)) {
      return luaPath;
    }
    return delegate.getEnv(name);
  }

  @Override
  public double getCpuTime() {
    return delegate.getCpuTime();
  }

}
