package net.wizardsoflua.lua.classes.event;

import static java.util.Objects.requireNonNull;

import javax.inject.Inject;

import com.google.auto.service.AutoService;

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
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.LuaInstance;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.util.BasicLuaClass;
import net.wizardsoflua.lua.extension.util.LuaClassAttributes;
import net.wizardsoflua.lua.module.events.EventsModule;

@AutoService(LuaConverter.class)
@LuaClassAttributes(name = EventClass.NAME)
@GenerateLuaClassTable(instance = EventClass.Instance.class)
@GenerateLuaDoc(subtitle = "The Event Base Class", type = EventClass.TYPE)
public class EventClass extends BasicLuaClass<Event, EventClass.Instance<?>> {
  public static final String NAME = "Event";
  public static final String TYPE = "event";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new EventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<?>> toLuaInstance(Event javaInstance) {
    return new EventClassInstanceTable<>(new Instance<>(javaInstance, getName(), injector),
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
      return delegate.isCancelable();
    }

    @LuaProperty
    public boolean isCanceled() {
      return delegate.isCanceled();
    }

    @LuaProperty
    public void setCanceled(boolean canceled) {
      if (!isCancelable()) {
        throw new IllegalOperationAttemptException("attempt to cancel " + getName());
      }
      delegate.setCanceled(canceled);
    }
  }
}
