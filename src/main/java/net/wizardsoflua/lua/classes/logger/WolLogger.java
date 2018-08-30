package net.wizardsoflua.lua.classes.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class WolLogger {
  private final Logger delegate;

  public WolLogger(Logger delegate) {
    this.delegate = delegate;
  }

  public void log(Level level, String message) {
    delegate.log(level, message);
  }

  public boolean matchesLevel(Level level) {
    return delegate.getLevel().isLessSpecificThan(level);
  }

}
