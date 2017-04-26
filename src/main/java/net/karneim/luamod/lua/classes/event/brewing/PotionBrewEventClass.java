package net.karneim.luamod.lua.classes.event.brewing;

import java.util.ArrayList;
import java.util.List;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.ImmutableLuaClass;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.sandius.rembulan.Table;

@LuaModule("PotionBrewEvent")
public class PotionBrewEventClass extends ImmutableLuaClass<PotionBrewEvent> {
  public PotionBrewEventClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(PatchedImmutableTable.Builder b, PotionBrewEvent event) {
    List<ItemStack> stacks = new ArrayList<>();
    for (int i = 0; i < event.getLength(); i++) {
      stacks.add(event.getItem(i));
    }
    b.add("items", repo.wrap(stacks));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
