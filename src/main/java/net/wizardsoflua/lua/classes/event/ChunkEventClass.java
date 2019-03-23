package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkEvent;
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
 * The <span class="notranslate">ChunkEvent</span> is the common base class of
 * [ChunkLoadEvent](/modules/ChunkLoadEvent) and [ChunkUnloadEvent](/modules/ChunkUnloadEvent).
 *
 * Please note that instances of this event could occur asynchronously to the game loop. Hence, if
 * you use an event interceptor to handle them, make sure that your code is thread safe.
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = ChunkEventClass.NAME, superClass = EventClass.class)
@GenerateLuaClassTable(instance = ChunkEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public final class ChunkEventClass
    extends BasicLuaClass<ChunkEvent, ChunkEventClass.Instance<ChunkEvent>> {
  public static final String NAME = "ChunkEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new ChunkEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<ChunkEvent>> toLuaInstance(ChunkEvent javaInstance) {
    return new ChunkEventClassInstanceTable<>(new Instance<>(javaInstance, getName(), injector),
        getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends ChunkEvent> extends EventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    /**
     * This is the world where this event did occur.
     */
    @LuaProperty
    public World getWorld() {
      // TODO support IWorld interface
      // we can as of MC 1.13 savely downcast to World since a ChunkEvent always has a World
      // instance
      return (World) delegate.getWorld();
    }

    /**
     * This is the x-component of the chunk coordinate.
     */
    @LuaProperty
    public int getChunkX() {
      return delegate.getChunk().getPos().x;
    }

    /**
     * This is the z-component of the chunk coordinate.
     */
    @LuaProperty
    public int getChunkZ() {
      return delegate.getChunk().getPos().z;
    }

    // @LuaProperty
    // public Chunk getChunk() {
    // return delegate.getChunk();
    // }

  }
}
