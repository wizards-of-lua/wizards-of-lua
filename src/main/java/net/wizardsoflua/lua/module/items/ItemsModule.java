package net.wizardsoflua.lua.module.items;

import javax.annotation.Nullable;
import com.google.auto.service.AutoService;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.extension.LuaTableExtension;

/**
 * The <span class="notranslate">Items</span> module can be used to create an [item](/modules/Item/)
 * of any type.
 */
@AutoService(SpellExtension.class)
@GenerateLuaModuleTable
@GenerateLuaDoc(name = ItemsModule.NAME, subtitle = "Creating Items")
public class ItemsModule extends LuaTableExtension {
  public static final String NAME = "Items";
  @Resource
  private LuaConverters converters;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table getTable() {
    return new ItemsModuleTable<>(this, converters);
  }

  /**
   * The 'get' function returns a new [item](/modules/Item/) of the given type and amount.
   *
   * #### Example
   *
   * Creating one diamond axe and putting it into the player's hand.
   *
   * <code>
   * local axe = Items.get("diamond_axe")
   * spell.owner.mainhand = axe
   * </code>
   *
   * #### Example
   *
   * Creating a full stack of wheat and putting it into the wizard's hand.
   *
   * <code>
   * spell.owner.mainhand = Items.get("wheat", 64)
   * </code>
   *
   */
  @LuaFunction
  public ItemStack get(String name, @Nullable Integer amount) {
    amount = amount != null ? amount : 1;
    Item item = IRegistry.field_212630_s.func_212608_b(new ResourceLocation(name));
    return new ItemStack(item, amount);
  }
}
