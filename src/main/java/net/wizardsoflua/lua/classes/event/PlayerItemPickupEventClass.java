package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
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
 * The <span class="notranslate">PlayerItemPickupEvent</span> is fired whenever a
 * [Player](/modules/Player) picks up an [DroppedItem](/modules/DroppedItem).
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = PlayerItemPickupEventClass.NAME, superClass = EventClass.class)
@GenerateLuaClassTable(instance = PlayerItemPickupEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE, subtitle = "When a Player Collects Something")
public final class PlayerItemPickupEventClass extends
    BasicLuaClass<PlayerEvent.ItemPickupEvent, PlayerItemPickupEventClass.Instance<PlayerEvent.ItemPickupEvent>> {
  public static final String NAME = "PlayerItemPickupEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new PlayerItemPickupEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<PlayerEvent.ItemPickupEvent>> toLuaInstance(
      PlayerEvent.ItemPickupEvent javaInstance) {
    return new PlayerItemPickupEventClassInstanceTable<>(
        new Instance<>(javaInstance, getName(), injector), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends PlayerEvent.ItemPickupEvent>
      extends EventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    /**
     * The player that triggered this event.
     */
    @LuaProperty
    public EntityPlayer getPlayer() {
      return delegate.player;
    }

    /**
     * The item that has been collected.
     */
    @LuaProperty
    public EntityItem getItem() {
      return delegate.getOriginalEntity();
    }
  }
}
