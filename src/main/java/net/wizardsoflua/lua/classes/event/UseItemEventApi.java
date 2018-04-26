package net.wizardsoflua.lua.classes.event;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;

/**
 * The <span class="notranslate">UseItemEvent</span> class is the base class of events about
 * [Item](/modules/Item) usage. Typical scenarios are:
 * <ul>
 * <li>Drawing a bow</li>
 * <li>Eating food</li>
 * <li>Drinking potions or milk</li>
 * <li>Guarding with a shield</li>
 * </ul>
 */
@GenerateLuaClass(name = UseItemEventApi.NAME)
@GenerateLuaDoc(subtitle = "When an Entity uses an Item")
public class UseItemEventApi<D extends LivingEntityUseItemEvent> extends LivingEventApi<D> {
  public static final String NAME = "UseItemEvent";

  public UseItemEventApi(DelegatorLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }

  /**
   * This is the used [item](/modules/Item).
   */
  @LuaProperty
  public ItemStack getItem() {
    return delegate.getItem();
  }

  /**
   * The time in ticks left until the item use is finished.
   *
   * #### Example
   *
   * Increase the time it takes to eat a golden apple to 5 seconds (100 gameticks), gold is pretty
   * hard to chew anyway.
   *
   * <code>
   * Events.on('UseItemStartEvent'):call(function(event)
   *   if event.item.id == 'golden_apple' then
   *     event.duration = 100
   *   end
   * end)
   * </code>
   */
  @LuaProperty
  public int getDuration() {
    return delegate.getDuration();
  }

  @LuaProperty
  public void setDuration(int duration) {
    delegate.setDuration(duration);
  }
}
