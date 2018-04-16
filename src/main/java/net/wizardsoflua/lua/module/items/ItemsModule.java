package net.wizardsoflua.lua.module.items;

import javax.annotation.Nullable;

import com.google.auto.service.AutoService;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.lua.extension.api.inject.Inject;
import net.wizardsoflua.lua.extension.api.service.LuaConverters;
import net.wizardsoflua.lua.extension.spi.SpellExtension;
import net.wizardsoflua.lua.extension.util.LuaTableExtension;

@GenerateLuaModuleTable
@GenerateLuaDoc(name = ItemsModule.NAME, subtitle = "Creating Items")
@AutoService(SpellExtension.class)
public class ItemsModule extends LuaTableExtension {
  public static final String NAME = "Items";
  @Inject
  private LuaConverters converters;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table getTable() {
    return new ItemsModuleTable<>(this, converters);
  }

  @LuaFunction
  public ItemStack get(String name, @Nullable Integer amount) {
    amount = amount != null ? amount : 1;
    Item item = Item.getByNameOrId(name);
    return new ItemStack(item, amount);
  }
}
