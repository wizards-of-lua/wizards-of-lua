package net.karneim.luamod.lua.classes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.karneim.luamod.lua.LuaTypeConverter;
import net.karneim.luamod.lua.patched.DelegatingTable;
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
  protected void addProperties(DelegatingTable.Builder b, IBlockState delegate) {
    b.add("name", repo.wrap(delegate.getBlock().getRegistryName().getResourcePath()));
    Map<Object, Object> props = new HashMap<>();
    Collection<IProperty<?>> names = delegate.getPropertyNames();
    for (IProperty<?> name : names) {
      Object value = delegate.getValue(name);
      Object luaValue = LuaTypeConverter.luaValueOf(value);
      props.put(name.getName(), luaValue);
    }
    b.add("properties", DelegatingTable.of(props));
    b.add("material", repo.wrap(delegate.getMaterial()));
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
