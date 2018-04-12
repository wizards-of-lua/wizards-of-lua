package net.wizardsoflua.lua.classes.event;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;

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
@GenerateLuaClass(name = UseItemFinishEventApi.NAME)
@GenerateLuaDoc(subtitle = "When an Entity finishes using an Item")
public class UseItemFinishEventApi<D extends LivingEntityUseItemEvent.Finish>
    extends UseItemEventApi<D> {
  public static final String NAME = "UseItemFinishEvent";

  public UseItemFinishEventApi(DelegatorLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }

  /**
   * This item is placed in the players inventory in replacement of the item that is currently used.
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
