package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
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
 * The <span class="notranslate">PopulateChunkEvent</span> is the base class of the
 * [PostPopulateChunkEvent](/modules/PostPopulateChunkEvent).
 * 
 * Please note that instances of these events could occur asynchronously to the game loop. Hence, if
 * you use an event interceptor to handle them, make sure that your code is thread safe.
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = PopulateChunkEventClass.NAME, superClass = EventClass.class)
@GenerateLuaClassTable(instance = PopulateChunkEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public final class PopulateChunkEventClass extends
    BasicLuaClass<PopulateChunkEvent, PopulateChunkEventClass.Instance<PopulateChunkEvent>> {
  public static final String NAME = "PopulateChunkEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new PopulateChunkEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<PopulateChunkEvent>> toLuaInstance(PopulateChunkEvent javaInstance) {
    return new PopulateChunkEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends PopulateChunkEvent> extends EventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    /**
     * This is the world where this event did occur.
     */
    @LuaProperty
    public World getWorld() {
      return delegate.getWorld();
    }

    /**
     * This is the x-component of the chunk coordinate.
     */
    @LuaProperty
    public int getChunkX() {
      return delegate.getChunkX();
    }

    /**
     * This is the z-component of the chunk coordinate.
     */
    @LuaProperty
    public int getChunkZ() {
      return delegate.getChunkZ();
    }

    // @LuaProperty
    // public Chunk getChunk() {
    // return delegate.getWorld().getChunkFromChunkCoords(delegate.getChunkX(),
    // delegate.getChunkZ());
    // }

  }
}
