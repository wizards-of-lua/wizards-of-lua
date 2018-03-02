package net.wizardsoflua.lua.classes.eventsubscription;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;

import net.sandius.rembulan.runtime.LuaFunction;

public class EventSubscription {
  private final ImmutableList<String> eventNames;
  private final LuaFunction eventHandler;
  private final Context context;

  public interface Context {
    void unsubscribe(EventSubscription subscription);
  }

  public EventSubscription(Iterable<String> eventNames, LuaFunction eventHandler, Context context) {
    this.eventNames = ImmutableList.copyOf(eventNames);
    this.eventHandler = requireNonNull(eventHandler, "eventHandler == null!");
    this.context = requireNonNull(context, "context == null!");
  }

  /**
   * @return the value of {@link #eventNames}
   */
  public ImmutableList<String> getEventNames() {
    return eventNames;
  }

  /**
   * @return the value of {@link #eventHandler}
   */
  public LuaFunction getEventHandler() {
    return eventHandler;
  }

  public void unsubscribe() {
    context.unsubscribe(this);
  }
}
