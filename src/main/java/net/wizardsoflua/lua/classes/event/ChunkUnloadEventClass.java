package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.minecraftforge.event.world.ChunkEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.common.Delegator;

/**
 * The <span class="notranslate">ChunkUnloadEvent</span> occurs when a world chunk is unloaded from
 * the server's memory.
 * 
 * #### Example
 * 
 * Printing all <tt>ChunkUnloadEvent</tt>s when they occur.
 * 
 * <code>
 * Events.on('ChunkUnloadEvent'):call(function(event)
 *  print(string.format("Unloaded world chunk at %s,%s", event.chunkX, event.chunkZ))
 * end)
 * </code>
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = ChunkUnloadEventClass.NAME, superClass = ChunkEventClass.class)
@GenerateLuaClassTable(instance = ChunkUnloadEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public final class ChunkUnloadEventClass
    extends BasicLuaClass<ChunkEvent.Unload, ChunkUnloadEventClass.Instance<ChunkEvent.Unload>> {
  public static final String NAME = "ChunkUnloadEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new ChunkUnloadEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<ChunkEvent.Unload>> toLuaInstance(ChunkEvent.Unload javaInstance) {
    return new ChunkUnloadEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends ChunkEvent.Unload> extends ChunkEventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }
  }
}
