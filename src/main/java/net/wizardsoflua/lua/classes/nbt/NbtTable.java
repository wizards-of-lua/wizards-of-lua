package net.wizardsoflua.lua.classes.nbt;

import static java.util.Objects.requireNonNull;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import javax.inject.Inject;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.lua.nbt.NbtConverter;
import net.wizardsoflua.lua.nbt.accessor.NbtAccessor;
import net.wizardsoflua.lua.nbt.accessor.NbtChildAccessor;

public abstract class NbtTable<NBT extends NBTBase> extends Table {
  @Resource
  protected LuaConverters converters;
  @Inject
  protected NbtConverter nbtConverters;
  @Inject
  private CompoundNbtClass compoundNbtClass;
  @Inject
  private ListNbtClass listNbtClass;

  private NbtAccessor<NBT> accessor;
  private @Nullable Map<Object, Object> successors;

  public NbtTable(NbtAccessor<NBT> accessor, Table metatable, Injector injector) {
    this.accessor = requireNonNull(accessor, "accessor == null!");
    setMetatable(metatable);
    injector.injectMembers(this);
  }

  public NBT getNbt() {
    return accessor.getNbt();
  }

  @Override
  public Object rawget(Object key) {
    NBT nbt = getNbt();
    NBTBase value = getChild(nbt, key);
    if (value instanceof NBTTagCompound) {
      NbtChildAccessor<NBTTagCompound, NBT> child = getChildAccessor(NBTTagCompound.class, key);
      return compoundNbtClass.toLuaInstance(child);
    } else if (value instanceof NBTTagList) {
      NbtChildAccessor<NBTTagList, NBT> child = getChildAccessor(NBTTagList.class, key);
      return listNbtClass.toLuaInstance(child);
    } else {
      return converters.toLuaNullable(value);
    }
  }

  private <C extends NBTBase> NbtChildAccessor<C, NBT> getChildAccessor(Class<C> expectedChildType,
      Object key) {
    return new NbtChildAccessor<>(expectedChildType, accessor, p -> getChild(p, key));
  }

  protected abstract @Nullable NBTBase getChild(NBT parent, Object key);

  @Override
  public void rawset(Object key, Object value) {
    accessor.modifyNbt(nbt -> {
      NBTBase oldValue = getChild(nbt, key);
      NBTBase newValue = nbtConverters.toNbt(value, oldValue);
      setChild(nbt, key, newValue);
    });
  }

  protected abstract void setChild(NBT parent, Object key, @Nullable NBTBase child);

  @Override
  public Object initialKey() {
    successors = new HashMap<>();
    Iterable<?> keys = getKeys();
    Iterator<?> it = keys.iterator();
    if (it.hasNext()) {
      Object initialKey = it.next();
      Object predecessor = initialKey;
      while (it.hasNext()) {
        Object successor = it.next();
        successors.put(predecessor, successor);
        predecessor = successor;
      }
      return initialKey;
    } else {
      return null;
    }
  }

  protected abstract Iterable<?> getKeys();

  @Override
  public Object successorKeyOf(Object key) {
    if (successors == null) {
      initialKey();
    }
    return successors.get(key);
  }

  @Override
  protected void setMode(boolean weakKeys, boolean weakValues) {
    // no-op
  }
}
