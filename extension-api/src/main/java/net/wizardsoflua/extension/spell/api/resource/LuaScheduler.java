package net.wizardsoflua.extension.spell.api.resource;

import net.sandius.rembulan.LuaRuntimeException;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.Continuation;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.IllegalOperationAttemptException;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.UnresolvedControlThrowable;
import net.wizardsoflua.extension.spell.api.LuaTickListener;
import net.wizardsoflua.extension.spell.api.PauseContext;

public interface LuaScheduler {
  Object[] call(long luaTickLimit, LuaFunction function, Object... args)
      throws CallException, CallPausedException, InterruptedException;

  Object[] callUnpausable(long luaTickLimit, LuaFunction function, Object... args)
      throws CallException, InterruptedException;

  Object[] resume(long luaTickLimit, Continuation continuation)
      throws CallException, CallPausedException, InterruptedException;

  Object[] resumeUnpausable(long luaTickLimit, Continuation continuation)
      throws CallException, InterruptedException;

  /**
   * Pauses the current Lua execution.
   *
   * @param context the {@link ExecutionContext} of the current Lua execution
   * @throws UnresolvedControlThrowable if the Lua execution is paused
   * @throws IllegalOperationAttemptException if the current Lua execution is not pausable
   * @see ExecutionContext#pauseIfRequested()
   */
  void pause(ExecutionContext context)
      throws UnresolvedControlThrowable, IllegalOperationAttemptException;

  /**
   * Pauses the current Lua execution if either
   * <p>
   * 1. The {@link #getAllowance() allowance} hit zero and {@link #isAutosleep() autosleep} is
   * enabled.<br>
   * or<br>
   * 2. One of the {@link PauseContext}s wants to sleep.
   * <p>
   * If the {@link #getAllowance() allowance} hit zero and {@link #isAutosleep() autosleep} is
   * disabled a {@link LuaRuntimeException} is thrown.
   *
   * @param context the {@link ExecutionContext} of the current Lua execution
   * @throws UnresolvedControlThrowable if the Lua execution is paused
   * @throws LuaRuntimeException if the {@link #getAllowance() allowance} hit zero and
   *         {@link #isAutosleep() autosleep} is disabled a {@link LuaRuntimeException} is thrown
   * @throws IllegalOperationAttemptException if the current Lua execution is not pausable
   * @see ExecutionContext#pauseIfRequested()
   */
  void pauseIfRequested(ExecutionContext context)
      throws UnresolvedControlThrowable, LuaRuntimeException, IllegalOperationAttemptException;

  /**
   * Send the current Lua execution to sleep for the specified amount of game ticks.
   *
   * @param context the {@link ExecutionContext} of the current Lua execution
   * @param ticks the number of game ticks the execution should be paused
   * @throws UnresolvedControlThrowable
   * @throws IllegalOperationAttemptException if the current Lua execution is not pausable
   */
  void sleep(ExecutionContext context, int ticks)
      throws UnresolvedControlThrowable, IllegalOperationAttemptException;

  /**
   * The number of Lua ticks left for the current Lua execution in the current game tick. When the
   * allowance hits zero the current Lua execution is either paused or terminated depending on
   * {@link #isAutosleep()}.
   *
   * @return the number of Lua ticks left for the current Lua execution in the current game tick
   */
  long getAllowance();

  /**
   * Returns whether or not to pause the current Lua execution when the {@link #getAllowance()
   * allowance} hits zero.
   *
   * @return whether or not to pause the current Lua execution when the {@link #getAllowance()
   *         allowance} hits zero
   */
  boolean isAutosleep();

  /**
   * Sets the value of {@link #isAutosleep() autosleep} for the current spell. This can only be done
   * if the current Lua execution is pausable. This is for instance not the case during event
   * interception.
   *
   * @param autosleep
   * @throws IllegalOperationAttemptException if the current Lua execution is not pausable
   */
  void setAutosleep(boolean autosleep) throws IllegalOperationAttemptException;

  /**
   * Adds the specified {@link LuaTickListener} to the current spell if it is not present yet. This
   * is based on {@link #equals(Object)}.
   *
   * @param tickListener
   * @return {@code true} if the {@link LuaTickListener} was added, {@code false} if it was already
   *         present
   */
  boolean addTickListener(LuaTickListener tickListener);

  /**
   * Removes the specified {@link LuaTickListener} from the current spell if it is present. This is
   * based on {@link #equals(Object)}.
   *
   * @param tickListener
   * @return {@code true} if the {@link LuaTickListener} was removed, {@code false} if it was not
   *         present
   */
  boolean removeTickListener(LuaTickListener tickListener);

  /**
   * Adds the specified {@link PauseContext} to the current spell if it is not present yet. This is
   * based on {@link #equals(Object)}.
   *
   * @param pauseContext
   * @return {@code true} if the {@link PauseContext} was added, {@code false} if it was already
   *         present
   */
  boolean addPauseContext(PauseContext pauseContext);

  /**
   * Removes the specified {@link PauseContext} from the current spell if it is present. This is
   * based on {@link #equals(Object)}.
   *
   * @param pauseContext
   * @return {@code true} if the {@link PauseContext} was removed, {@code false} if it was not
   *         present
   */
  boolean removePauseContext(PauseContext pauseContext);
}
