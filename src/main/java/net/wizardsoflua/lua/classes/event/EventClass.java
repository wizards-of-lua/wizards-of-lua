package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.sandius.rembulan.runtime.IllegalOperationAttemptException;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;
import net.wizardsoflua.lua.classes.GeneratedLuaInstance;
import net.wizardsoflua.lua.module.events.EventsModule;

@DeclareLuaClass(name = EventClass.NAME)
public class EventClass extends DelegatorLuaClass<Event, EventClass.Proxy<EventApi<Event>, Event>> {
  public static final String NAME = "Event";

  @Override
  public Proxy<EventApi<Event>, Event> toLua(Event javaObj) {
    return new Proxy<>(new EventApi<>(this, javaObj));
  }

  public static class Proxy<A extends EventApi<D>, D extends Event>
      extends GeneratedLuaInstance<A, D> {
    public Proxy(A api) {
      super(api);
      addImmutable("name", getName());
      addReadOnly("cancelable", this::isCancelable);
      add("canceled", this::isCanceled, this::setCanceled);
    }

    @Override
    public boolean isTransferable() {
      return true;
    }

    public String getName() {
      return getLuaClass().getName();
    }

    public boolean isCancelable() {
      EventsModule events = getLuaClass().getClassLoader().getEventsModule();
      if (!events.isDuringEventIntercepting()) {
        return false;
      }
      return delegate.isCancelable();
    }

    public boolean isCanceled() {
      return delegate.isCanceled();
    }

    public void setCanceled(Object arg) {
      boolean canceled = getConverters().toJava(boolean.class, arg, "canceled");
      if (!isCancelable()) {
        throw new IllegalOperationAttemptException("attempt to cancel " + getName());
      }
      delegate.setCanceled(canceled);
    }
  }
}
