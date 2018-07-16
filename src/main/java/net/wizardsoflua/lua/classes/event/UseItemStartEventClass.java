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
 * The <span class="notranslate">UseItemStartEvent</span> class is fired when a [Mob](/modules/Mob)
 * or [Player](/modules/Player) starts using an [Item](/modules/Item). Setting the
 * [duration](/modules/UseItemEvent#duration) to zero or less cancels this event.
 *
 * #### Example
 *
 * Prevent those nasty skeletons from shooting you.
 *
 * <code>
 * Events.on('UseItemStartEvent'):call(function(event)
 *   if event.entity.name == 'Skeleton' then
 *     event.canceled = true
 *   end
 * end)
 * </code>
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = UseItemStartEventClass.NAME, superClass = UseItemEventClass.class)
@GenerateLuaClassTable(instance = UseItemStartEventClass.Instance.class)
@GenerateLuaDoc(subtitle = "When an Entity starts using an Item", type = EventClass.TYPE)
public class UseItemStartEventClass extends
    BasicLuaClass<LivingEntityUseItemEvent.Start, UseItemStartEventClass.Instance<LivingEntityUseItemEvent.Start>> {
  public static final String NAME = "UseItemStartEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new UseItemStartEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<LivingEntityUseItemEvent.Start>> toLuaInstance(
      LivingEntityUseItemEvent.Start javaInstance) {
    return new UseItemStartEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends LivingEntityUseItemEvent.Start>
      extends UseItemEventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }
  }
}
