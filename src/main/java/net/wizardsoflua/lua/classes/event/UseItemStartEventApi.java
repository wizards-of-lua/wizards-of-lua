package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

/**
 * The <span class="notranslate">UseItemEvent</span> class is fired when a [Mob](/modules/Mob)
 * starts using an [Item](/modules/Item).
 */
@GenerateLuaClass(name = UseItemStartEventApi.NAME)
@GenerateLuaDoc(subtitle = "When an Entity starts using an Item")
public class UseItemStartEventApi<D extends LivingEntityUseItemEvent.Start>
    extends UseItemEventApi<D> {
  public static final String NAME = "UseItemStartEvent";

  public UseItemStartEventApi(ProxyingLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }
}
