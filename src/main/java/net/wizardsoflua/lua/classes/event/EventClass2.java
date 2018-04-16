package net.wizardsoflua.lua.classes.event;

import static java.util.Objects.requireNonNull;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.IllegalOperationAttemptException;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.classes.LuaInstance;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.api.inject.AfterInjection;
import net.wizardsoflua.lua.extension.api.inject.Inject;
import net.wizardsoflua.lua.extension.api.service.Injector;
import net.wizardsoflua.lua.extension.api.service.LuaConverters;
import net.wizardsoflua.lua.extension.api.service.SpellExtensions;
import net.wizardsoflua.lua.extension.util.DelegatorCachingLuaClass;
import net.wizardsoflua.lua.module.events.EventsModule;

@GenerateLuaClassTable(instance = EventClass2.Instance.class)
@GenerateLuaDoc(name = EventClass2.NAME, subtitle = "The Event Base Class")
public class EventClass2 extends DelegatorCachingLuaClass<Event> {
  public static final String NAME = "Event";
  @Inject
  private LuaConverters converters;
  @Inject
  private Injector injector;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table createRawTable() {
    return new EventClass2Table<>(this, converters);
  }

  @Override
  protected Delegator<Instance<?>> toLuaInstance(Event javaInstance) {
    return new EventClass2InstanceTable<>(new Instance<>(javaInstance, NAME, injector), getTable(),
        converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends Event> extends LuaInstance<D> {
    @Inject
    private SpellExtensions extensions;

    private final String name;
    private EventsModule events;

    public Instance(D delegate, String name, Injector injector) {
      super(delegate);
      this.name = requireNonNull(name, "name == null!");
      injector.inject(this);
    }

    @AfterInjection
    public void initialize() {
      events = extensions.getSpellExtension(EventsModule.class);
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
