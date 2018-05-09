package net.wizardsoflua.lua.classes.event;

import static java.util.Objects.requireNonNull;

import javax.inject.Inject;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.IllegalOperationAttemptException;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.lua.classes.LuaInstance;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.util.BasicLuaClass;
import net.wizardsoflua.lua.extension.util.LuaClassAttributes;
import net.wizardsoflua.lua.module.events.EventsModule;

@LuaClassAttributes(name = EventClass2.NAME)
@GenerateLuaClassTable(instance = EventClass2.Instance.class)
@GenerateLuaDoc(name = EventClass2.NAME, subtitle = "The Event Base Class")
public class EventClass2 extends BasicLuaClass<Event, EventClass2.Instance<?>> {
  public static final String NAME = "Event";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  public Table createRawTable() {
    return new EventClass2Table<>(this, converters);
  }

  @Override
  protected Delegator<Instance<?>> toLuaInstance(Event javaInstance) {
    return new EventClass2InstanceTable<>(new Instance<>(javaInstance, getName(), injector),
        getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends Event> extends LuaInstance<D> {
    private final String name;
    @Inject
    private EventsModule events;

    public Instance(D delegate, String name, Injector injector) {
      super(delegate);
      this.name = requireNonNull(name, "name == null!");
      injector.injectMembers(this);
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
