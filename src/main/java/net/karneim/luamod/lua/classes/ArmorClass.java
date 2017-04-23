package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.ArmorInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraft.item.ItemStack;

@LuaClass("Armor")
public class ArmorClass extends AbstractLuaType {
  public ArmorInstance newInstance(Iterable<ItemStack> delegate) {
    return new ArmorInstance(getRepo(), delegate,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  @Override
  protected void addFunctions() {}
}
