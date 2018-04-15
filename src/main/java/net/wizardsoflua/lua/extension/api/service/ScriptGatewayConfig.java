package net.wizardsoflua.lua.extension.api.service;

import java.nio.file.Path;

public interface ScriptGatewayConfig {
  boolean isEnabled();

  Path getScriptDir();

  long getScriptTimeoutMillis();
}
