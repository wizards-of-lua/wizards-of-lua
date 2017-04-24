package net.karneim.luamod.lua.classes;

import java.util.Iterator;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.item.ItemStack;
import net.sandius.rembulan.Table;

@LuaModule("Armor")
public class ArmorClass extends DelegatingLuaClass<Iterable<ItemStack>> {
  public ArmorClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends Iterable<ItemStack>> b,
      Iterable<ItemStack> delegate) {
    Iterator<ItemStack> it = delegate.iterator();
    final ItemStack feet = it.next();
    b.add("feet", () -> repo.wrap(feet), null);
    final ItemStack legs = it.next();
    b.add("legs", () -> repo.wrap(legs), null);
    final ItemStack chest = it.next();
    b.add("chest", () -> repo.wrap(chest), null);
    final ItemStack head = it.next();
    b.add("head", () -> repo.wrap(head), null);
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
