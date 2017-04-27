package net.karneim.luamod.lua.classes.event.brewing;

import static net.karneim.luamod.lua.util.LuaPreconditions.checkTypeDelegatingTable;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
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
    Builder<PotionBrewEvent> itemsBuilder = DelegatingTable.builder(delegate);
    for (int i = 0; i < delegate.getLength(); i++) {
      final int luaIndex = i + 1;

      Supplier<DelegatingTable<? extends ItemStack>> get =
          () -> repo.wrap(delegate.getItem(luaIndex - 1));

      Consumer<Object> set =
          l -> delegate.setItem(luaIndex - 1, checkTypeDelegatingTable(l, ItemStack.class));

      itemsBuilder.add(luaIndex, get, set);
    }
    DelegatingTable<PotionBrewEvent> items = itemsBuilder.build();
    b.addReadOnly("items", () -> items);
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
