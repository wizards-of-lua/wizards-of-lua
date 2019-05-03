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
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.LuaInstance;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.module.events.EventsModule;

/**
 * The <span class="notranslate">Event</span> class represents a notification about something that
 * happend in the world.
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = EventClass.NAME)
@GenerateLuaClassTable(instance = EventClass.Instance.class)
@GenerateLuaDoc(subtitle = "The Event Base Class", type = EventClass.TYPE)
public final class EventClass extends BasicLuaClass<Event, EventClass.Instance<Event>> {
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
  protected Delegator<Instance<Event>> toLuaInstance(Event javaInstance) {
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

    /**
     * The 'cancelable' property can be used to detect dynamically whether this event instance can
     * be [canceled](#canceled) by calling
     *
     * <code>
     * event.canceled = true
     * </code>
     *
     * In general, this is determined by the event class.
     *
     * For instance, a [BlockPlaceEvent](/modules/BlockPlaceEvent) is *cancelable*, but a
     * [SwingArmEvent](/modules/SwingArmEvent) is not.
     *
     * If *cancelable* is <span class="notranslate">*false*</span>, then setting
     * [canceled](#canceled) results in an error.
     *
     * Please note, an event can only be canceled during [event
     * interception](/modules/Events#intercept).
     *
     */
    @LuaProperty
    public boolean isCancelable() {
      if (!events.isDuringEventIntercepting()) {
        return false;
      }
      return delegate.isCancelable();
    }

    /**
     * The 'canceled' property can be used to define whether this event should be canceled.
     *
     * If *cancelable* is <span class="notranslate">*false*</span>, then setting
     * [canceled](#canceled) results in an error.
     *
     * Please note, an event can only be canceled during [event
     * interception](/modules/Events#intercept).
     *
     * #### Example
     *
     * Canceling all chat messages from player 'mickkay'.
     *
     * <code>
     * Events.on('ChatEvent'):call(function(event)
     *   if event.player.name == 'mickkay' then
     *      event.canceled = true
     *   end
     * end)
     * </code>
     */
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

    /**
     * The name of this kind of event. Use this name to [connect an event
     * queue](/modules/Events/#collect) to the event source for events of this kind.
     */
    @LuaProperty
    public String getName() {
      return name;
    }
  }
}

