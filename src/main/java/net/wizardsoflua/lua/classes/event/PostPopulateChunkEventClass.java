package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.minecraftforge.event.terraingen.PopulateChunkEvent;
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
 * Instances of the <span class="notranslate">PostPopulateChunkEvent</span> are fired when a new
 * chunk has been loaded and then been populated with blocks, ores, structures, and enities.
 * 
 * #### Example
 * 
 * Printing all <tt>PostPopulateChunkEvent</tt>s when they occur. 
 * 
 * <code>
 * Events.on('PostPopulateChunkEvent'):call(function(event)
 *   print(string.format("Populated world chunk at %s,%s", event.chunkX, event.chunkZ))
 * end)
 * </code>
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = PostPopulateChunkEventClass.NAME,
    superClass = PopulateChunkEventClass.class)
@GenerateLuaClassTable(instance = PostPopulateChunkEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public final class PostPopulateChunkEventClass extends
    BasicLuaClass<PopulateChunkEvent.Post, PostPopulateChunkEventClass.Instance<PopulateChunkEvent.Post>> {
  public static final String NAME = "PostPopulateChunkEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new PostPopulateChunkEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<PopulateChunkEvent.Post>> toLuaInstance(
      PopulateChunkEvent.Post javaInstance) {
    return new PostPopulateChunkEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends PopulateChunkEvent.Post>
      extends PopulateChunkEventClass.Instance<D> {

    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

  }
}
