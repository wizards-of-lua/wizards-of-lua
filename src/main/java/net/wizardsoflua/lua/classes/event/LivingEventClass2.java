package net.wizardsoflua.lua.classes.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.extension.api.inject.Inject;
import net.wizardsoflua.lua.extension.api.service.Injector;
import net.wizardsoflua.lua.extension.util.AbstractLuaClass;

@GenerateLuaClassTable(instance = LivingEventClass2.Instance.class)
@GenerateLuaDoc(name = LivingEventClass2.NAME)
public class LivingEventClass2
    extends AbstractLuaClass<LivingEvent, LivingEventClass2InstanceTable<?>> {
  public static final String NAME = "LivingEvent";
  @Inject
  private Injector injector;

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
    return new LivingEventClass2InstanceTable<>(new Instance<>(javaInstance, NAME, injector),
        getConverter());
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends LivingEvent> extends EventClass2.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    @LuaProperty
    public EntityLivingBase getEntity() {
      return getDelegate().getEntityLiving();
    }
  }
}
