package net.wizardsoflua.testenv.event;

import org.apache.logging.log4j.core.LogEvent;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ServerLog4jEvent extends Event {

  private final LogEvent logEvent;

  public ServerLog4jEvent(LogEvent logEvent) {
    this.logEvent = logEvent;
  }

  public LogEvent getLogEvent() {
    return logEvent;
  }

}
