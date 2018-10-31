package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.sandius.rembulan.Table;
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
import net.wizardsoflua.lua.classes.common.Delegator;

/**
 * The <span class="notranslate">EntityEvent</span> is the base class of events about an entity.
 *
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = EntityEventClass.NAME, superClass = EventClass.class)
@GenerateLuaClassTable(instance = EntityEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public final class EntityEventClass
    extends BasicLuaClass<EntityEvent, EntityEventClass.Instance<EntityEvent>> {
  public static final String NAME = "EntityEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new EntityEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<EntityEvent>> toLuaInstance(EntityEvent javaInstance) {
    return new EntityEventClassInstanceTable<>(new Instance<>(javaInstance, getName(), injector),
        getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends EntityEvent> extends EventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    /**
     * The entity that this event is about.
     */
    @LuaProperty
    public Entity getEntity() {
      return delegate.getEntity();
    }
  }
}
