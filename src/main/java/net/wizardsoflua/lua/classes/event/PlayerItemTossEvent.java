package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.item.ItemTossEvent;
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
 * The <span class="notranslate">PlayerItemTossEvent</span> is fired whenever a player tosses (for
 * example by pressing 'Q') an item or drag-n-drops a stack of items outside the inventory GUI
 * screens.
 *
 * Canceling the event will stop the items from entering the world, but will not prevent them being
 * removed from the inventory - and thus removed from the system.
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = PlayerItemTossEvent.NAME, superClass = EntityEventClass.class)
@GenerateLuaClassTable(instance = PlayerItemTossEvent.Instance.class)
@GenerateLuaDoc(subtitle = "When an Entity Enters the World", type = EventClass.TYPE)
public final class PlayerItemTossEvent
    extends BasicLuaClass<ItemTossEvent, PlayerItemTossEvent.Instance<ItemTossEvent>> {
  public static final String NAME = "PlayerItemTossEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new PlayerItemTossEventTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<ItemTossEvent>> toLuaInstance(ItemTossEvent javaInstance) {
    return new PlayerItemTossEventInstanceTable<>(new Instance<>(javaInstance, getName(), injector),
        getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends ItemTossEvent> extends EntityEventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    /**
     * The [player](/modules/Player) tossing the item.
     */
    @LuaProperty
    public EntityPlayer getPlayer() {
      return delegate.getPlayer();
    }

    /**
     * This is the [dropped item](/modules/DroppedItem) being tossed.
     */
    @LuaProperty
    public EntityItem getItem() {
      return delegate.getEntityItem();
    }

  }
}
