package net.wizardsoflua.extension.spell.api.resource;

import java.time.Clock;

public interface Time {
  Clock getClock();

  long getTotalWorldTime();
}
