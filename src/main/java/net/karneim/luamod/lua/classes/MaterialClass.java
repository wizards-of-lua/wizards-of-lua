package net.karneim.luamod.lua.classes;

import net.karneim.luamod.lua.wrapper.MaterialInstance;
import net.karneim.luamod.lua.wrapper.Metatables;
import net.minecraft.block.material.Material;

@LuaClass("Material")
public class MaterialClass extends AbstractLuaType {
  public MaterialInstance newInstance(Material delegate) {
    return new MaterialInstance(getRepo(), delegate,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  @Override
  protected void addFunctions() {}
}
