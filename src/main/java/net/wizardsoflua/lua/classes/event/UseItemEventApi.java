package net.wizardsoflua.lua.classes.event;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

/**
 * The <span class="notranslate">UseItemEvent</span> class is fired when a [Mob](/modules/Mob) uses
 * an [Item](/modules/Item).
 */
@GenerateLuaClass(name = UseItemEventApi.NAME)
@GenerateLuaDoc(subtitle = "When an Entity uses an Item")
public class UseItemEventApi<D extends LivingEntityUseItemEvent> extends LivingEventApi<D> {
  public static final String NAME = "UseItemEvent";

  public UseItemEventApi(ProxyingLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }

  /**
   * This is the [item](/modules/Item/) that has been dropped.
   */
  @LuaProperty
  public ItemStack getItem() {
    return delegate.getItem();
  }

  @LuaProperty
  public int getDuration() {
    return delegate.getDuration();
  }

  @LuaProperty
  public void setDuration(int duration) {
    delegate.setDuration(duration);
  }
}
