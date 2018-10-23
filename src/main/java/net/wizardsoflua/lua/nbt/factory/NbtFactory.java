package net.wizardsoflua.lua.nbt.factory;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ServiceLoader;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.nbt.NBTBase;

public interface NbtFactory<NBT extends NBTBase, D> {
  static @Nullable ImmutableMap<Class<? extends NBTBase>, NbtFactory<?, ?>> FACTORIES =
      loadFactories();

  static ImmutableMap<Class<? extends NBTBase>, NbtFactory<?, ?>> loadFactories() {
    Class<NbtFactory<?, ?>> cls = getClassWithWildcards();
    ServiceLoader<NbtFactory<?, ?>> load = ServiceLoader.load(cls, cls.getClassLoader());
    return Maps.uniqueIndex(load, f -> f.getNbtClass());
  }

  static <NBT> NbtFactory<NBTBase, ?> get(Class<NBT> nbtClass) {
    @SuppressWarnings("unchecked")
    NbtFactory<NBTBase, ?> result = (NbtFactory<NBTBase, ?>) FACTORIES.get(nbtClass);
    checkArgument(result != null, "Unknown NBT type: " + nbtClass);
    return result;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  static Class<NbtFactory<?, ?>> getClassWithWildcards() {
    return (Class) NbtFactory.class;
  }

  Class<NBT> getNbtClass();

  Class<D> getDataClass();

  default @Nullable NBT tryCreate(Object data) {
    Class<D> dataClass = getDataClass();
    if (dataClass.isInstance(data)) {
      return create(dataClass.cast(data));
    } else {
      return null;
    }
  }

  NBT create(D data);
}
