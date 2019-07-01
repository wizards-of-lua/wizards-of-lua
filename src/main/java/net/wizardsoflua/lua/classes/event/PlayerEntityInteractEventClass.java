package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.common.Delegator;

/**
 * The <span class="notranslate">PlayerEntityInteractEvent</span> class is fired when a player
 * right-clicks somewhere an [entity](../Entity)
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = PlayerEntityInteractEventClass.NAME,
    superClass = PlayerInteractEventClass.class)
@GenerateLuaClassTable(instance = PlayerEntityInteractEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE, subtitle = "When a Player Right-Clicks on an Entity")
public final class PlayerEntityInteractEventClass extends
    BasicLuaClass<PlayerInteractEvent.EntityInteract, PlayerEntityInteractEventClass.Instance<PlayerInteractEvent.EntityInteract>> {
  public static final String NAME = "PlayerEntityInteractEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new PlayerEntityInteractEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<PlayerInteractEvent.EntityInteract>> toLuaInstance(
      PlayerInteractEvent.EntityInteract javaInstance) {
    return new PlayerEntityInteractEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends PlayerInteractEvent.EntityInteract>
      extends PlayerInteractEventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    /**
     * The target entity.
     */
    @LuaProperty
    public Entity getTarget() {
      return delegate.getTarget();
    }
  }
}
