package net.karneim.luamod.lua.wrapper;

import java.util.Iterator;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.ItemStackClass;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.minecraft.item.ItemStack;
import net.sandius.rembulan.Table;

public class ArmorInstance extends DelegatingTableWrapper<Iterable<ItemStack>> {
  public ArmorInstance(LuaTypesRepo repo, @Nullable Iterable<ItemStack> delegate, Table metatable) {
    super(repo, delegate, metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    Iterator<ItemStack> it = delegate.iterator();
    builder.addNullable("feet",
        getRepo().get(ItemStackClass.class).newInstance(it.next()).getLuaObject());
    builder.addNullable("legs",
        getRepo().get(ItemStackClass.class).newInstance(it.next()).getLuaObject());
    builder.addNullable("chest",
        getRepo().get(ItemStackClass.class).newInstance(it.next()).getLuaObject());
    builder.addNullable("head",
        getRepo().get(ItemStackClass.class).newInstance(it.next()).getLuaObject());
  }

}
