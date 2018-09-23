package net.wizardsoflua.lua;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;

import net.sandius.rembulan.env.RuntimeEnvironment;

public class WolRuntimeEnvironment implements RuntimeEnvironment {

  private RuntimeEnvironment delegate;
  private FileSystem fileSystem;

  public WolRuntimeEnvironment(RuntimeEnvironment delegate, FileSystem fileSystem) {
    this.delegate = delegate;
    this.fileSystem = fileSystem;
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
    return fileSystem;
  }

  @Override
  public String getEnv(String name) {
    return delegate.getEnv(name);
  }

  @Override
  public double getCpuTime() {
    return delegate.getCpuTime();
  }

}
