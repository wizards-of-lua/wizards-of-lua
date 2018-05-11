package net.wizardsoflua.lua.classes.block;

import java.util.Collection;

import com.google.auto.service.AutoService;

import net.minecraft.block.properties.IProperty;
import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.spell.spi.JavaToLuaConverter;
import net.wizardsoflua.lua.extension.util.TypeTokenJavaToLuaConverter;
import net.wizardsoflua.lua.table.PatchedImmutableTable;

@AutoService(JavaToLuaConverter.class)
public class BlockStateConverter extends TypeTokenJavaToLuaConverter<WolBlockState> {
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
