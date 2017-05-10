package net.karneim.luamod.lua.classes.inventory;

import static net.karneim.luamod.lua.util.LuaPreconditions.checkTypeDelegatingTable;

import java.util.function.Function;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.karneim.luamod.lua.wrapper.FixedSizeCollection;
import net.karneim.luamod.lua.wrapper.FixedSizeCollectionWrapper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.sandius.rembulan.Table;

@LuaModule("Inventory")
public class InventoryClass extends DelegatingLuaClass<IInventory> {
  public InventoryClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends IInventory> b, IInventory delegate) {
    Function<ItemStack, Object> toLua = j -> repo.wrap(j);
    Function<Object, ItemStack> toJava = l -> checkTypeDelegatingTable(l, ItemStack.class);
    FixedSizeCollectionWrapper<ItemStack, Object, IInventory> wrapper =
        new FixedSizeCollectionWrapper<>(Object.class, toLua, toJava);
    FixedSizeCollection<ItemStack, IInventory> items =
        new FixedSizeCollection<ItemStack, IInventory>() {
          @Override
          public void setAt(int i, ItemStack element) {
            delegate.setInventorySlotContents(i, element);
          }

          @Override
          public int getLength() {
            return delegate.getSizeInventory();
          }

          @Override
          public IInventory getDelegate() {
            return delegate;
          }

          @Override
          public ItemStack getAt(int i) {
            return delegate.getStackInSlot(i);
          }
        };
    wrapper.addProperties(b, items);
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
