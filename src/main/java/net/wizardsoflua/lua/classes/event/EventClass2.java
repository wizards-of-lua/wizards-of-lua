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
import net.wizardsoflua.lua.extension.api.inject.AfterInjection;
import net.wizardsoflua.lua.extension.api.inject.Inject;
import net.wizardsoflua.lua.extension.api.service.Injector;
import net.wizardsoflua.lua.extension.api.service.LuaExtensionLoader;
import net.wizardsoflua.lua.extension.util.AbstractLuaClass;
import net.wizardsoflua.lua.module.events.EventHandlers;
import net.wizardsoflua.lua.module.events.EventsModule;

@GenerateLuaClassTable(instance = EventClass2.Instance.class)
@GenerateLuaDoc(name = EventClass2.NAME, subtitle = "The Event Base Class")
public class EventClass2 extends AbstractLuaClass<Event, EventClass2InstanceTable<?>> {
  public static final String NAME = "Event";
  @Inject
  private Injector injector;

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
    return new EventClass2InstanceTable<>(new Instance<>(javaInstance, NAME, injector),
        getConverter());
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends Event> extends ModifiableDelegator<D> {
    @Inject
    private LuaExtensionLoader extensionLoader;

    private final String name;
    private EventHandlers events;

    public Instance(D delegate, String name, Injector injector) {
      super(delegate);
      this.name = requireNonNull(name, "name == null!");
      injector.inject(this);
    }

    @AfterInjection
    public void initialize() {
      EventsModule module = extensionLoader.getLuaExtension(EventsModule.class);
      events = module.getDelegate();
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
