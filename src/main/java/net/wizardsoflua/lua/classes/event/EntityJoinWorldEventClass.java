package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
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
 * The <span class="notranslate">EntityJoinWorldEvent</span> is fired when an entity joins the
 * world. This happens e.g. when an entity is spawned and when a chunk with existing entities is
 * loaded into the server's memory.
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = EntityJoinWorldEventClass.NAME, superClass = EntityEventClass.class)
@GenerateLuaClassTable(instance = EntityJoinWorldEventClass.Instance.class)
@GenerateLuaDoc(subtitle = "When an Entity Enters the World", type = EventClass.TYPE)
public final class EntityJoinWorldEventClass extends
    BasicLuaClass<EntityJoinWorldEvent, EntityJoinWorldEventClass.Instance<EntityJoinWorldEvent>> {
  public static final String NAME = "EntityJoinWorldEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new EntityJoinWorldEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<EntityJoinWorldEvent>> toLuaInstance(
      EntityJoinWorldEvent javaInstance) {
    return new EntityJoinWorldEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends EntityJoinWorldEvent>
      extends EntityEventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    /**
     * This is the [world](/modules/World) in which this entity is going to join.
     */
    @LuaProperty
    public World getWorld() {
      return delegate.getWorld();
    }

  }
}
