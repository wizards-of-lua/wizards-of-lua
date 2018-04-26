package net.wizardsoflua.extension.spell.api.resource;

import java.nio.file.Path;

public interface ScriptGatewayConfig {
  boolean isEnabled();

  Path getScriptDir();

  long getScriptTimeoutMillis();
}
