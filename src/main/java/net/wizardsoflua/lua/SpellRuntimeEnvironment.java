package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;

import net.sandius.rembulan.env.RuntimeEnvironment;
import net.sandius.rembulan.env.RuntimeEnvironments;

public class SpellRuntimeEnvironment implements RuntimeEnvironment {

  public interface Context {
    String getLuaPath();
  }

  private final Context context;
  private final RuntimeEnvironment delegate = RuntimeEnvironments.system();

  public SpellRuntimeEnvironment(Context context) {
    this.context = checkNotNull(context, "context==null!");
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
    if ("LUA_PATH".equals(name)) {
      String result = context.getLuaPath();
      return result;
    }
    // TODO ensure that the "package.path" variable can not be changed by users during runtime
    return delegate.getEnv(name);
  }

  @Override
  public double getCpuTime() {
    return delegate.getCpuTime();
  }

}
