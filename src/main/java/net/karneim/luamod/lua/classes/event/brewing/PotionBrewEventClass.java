package net.karneim.luamod.lua.classes.event.brewing;

import static net.karneim.luamod.lua.util.LuaPreconditions.checkTypeDelegatingTable;

import java.util.function.Function;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.karneim.luamod.lua.wrapper.FixedSizeCollection;
import net.karneim.luamod.lua.wrapper.FixedSizeCollectionWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.sandius.rembulan.Table;

@LuaModule("PotionBrewEvent")
public class PotionBrewEventClass extends DelegatingLuaClass<PotionBrewEvent> {
  public PotionBrewEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends PotionBrewEvent> b,
      PotionBrewEvent delegate) {
    DelegatingTable<PotionBrewEvent> wrappedItems = wrapItems(delegate);
    b.addReadOnly("items", () -> wrappedItems);
  }

  private DelegatingTable<PotionBrewEvent> wrapItems(PotionBrewEvent delegate) {
    Function<ItemStack, Object> toLua = j -> repo.wrap(j);
    Function<Object, ItemStack> toJava = l -> checkTypeDelegatingTable(l, ItemStack.class);
    FixedSizeCollectionWrapper<ItemStack, Object, PotionBrewEvent> itemsWrapper =
        new FixedSizeCollectionWrapper<>(Object.class, toLua, toJava);
    FixedSizeCollection<ItemStack, PotionBrewEvent> items =
        new FixedSizeCollection<ItemStack, PotionBrewEvent>() {
          @Override
          public void setAt(int i, ItemStack element) {
            delegate.setItem(i, element);
          }

          @Override
          public int getLength() {
            return delegate.getLength();
          }

          @Override
          public PotionBrewEvent getDelegate() {
            return delegate;
          }

          @Override
          public ItemStack getAt(int i) {
            return delegate.getItem(i);
          }
        };
    return itemsWrapper.createLuaObject(items);
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
