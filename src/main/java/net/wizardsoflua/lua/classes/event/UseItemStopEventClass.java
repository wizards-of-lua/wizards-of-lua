package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.util.BasicLuaClass;
import net.wizardsoflua.lua.extension.util.LuaClassAttributes;

/**
 * The <span class="notranslate">UseItemStopEvent</span> class is fired when a [Mob](/modules/Mob)
 * or [Player](/modules/Player) stops using an [Item](/modules/Item) without
 * [finishing](/modules/UseItemStopEvent) it. Currently the only vanilla item that is affected by
 * canceling this event is the bow. If this event is canceled the bow does not shoot an arrow.
 *
 * #### Example
 *
 * Print a message when the player stops eating a golden apple.
 *
 * <code>
 * Events.on('UseItemStopEvent'):call(function(event)
 *   if event.item.id == 'golden_apple' then
 *     print('Are you not hungry?')
 *   end
 * end)
 * </code>
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = UseItemStopEventClass.NAME, superClass = UseItemEventClass.class)
@GenerateLuaClassTable(instance = UseItemStopEventClass.Instance.class)
@GenerateLuaDoc(subtitle = "When an Entity stops using an Item", type = EventClass.TYPE)
public class UseItemStopEventClass extends
    BasicLuaClass<LivingEntityUseItemEvent.Stop, UseItemStopEventClass.Instance<LivingEntityUseItemEvent.Stop>> {
  public static final String NAME = "UseItemStopEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new UseItemStopEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<LivingEntityUseItemEvent.Stop>> toLuaInstance(
      LivingEntityUseItemEvent.Stop javaInstance) {
    return new UseItemStopEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends LivingEntityUseItemEvent.Stop>
      extends UseItemEventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }
  }
}
