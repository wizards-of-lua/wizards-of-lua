package net.wizardsoflua.lua;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;

import net.sandius.rembulan.env.RuntimeEnvironment;
import net.sandius.rembulan.env.RuntimeEnvironments;

public class SpellRuntimeEnvironment implements RuntimeEnvironment {

  private RuntimeEnvironment delegate = RuntimeEnvironments.system();

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
    // Make sure that lua modules are loaded from this mod's config dir only
    // TODO ensure that the "package.path" variable can not be changed by users during runtime
    if ("LUA_PATH".equals(name)) {
      // FIXME return actual path
      // return LuaMod.instance.getLuaDir().getAbsolutePath() + "/?.lua";
      return "";
    }
    return delegate.getEnv(name);
  }

  @Override
  public double getCpuTime() {
    return delegate.getCpuTime();
  }

}
