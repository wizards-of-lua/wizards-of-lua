package net.karneim.luamod.lua.classes.block.state;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.karneim.luamod.lua.LuaTypeConverter;
import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.patched.PatchedImmutableTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.sandius.rembulan.Table;

@LuaModule("BlockState")
public class BlockStateClass extends DelegatingLuaClass<IBlockState> {
  public BlockStateClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(
      net.karneim.luamod.lua.util.table.DelegatingTable.Builder<? extends IBlockState> b,
      IBlockState delegate) {
    b.addReadOnly("name", () -> repo.wrap(delegate.getBlock().getRegistryName().getResourcePath()));
    Map<Object, Object> props = new HashMap<>();
    Collection<IProperty<?>> names = delegate.getPropertyNames();
    for (IProperty<?> name : names) {
      Object value = delegate.getValue(name);
      Object luaValue = LuaTypeConverter.luaValueOf(value);
      props.put(name.getName(), luaValue);
    }
    b.addReadOnly("properties", () -> PatchedImmutableTable.of(props));
    b.addReadOnly("material", () -> repo.wrap(delegate.getMaterial()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
