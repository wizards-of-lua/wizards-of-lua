package net.wizardsoflua.lua.classes.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.api.inject.Inject;
import net.wizardsoflua.lua.extension.api.service.Injector;
import net.wizardsoflua.lua.extension.api.service.LuaConverters;
import net.wizardsoflua.lua.extension.util.DelegatorCachingLuaClass;

@GenerateLuaClassTable(instance = LivingEventClass2.Instance.class)
@GenerateLuaDoc(name = LivingEventClass2.NAME)
public class LivingEventClass2 extends DelegatorCachingLuaClass<LivingEvent> {
  public static final String NAME = "LivingEvent";
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
    return new LivingEventClass2Table<>(this, converters);
  }

  @Override
  protected Delegator<Instance<?>> toLuaInstance(LivingEvent javaInstance) {
    return new LivingEventClass2InstanceTable<>(new Instance<>(javaInstance, NAME, injector),
        getTable(), converters);
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
