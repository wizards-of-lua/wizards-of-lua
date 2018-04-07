package net.wizardsoflua.lua.extension.api;

public interface ExceptionHandler {
  default void handle(Throwable t) {
    handle("Error in module", t);
  }

  void handle(String contextMessage, Throwable t);
}
