package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

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
@GenerateLuaClass(name = UseItemStartEventApi.NAME)
@GenerateLuaDoc(subtitle = "When an Entity starts using an Item")
public class UseItemStartEventApi<D extends LivingEntityUseItemEvent.Start>
    extends UseItemEventApi<D> {
  public static final String NAME = "UseItemStartEvent";

  public UseItemStartEventApi(ProxyingLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }
}
