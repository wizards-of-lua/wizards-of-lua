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
    b.addReadOnly("feet", () -> repo.wrap(feet));
    final ItemStack legs = it.next();
    b.addReadOnly("legs", () -> repo.wrap(legs));
    final ItemStack chest = it.next();
    b.addReadOnly("chest", () -> repo.wrap(chest));
    final ItemStack head = it.next();
    b.addReadOnly("head", () -> repo.wrap(head));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
