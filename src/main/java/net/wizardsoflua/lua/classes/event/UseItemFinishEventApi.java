package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

/**
 * The <span class="notranslate">UseItemFinishEvent</span> class is fired when a [Mob](/modules/Mob)
 * finishes using an [Item](/modules/Item).
 */
@GenerateLuaClass(name = UseItemFinishEventApi.NAME)
@GenerateLuaDoc(subtitle = "When an Entity finishes using an Item")
public class UseItemFinishEventApi<D extends LivingEntityUseItemEvent.Finish>
    extends UseItemEventApi<D> {
  public static final String NAME = "UseItemFinishEvent";

  public UseItemFinishEventApi(ProxyingLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }
}
