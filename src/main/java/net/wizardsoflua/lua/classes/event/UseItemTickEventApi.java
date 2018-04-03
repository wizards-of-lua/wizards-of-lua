package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

/**
 * The <span class="notranslate">UseItemTickEvent</span> class is fired while a [Mob](/modules/Mob) uses
 * an [Item](/modules/Item).
 */
@GenerateLuaClass(name = UseItemTickEventApi.NAME)
@GenerateLuaDoc(subtitle = "While an Entity uses an Item")
public class UseItemTickEventApi<D extends LivingEntityUseItemEvent.Tick>
    extends UseItemEventApi<D> {
  public static final String NAME = "UseItemTickEvent";

  public UseItemTickEventApi(ProxyingLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }
}
