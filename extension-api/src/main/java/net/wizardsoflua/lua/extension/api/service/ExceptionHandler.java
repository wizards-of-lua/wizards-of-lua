package net.wizardsoflua.lua.extension.api.service;

public interface ExceptionHandler {
  default void handle(Throwable t) {
    handle("Error in module", t);
  }

  void handle(String contextMessage, Throwable t);
}
