package net.wizardsoflua.lua.classes.block;

import java.util.Collection;

import com.google.auto.service.AutoService;

import net.minecraft.block.properties.IProperty;
import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.table.PatchedImmutableTable;

@AutoService(LuaConverter.class)
public class BlockStateConverter implements LuaConverter<WolBlockState, Table> {
  @Override
  public String getName() {
    return "BlockState";
  }

  @Override
  public Class<WolBlockState> getJavaClass() {
    return WolBlockState.class;
  }

  @Override
  public Class<Table> getLuaClass() {
    return Table.class;
  }

  @Override
  public WolBlockState getJavaInstance(Table luaInstance) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Table getLuaInstance(WolBlockState blockState) {
    PatchedImmutableTable.Builder b = new PatchedImmutableTable.Builder();
    Collection<IProperty<?>> names = blockState.getDelegate().getPropertyKeys();
    for (IProperty<?> name : names) {
      Comparable<?> value = blockState.getDelegate().getValue(name);
      Object luaValue = BlockPropertyConverter.toLua(value);
      b.add(name.getName(), luaValue);
    }
    return b.build();
  }

}
