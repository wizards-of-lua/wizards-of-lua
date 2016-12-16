package net.karneim.luamod.lua.wrapper;

import java.util.Iterator;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.sandius.rembulan.impl.ImmutableTable;

class ArmorWrapper extends StructuredLuaWrapper<Iterable<ItemStack>> {
  public ArmorWrapper(@Nullable Iterable<ItemStack> delegate) {
    super(delegate);
  }

  @Override
  protected void addProperties(ImmutableTable.Builder builder) {
    super.addProperties(builder);
    Iterator<ItemStack> it = delegate.iterator();
    builder.add("feet", new ItemStackWrapper(it.next()).getLuaObject());
    builder.add("legs", new ItemStackWrapper(it.next()).getLuaObject());
    builder.add("chest", new ItemStackWrapper(it.next()).getLuaObject());
    builder.add("head", new ItemStackWrapper(it.next()).getLuaObject());
  }

}
