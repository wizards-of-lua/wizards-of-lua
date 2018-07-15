package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.util.BasicLuaClass;
import net.wizardsoflua.lua.extension.util.LuaClassAttributes;

/**
 * The <span class="notranslate">UseItemFinishEvent</span> class is fired after a
 * [Mob](/modules/Mob) or [Player](/modules/Player) finishes using an [Item](/modules/Item). The
 * [item](/modules/UseItemEvent#item) and [resultItem](#resultItem) reflect the state after item
 * use. If the item in use had a [count](/modules/Item#count) of 1 then the item in this event is
 * air.
 *
 * #### Example
 *
 * Print a message when the player finishes eating a golden apple.
 *
 * <code>
 * local itemsInUse = {}
 * Events.on('UseItemStartEvent'):call(function(event)
 *   itemsInUse[event.entity] = event.item.id
 * end)
 * Events.on('UseItemStopEvent'):call(function(event)
 *   itemsInUse[event.entity] = nil
 * end)
 * Events.on('UseItemFinishEvent'):call(function(event)
 *   local itemInUse = itemsInUse[event.entity]
 *   itemsInUse[event.entity] = nil
 *   if itemInUse == 'golden_apple' then
 *     print('That was delicious!')
 *   end
 * end)
 * </code>
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = UseItemFinishEventClass.NAME, superClass = UseItemEventClass.class)
@GenerateLuaClassTable(instance = UseItemFinishEventClass.Instance.class)
@GenerateLuaDoc(subtitle = "When an Entity finishes using an Item", type = EventClass.TYPE)
public class UseItemFinishEventClass extends
    BasicLuaClass<LivingEntityUseItemEvent.Finish, UseItemFinishEventClass.Instance<LivingEntityUseItemEvent.Finish>> {
  public static final String NAME = "UseItemFinishEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new UseItemFinishEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<LivingEntityUseItemEvent.Finish>> toLuaInstance(
      LivingEntityUseItemEvent.Finish javaInstance) {
    return new UseItemFinishEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends LivingEntityUseItemEvent.Finish>
      extends UseItemEventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    /**
     * This item is placed in the players inventory in replacement of the item that is currently
     * used.
     *
     * #### Example
     *
     * Print a message when the player finishes eating a golden apple.
     *
     * <code>
     * local itemsInUse = {}
     * Events.on('UseItemStartEvent'):call(function(event)
     *   itemsInUse[event.entity] = event.item.id
     * end)
     * Events.on('UseItemStopEvent'):call(function(event)
     *   itemsInUse[event.entity] = nil
     * end)
     * Events.on('UseItemFinishEvent'):call(function(event)
     *   local itemInUse = itemsInUse[event.entity]
     *   itemsInUse[event.entity] = nil
     *   if itemInUse == 'golden_apple' then
     *     event.resultItem = Items.get('apple')
     *   end
     * end)
     * </code>
     */
    @LuaProperty
    public ItemStack getResultItem() {
      return delegate.getResultStack();
    }

    @LuaProperty
    public void setResultItem(ItemStack result) {
      delegate.setResultStack(result);
    }
  }
}
