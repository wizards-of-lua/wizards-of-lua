package net.wizardsoflua.extension.spell.api;

import net.sandius.rembulan.runtime.SchedulingContext;
import net.wizardsoflua.extension.spell.api.resource.LuaScheduler;

/**
 * A {@link LuaTickListener} is notified whenever a spell consumes Lua ticks. You can register
 * {@link LuaTickListener}s via {@link LuaScheduler#addTickListener(LuaTickListener)}.
 *
 * @author Adrodoc
 * @see SchedulingContext#registerTicks(int)
 */
public interface LuaTickListener {
  /**
   * Informs the listener that the spell is about to consume or has consumed Lua ticks.
   * 
   * @param ticks the number of Lua ticks
   * @see SchedulingContext#registerTicks(int)
   */
  void registerTicks(int ticks);
}
