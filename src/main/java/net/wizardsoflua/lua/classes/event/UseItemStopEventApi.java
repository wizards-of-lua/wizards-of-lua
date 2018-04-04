package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

/**
 * The <span class="notranslate">UseItemStopEvent</span> class is fired when a [Mob](/modules/Mob)
 * or [Player](/modules/Player) stops using an [Item](/modules/Item) without
 * [finishing](/modules/UseItemFinishEvent) it. Currently the only vanilla item that is affected by
 * canceling this event is the bow. If this event is canceled the bow does not shoot an arrow.
 *
 * #### Example
 *
 * Print a message when the player stops eating a golden apple.
 *
 * <code>
 * Events.on('UseItemStopEvent'):call(function(event)
 *   if event.item.id == 'golden_apple' then
 *     print('Are you not hungry?')
 *   end
 * end)
 * </code>
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
