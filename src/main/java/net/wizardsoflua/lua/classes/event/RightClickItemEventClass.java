package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.common.Delegator;

/**
 * The <span class="notranslate">RightClickItemEvent</span> class is fired when a player
 * right-clicks somewhere with an [Item](/modules/Item).
 *
 * Note that this is NOT fired if the player is targeting a block (see
 * [RightClickBlockEvent](/modules/RightClickBlockEvent)) or an entity (see
 * [PlayerEntityInteractEvent](/modules/PlayerEntityInteractEvent)).
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = RightClickItemEventClass.NAME,
    superClass = PlayerInteractEventClass.class)
@GenerateLuaClassTable(instance = RightClickItemEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE, subtitle = "When a Player Right-Clicks with an Item")
public final class RightClickItemEventClass extends
    BasicLuaClass<PlayerInteractEvent.RightClickItem, RightClickItemEventClass.Instance<PlayerInteractEvent.RightClickItem>> {
  public static final String NAME = "RightClickItemEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new RightClickItemEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<PlayerInteractEvent.RightClickItem>> toLuaInstance(
      PlayerInteractEvent.RightClickItem javaInstance) {
    return new RightClickItemEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends PlayerInteractEvent.RightClickItem>
      extends PlayerInteractEventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }
  }
}
