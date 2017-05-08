package net.karneim.luamod.lua.classes.nbt;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Set;

import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.nbt.NBTTagUtil;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.karneim.luamod.lua.wrapper.nbt.NbtAccessor;
import net.karneim.luamod.lua.wrapper.nbt.NbtCompoundChildAccessor;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.sandius.rembulan.Table;

public class NbtCompoundClass extends DelegatingLuaClass<NbtAccessor<NBTTagCompound>> {
  public NbtCompoundClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends NbtAccessor<NBTTagCompound>> b,
      NbtAccessor<NBTTagCompound> delegate) {
    Set<String> keySet = delegate.getTag().getKeySet();
    for (String key : keySet) {
      NbtCompoundChildAccessor nbtChild = new NbtCompoundChildAccessor(delegate, key);
      b.add(key, () -> repo.wrapNbt(nbtChild), o -> nbtChild.setTag(checkTypeNbt(o)));
    }
  }

  private NBTBase checkTypeNbt(Object o) {
    checkArgument(o != null, "Expected NBT but got nil");
    NBTBase value = NBTTagUtil.toTag(o);
    checkArgument(value != null, "Expected NBT but got " + o.getClass().getSimpleName());
    return value;
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
