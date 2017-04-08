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
  protected void addProperties(DelegatingTable.Builder b) {
    Iterator<ItemStack> it = delegate.iterator();
    final ItemStack feet = it.next();
    b.add("feet", () -> getRepo().get(ItemStackClass.class).newInstance(feet).getLuaObject(), null);
    final ItemStack legs = it.next();
    b.add("legs", () -> getRepo().get(ItemStackClass.class).newInstance(legs).getLuaObject(), null);
    final ItemStack chest = it.next();
    b.add("chest", () -> getRepo().get(ItemStackClass.class).newInstance(chest).getLuaObject(),
        null);
    final ItemStack head = it.next();
    b.add("head", () -> getRepo().get(ItemStackClass.class).newInstance(head).getLuaObject(), null);
  }

}
