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
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.common.Delegator;

/**
 * The <span class="notranslate">UseItemTickEvent</span> class is fired every gametick while a
 * [Mob](/modules/Mob) uses an [Item](/modules/Item). Setting the
 * [duration](/modules/UseItemEvent#duration) to zero or less cancels this event.
 *
 * #### Example
 *
 * Print messages while the player is eating a golden apple.
 *
 * <code>
 * Events.on('UseItemTickEvent'):call(function(event)
 *   if event.item.id == 'golden_apple' then
 *     print('Om nom '..event.duration)
 *   end
 * end)
 * </code>
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = UseItemTickEventClass.NAME, superClass = UseItemEventClass.class)
@GenerateLuaClassTable(instance = UseItemTickEventClass.Instance.class)
@GenerateLuaDoc(subtitle = "While an Entity uses an Item", type = EventClass.TYPE)
public class UseItemTickEventClass extends
    BasicLuaClass<LivingEntityUseItemEvent.Tick, UseItemTickEventClass.Instance<LivingEntityUseItemEvent.Tick>> {
  public static final String NAME = "UseItemTickEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new UseItemTickEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<LivingEntityUseItemEvent.Tick>> toLuaInstance(
      LivingEntityUseItemEvent.Tick javaInstance) {
    return new UseItemTickEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends LivingEntityUseItemEvent.Tick>
      extends UseItemEventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }
  }
}
