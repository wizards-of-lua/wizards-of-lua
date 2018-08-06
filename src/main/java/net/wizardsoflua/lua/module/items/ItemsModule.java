package net.wizardsoflua.lua.module.items;

import javax.annotation.Nullable;

import com.google.auto.service.AutoService;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.extension.LuaTableExtension;

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

  @LuaFunction
  public ItemStack get(String name, @Nullable Integer amount) {
    amount = amount != null ? amount : 1;
    Item item = Item.getByNameOrId(name);
    return new ItemStack(item, amount);
  }
}
