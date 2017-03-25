package net.karneim.luamod.lua.wrapper;

import java.util.Iterator;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.ItemStackClass;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.minecraft.item.ItemStack;
import net.sandius.rembulan.Table;

public class ArmorInstance extends DelegatingTableWrapper<Iterable<ItemStack>> {
  public ArmorInstance(Table env, @Nullable Iterable<ItemStack> delegate, Table metatable) {
    super(env, delegate, metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    Iterator<ItemStack> it = delegate.iterator();
    builder.addNullable("feet", ItemStackClass.get().newInstance(env, it.next()).getLuaObject());
    builder.addNullable("legs", ItemStackClass.get().newInstance(env, it.next()).getLuaObject());
    builder.addNullable("chest", ItemStackClass.get().newInstance(env, it.next()).getLuaObject());
    builder.addNullable("head", ItemStackClass.get().newInstance(env, it.next()).getLuaObject());
  }

}
