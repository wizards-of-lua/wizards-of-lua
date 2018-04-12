package net.wizardsoflua.lua.classes.event;

import static java.util.Objects.requireNonNull;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.IllegalOperationAttemptException;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.classes.common.ModifiableDelegator;
import net.wizardsoflua.lua.extension.api.InitializationContext;
import net.wizardsoflua.lua.extension.util.AbstractLuaClass;
import net.wizardsoflua.lua.module.events.EventHandlers;
import net.wizardsoflua.lua.module.events.EventsModule;

@GenerateLuaClassTable(instance = EventClass2.Instance.class)
@GenerateLuaDoc(name = EventClass2.NAME, subtitle = "The Event Base Class")
public class EventClass2 extends AbstractLuaClass<Event, EventClass2InstanceTable<?>> {
  public static final String NAME = "Event";
  private EventHandlers events;

  @Override
  public void initialize(InitializationContext context) {
    super.initialize(context);
    EventsModule module = context.getLuaExtensionLoader().getLuaExtension(EventsModule.class);
    events = module.getDelegate();
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table createTable() {
    return new EventClass2Table<>(this, getConverter());
  }

  @Override
  protected EventClass2InstanceTable<?> toLuaInstance(Event javaInstance) {
    return new EventClass2InstanceTable<>(new Instance<>(javaInstance, NAME, events),
        getConverter());
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends Event> extends ModifiableDelegator<D> {
    private final String name;
    private final EventHandlers events;

    public Instance(D delegate, String name, EventHandlers events) {
      super(delegate);
      this.name = requireNonNull(name, "name == null!");
      this.events = requireNonNull(events, "events == null!");
    }

    @LuaProperty
    public String getName() {
      return name;
    }

    @LuaProperty
    public boolean isCancelable() {
      if (!events.isDuringEventIntercepting()) {
        return false;
      }
      return getDelegate().isCancelable();
    }

    @LuaProperty
    public boolean isCanceled() {
      return getDelegate().isCanceled();
    }

    @LuaProperty
    public void setCanceled(boolean canceled) {
      if (!isCancelable()) {
        throw new IllegalOperationAttemptException("attempt to cancel " + getName());
      }
      getDelegate().setCanceled(canceled);
    }
  }
}
