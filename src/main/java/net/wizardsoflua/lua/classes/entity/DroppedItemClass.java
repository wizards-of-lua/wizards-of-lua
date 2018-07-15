package net.wizardsoflua.lua.classes.entity;

import com.google.auto.service.AutoService;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.util.BasicLuaClass;
import net.wizardsoflua.lua.extension.util.LuaClassAttributes;

/**
 * The <span class="notranslate">DroppedItem</span> class represents things that are lying somewhere
 * and can be collected by players.
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = DroppedItemClass.NAME, superClass = EntityClass.class)
@GenerateLuaClassTable(instance = DroppedItemClass.Instance.class)
@GenerateLuaDoc(subtitle = "Things That are Lying Around")
public class DroppedItemClass
    extends BasicLuaClass<EntityItem, DroppedItemClass.Instance<EntityItem>> {
  public static final String NAME = "DroppedItem";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new DroppedItemClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<EntityItem>> toLuaInstance(EntityItem javaInstance) {
    return new DroppedItemClassInstanceTable<>(new Instance<>(javaInstance, injector), getTable(),
        converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends EntityItem> extends EntityClass.Instance<D> {
    public Instance(D delegate, Injector injector) {
      super(delegate, injector);
    }

    /**
     * This is the [item](/modules/Item/) that has been dropped.
     */
    @LuaProperty
    public ItemStack getItem() {
      return delegate.getItem();
    }

    @LuaProperty
    public void setItem(ItemStack item) {
      delegate.setItem(item);
    }
  }
}
