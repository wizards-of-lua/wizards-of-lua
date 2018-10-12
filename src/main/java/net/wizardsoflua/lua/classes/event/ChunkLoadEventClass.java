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
 * The <span class="notranslate">ChunkLoadEvent</span> occurs when a world chunk is loaded into the
 * server's memory.
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = ChunkLoadEventClass.NAME, superClass = ChunkEventClass.class)
@GenerateLuaClassTable(instance = ChunkLoadEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public final class ChunkLoadEventClass
    extends BasicLuaClass<ChunkEvent.Load, ChunkLoadEventClass.Instance<ChunkEvent.Load>> {
  public static final String NAME = "ChunkLoadEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new ChunkLoadEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<ChunkEvent.Load>> toLuaInstance(ChunkEvent.Load javaInstance) {
    return new ChunkLoadEventClassInstanceTable<>(new Instance<>(javaInstance, getName(), injector),
        getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends ChunkEvent.Load> extends ChunkEventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }
  }
}
