package net.wizardsoflua.lua.extension.api;

import java.time.Clock;

public interface Time {
  Clock getClock();

  long getTotalWorldTime();
}
