package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
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
 * The <span class="notranslate">UseItemEvent</span> class is the base class of events about
 * [Item](/modules/Item) usage. Typical scenarios are:
 * <ul>
 * <li>Drawing a bow</li>
 * <li>Eating food</li>
 * <li>Drinking potions or milk</li>
 * <li>Guarding with a shield</li>
 * </ul>
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = UseItemEventClass.NAME, superClass = LivingEventClass.class)
@GenerateLuaClassTable(instance = UseItemEventClass.Instance.class)
@GenerateLuaDoc(subtitle = "When an Entity uses an Item", type = EventClass.TYPE)
public class UseItemEventClass extends
    BasicLuaClass<LivingEntityUseItemEvent, UseItemEventClass.Instance<LivingEntityUseItemEvent>> {
  public static final String NAME = "UseItemEvent";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new UseItemEventClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<LivingEntityUseItemEvent>> toLuaInstance(
      LivingEntityUseItemEvent javaInstance) {
    return new UseItemEventClassInstanceTable<>(new Instance<>(javaInstance, getName(), injector),
        getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends LivingEntityUseItemEvent>
      extends LivingEventClass.Instance<D> {
    public Instance(D delegate, String name, Injector injector) {
      super(delegate, name, injector);
    }

    /**
     * This is the used [item](/modules/Item).
     */
    @LuaProperty
    public ItemStack getItem() {
      return delegate.getItem();
    }

    /**
     * The time in ticks left until the item use is finished.
     *
     * #### Example
     *
     * Increase the time it takes to eat a golden apple to 5 seconds (100 gameticks), gold is pretty
     * hard to chew anyway.
     *
     * <code>
     * Events.on('UseItemStartEvent'):call(function(event)
     *   if event.item.id == 'golden_apple' then
     *     event.duration = 100
     *   end
     * end)
     * </code>
     */
    @LuaProperty
    public int getDuration() {
      return delegate.getDuration();
    }

    @LuaProperty
    public void setDuration(int duration) {
      delegate.setDuration(duration);
    }
  }
}
