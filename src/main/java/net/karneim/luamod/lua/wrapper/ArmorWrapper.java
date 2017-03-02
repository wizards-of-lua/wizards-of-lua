package net.karneim.luamod.lua.wrapper;

import java.util.Iterator;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.minecraft.item.ItemStack;
import net.sandius.rembulan.Table;


class ArmorWrapper extends DelegatingTableWrapper<Iterable<ItemStack>> {
  public ArmorWrapper(Table env, @Nullable Iterable<ItemStack> delegate) {
    super(env, delegate);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    Iterator<ItemStack> it = delegate.iterator();
    builder.addNullable("feet", new ItemStackWrapper(env, it.next()).getLuaObject());
    builder.addNullable("legs", new ItemStackWrapper(env, it.next()).getLuaObject());
    builder.addNullable("chest", new ItemStackWrapper(env, it.next()).getLuaObject());
    builder.addNullable("head", new ItemStackWrapper(env, it.next()).getLuaObject());
  }

}
