package net.wizardsoflua.lua.classes.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.extension.api.InitializationContext;
import net.wizardsoflua.lua.extension.util.AbstractLuaClass;
import net.wizardsoflua.lua.module.events.EventHandlers;
import net.wizardsoflua.lua.module.events.EventsModule;

@GenerateLuaClassTable(instance = LivingEventClass2.Instance.class)
@GenerateLuaDoc(name = LivingEventClass2.NAME)
public class LivingEventClass2
    extends AbstractLuaClass<LivingEvent, LivingEventClass2InstanceTable<?>> {
  public static final String NAME = "LivingEvent";
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
    return new LivingEventClass2Table<>(this, getConverter());
  }

  @Override
  protected LivingEventClass2InstanceTable<?> toLuaInstance(LivingEvent javaInstance) {
    return new LivingEventClass2InstanceTable<>(new Instance<>(javaInstance, NAME, events),
        getConverter());
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends LivingEvent> extends EventClass2.Instance<D> {
    public Instance(D delegate, String name, EventHandlers events) {
      super(delegate, name, events);
    }

    @LuaProperty
    public EntityLivingBase getEntity() {
      return getDelegate().getEntityLiving();
    }
  }
}
