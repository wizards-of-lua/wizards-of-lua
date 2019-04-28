package net.wizardsoflua;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.Clock;
import javax.inject.Singleton;
import com.google.common.annotations.VisibleForTesting;

/**
 * The {@link TimeService} holds a reference to the {@link Clock} used in the wizards-of-lua
 * modification. By having a central {@link Clock} provider the time can be stopped or changed
 * during testing.
 *
 * @author Adrodoc
 */
@Singleton
public class TimeService {
  private Clock clock = getDefaultClock();

  public Clock getClock() {
    return clock;
  }

  @VisibleForTesting
  public void setClock(Clock clock) {
    this.clock = checkNotNull(clock, "clock==null!");
  }

  @VisibleForTesting
  public Clock getDefaultClock() {
    return Clock.systemDefaultZone();
  }
}
