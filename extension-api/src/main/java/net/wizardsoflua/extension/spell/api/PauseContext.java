package net.wizardsoflua.extension.spell.api;

import net.sandius.rembulan.runtime.SchedulingContext;
import net.wizardsoflua.extension.spell.api.resource.LuaScheduler;

/**
 * A {@link PauseContext} is used to determine whether a spell should be paused. A
 * {@link PauseContext} can for instance be used when a spell is supposed to wait for an external
 * event. {@link #shouldPause()} is called frequently during execution, so it should be a cheap
 * computation. You can register {@link PauseContext}s via
 * {@link LuaScheduler#addPauseContext(PauseContext)}.
 *
 * @author Adrodoc
 * @see SchedulingContext#shouldPause()
 */
public interface PauseContext {
  /**
   * Returns whether or not the spell should be paused.
   *
   * @return whether or not the spell should be paused
   * @see SchedulingContext#shouldPause()
   */
  boolean shouldPause();
}
