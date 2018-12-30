package net.wizardsoflua.lua.classes.nbt;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaTypes;
import net.wizardsoflua.lua.BadArgumentException;
import net.wizardsoflua.lua.nbt.accessor.NbtAccessor;

public class IntArrayNbtTable extends IndexedNbtTable<NBTTagIntArray> {
  public IntArrayNbtTable(NbtAccessor<NBTTagIntArray> accessor, Table metatable,
      Injector injector) {
    super(accessor, metatable, injector);
  }

  @Override
  protected int getSize(NBTTagIntArray parent) {
    int[] array = parent.getIntArray();
    return array.length;
  }

  @Override
  protected NBTTagInt getChild(NBTTagIntArray parent, int javaIndex) {
    int[] array = parent.getIntArray();
    return new NBTTagInt(array[javaIndex]);
  }

  @Override
  protected void setChild(NBTTagIntArray parent, int javaIndex, @Nullable NBTBase value) {
    checkJavaIndex(javaIndex, getSize(parent));

    if (value == null)
      throw new BadArgumentException(LuaTypes.NUMBER, LuaTypes.NIL)
          .setFunctionOrPropertyName(getNbtPath(javaIndex + 1));
    if (!(value instanceof NBTPrimitive))
      throw new BadArgumentException(LuaTypes.NUMBER, LuaTypes.TABLE)
          .setFunctionOrPropertyName(getNbtPath(javaIndex + 1));

    int intValue = ((NBTPrimitive) value).getInt();
    int[] array = parent.getIntArray();
    array[javaIndex] = intValue;
  }
}
