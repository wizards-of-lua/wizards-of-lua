package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

/**
 * The <span class="notranslate">UseItemStopEvent</span> class is fired when a [Mob](/modules/Mob) stops
 * using an [Item](/modules/Item).
 */
@GenerateLuaClass(name = UseItemStopEventApi.NAME)
@GenerateLuaDoc(subtitle = "When an Entity stops using an Item")
public class UseItemStopEventApi<D extends LivingEntityUseItemEvent.Stop>
    extends UseItemEventApi<D> {
  public static final String NAME = "UseItemStopEvent";

  public UseItemStopEventApi(ProxyingLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }
}
