
package net.wizardsoflua.lua.scheduling;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;

import net.sandius.rembulan.runtime.SchedulingContext;

public class AggregatingSchedulingContext implements SchedulingContext {
  private final Collection<SchedulingContext> contexts = new ArrayList<>();

  public void addSchedulingContext(SchedulingContext context) {
    checkNotNull(context, "context == null!");
    contexts.add(context);
  }

  @Override
  public void registerTicks(int ticks) {
    for (SchedulingContext context : contexts) {
      context.registerTicks(ticks);
    }
  }

  @Override
  public boolean shouldPause() {
    boolean result = false;
    for (SchedulingContext context : contexts) {
      result |= context.shouldPause();
    }
    return result;
  }
}
