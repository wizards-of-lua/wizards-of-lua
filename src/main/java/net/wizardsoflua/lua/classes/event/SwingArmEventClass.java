package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.event.SwingArmEvent;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.common.Delegator;

/**
 * he <span class="notranslate">SwingArmEvent</span> is fired whenever a [Player](/modules/Player)
 * waves an arm. This can be the left arm or the right arm.
 *
 * This event is fired on three occasions:
 *
 * - just before the [RightClickBlockEvent](/modules/RightClickBlockEvent)
 *
 * - just before the [LeftClickBlockEvent](/modules/LeftClickBlockEvent)
 *
 * - when the player does a left-click into the air.
 *
 *
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = SwingArmEventClass.NAME, superClass = EventClass.class)
@GenerateLuaClassTable(instance = SwingArmEventClass.Instance.class)
@GenerateLuaDoc(type = EventClass.TYPE)
public final class SwingArmEventClass
    extends BasicLuaClass<SwingArmEvent, SwingArmEventClass.Instance<SwingArmEvent>> {
  public static final String NAME = "SwingArmEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new SwingArmEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<SwingArmEvent>> toLuaInstance(SwingArmEvent javaInstance) {
    return new SwingArmEventClassInstanceTable<>(new Instance<>(javaInstance, getName(), injector),
        getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends SwingArmEvent> extends EventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    /**
     * he hand the player waved. Can be 'MAIN_HAND' or 'OFF_HAND'.
     */
    @LuaProperty
    public EnumHand getHand() {
      return delegate.getHand();
    }

    /**
     * The item in the player's hand.
     */
    @LuaProperty
    public ItemStack getItem() {
      return delegate.getItemStack();
    }

    /**
     * The player that triggered this event.
     */
    @LuaProperty
    public EntityPlayer getPlayer() {
      return delegate.getPlayer();
    }
  }
}
