package net.wizardsoflua.lua.module.system;

public class ExecutionResult {
  public final int exitValue;
  public final String response;

  public ExecutionResult(int exitValue, String response) {
    this.exitValue = exitValue;
    this.response = response;
  }
  
}
