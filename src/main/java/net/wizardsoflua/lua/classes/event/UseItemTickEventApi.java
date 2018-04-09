package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;

/**
 * The <span class="notranslate">UseItemTickEvent</span> class is fired every gametick while a
 * [Mob](/modules/Mob) uses an [Item](/modules/Item). Setting the
 * [duration](/modules/UseItemEvent#duration) to zero or less cancels this event.
 *
 * #### Example
 *
 * Print messages while the player is eating a golden apple.
 *
 * <code>
 * Events.on('UseItemTickEvent'):call(function(event)
 *   if event.item.id == 'golden_apple' then
 *     print('Om nom '..event.duration)
 *   end
 * end)
 * </code>
 */
@GenerateLuaClass(name = UseItemTickEventApi.NAME)
@GenerateLuaDoc(subtitle = "While an Entity uses an Item")
public class UseItemTickEventApi<D extends LivingEntityUseItemEvent.Tick>
    extends UseItemEventApi<D> {
  public static final String NAME = "UseItemTickEvent";

  public UseItemTickEventApi(DelegatorLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }
}
