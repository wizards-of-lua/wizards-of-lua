package net.wizardsoflua.lua.classes.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.util.BasicLuaClass;
import net.wizardsoflua.lua.extension.util.LuaClassAttributes;

@LuaClassAttributes(name = LivingEventClass2.NAME, superClass = EventClass2.class)
@GenerateLuaClassTable(instance = LivingEventClass2.Instance.class)
@GenerateLuaDoc(name = LivingEventClass2.NAME, type = EventClass2.TYPE)
public class LivingEventClass2 extends BasicLuaClass<LivingEvent, LivingEventClass2.Instance<?>> {
  public static final String NAME = "LivingEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  public Table createRawTable() {
    return new LivingEventClass2Table<>(this, converters);
  }

  @Override
  protected Delegator<Instance<?>> toLuaInstance(LivingEvent javaInstance) {
    return new LivingEventClass2InstanceTable<>(new Instance<>(javaInstance, getName(), injector),
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
