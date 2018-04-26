package net.wizardsoflua.lua.scheduling;

import static com.google.common.base.Preconditions.checkArgument;

import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.Continuation;

public class CallFellAsleepException extends CallPausedException {
  private static final long serialVersionUID = 1L;

  private final int sleepDuration;

  public CallFellAsleepException(int sleepDuration, Continuation continuation) {
    super(continuation);
    checkArgument(sleepDuration > 0, "Can't sleep 0 or less ticks");
    this.sleepDuration = sleepDuration;
  }

  public int getSleepDuration() {
    return sleepDuration;
  }
}
