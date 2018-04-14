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
import net.wizardsoflua.lua.extension.api.service.Converter;
import net.wizardsoflua.lua.extension.spi.LuaExtension;
import net.wizardsoflua.lua.extension.util.LuaTableExtension;

@GenerateLuaModuleTable
@GenerateLuaDoc(name = ItemsModule.NAME, subtitle = "Creating Items")
@AutoService(LuaExtension.class)
public class ItemsModule implements LuaTableExtension {
  public static final String NAME = "Items";
  @Inject
  private Converter converter;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table getTable() {
    return new ItemsModuleTable<>(this, converter);
  }

  @LuaFunction
  public ItemStack get(String name, @Nullable Integer amount) {
    amount = amount != null ? amount : 1;
    Item item = Item.getByNameOrId(name);
    return new ItemStack(item, amount);
  }
}
