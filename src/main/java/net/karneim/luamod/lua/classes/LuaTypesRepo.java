package net.karneim.luamod.lua.classes;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.entity.ArmorClass;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.CachingLuaClass;
import net.karneim.luamod.lua.wrapper.ModifiableArrayWrapper;
import net.karneim.luamod.lua.wrapper.UnmodifiableIterableWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;

public class LuaTypesRepo {
  private final Map<String, LuaClass> types = new HashMap<>();
  private final Map<Class<?>, CachingLuaClass<?, ?>> cachingLuaClasses = new HashMap<>();
  private final Table env;

  public LuaTypesRepo(Table env) {
    this.env = checkNotNull(env);
  }

  public <T extends LuaClass> T get(Class<T> cls) {
    String moduleName = LuaClass.getModuleNameOf(cls);
    LuaClass luaClass = get(moduleName);
    return cls.cast(luaClass);
  }

  public LuaClass get(String name) {
    LuaClass result = types.get(name);
    return checkNotNull(result, "Type '%s' is not registered", name);
  }

  private <T> CachingLuaClass<T, ?> getCachingLuaClass(Class<T> javaClass) {
    @SuppressWarnings("unchecked")
    CachingLuaClass<T, ?> result = (CachingLuaClass<T, ?>) cachingLuaClasses.get(javaClass);
    return result;
  }

  public Table getEnv() {
    return env;
  }

  public boolean isRegistered(String name) {
    return types.containsKey(name);
  }

  public <C extends LuaClass> void register(C luaClass) {
    String name = luaClass.getModuleName();
    if (isRegistered(name)) {
      throw new IllegalArgumentException(String.format("Type %s is already definded!", luaClass));
    }
    types.put(name, luaClass);
    if (luaClass instanceof CachingLuaClass<?, ?>) {
      CachingLuaClass<?, ?> cachingLuaClass = (CachingLuaClass<?, ?>) luaClass;
      registerCachingLuaClass(cachingLuaClass);
    }
  }

  private <T> void registerCachingLuaClass(CachingLuaClass<T, ?> cachingLuaClass) {
    Class<T> javaClass = cachingLuaClass.getJavaClass();
    cachingLuaClasses.put(javaClass, cachingLuaClass);
  }

  public boolean wrap(boolean javaObject) {
    return javaObject;
  }

  public long wrap(byte javaObject) {
    return javaObject;
  }

  public @Nullable ByteString wrap(@Nullable ByteString javaObject) {
    return javaObject;
  }

  public double wrap(double javaObject) {
    return javaObject;
  }

  public @Nullable ByteString wrap(@Nullable Enum<?> javaObject) {
    return javaObject == null ? null : ByteString.of(javaObject.name());
  }

  public double wrap(float javaObject) {
    return javaObject;
  }

  public long wrap(int javaObject) {
    return javaObject;
  }

  public ByteString wrap(@Nullable ITextComponent javaObject) {
    return javaObject == null ? null : wrap(javaObject.getFormattedText());
  }

  public long wrap(long javaObject) {
    return javaObject;
  }

  public long wrap(short javaObject) {
    return javaObject;
  }

  public @Nullable ByteString wrap(@Nullable String javaObject) {
    return javaObject == null ? null : ByteString.of(javaObject);
  }

  public @Nullable <T> Object wrap(@Nullable T javaObject) {
    if (javaObject == null) {
      return null;
    }
    @SuppressWarnings("unchecked")
    Class<? extends T> javaClass = (Class<? extends T>) javaObject.getClass();
    return wrap(javaObject, javaClass);
  }

  private <T, A extends T> Object wrap(T javaObject, Class<A> javaClass) {
    A actualJavaObject = javaClass.cast(javaObject);
    CachingLuaClass<A, ?> luaClass = getCachingLuaClass(javaClass);
    checkArgument(luaClass != null, "No CachingLuaClass is registered for %s", javaClass);
    return luaClass.getLuaObject(actualJavaObject);
  }

  public @Nullable DelegatingTable<? extends Iterable<ItemStack>> wrapArmor(
      @Nullable Iterable<ItemStack> javaObject) {
    return get(ArmorClass.class).getLuaObject(javaObject);
  }

  public @Nullable DelegatingTable<? extends Iterable<String>> wrapStrings(
      @Nullable Iterable<String> javaObject) {
    if (javaObject == null) {
      return null;
    }
    UnmodifiableIterableWrapper<String, ByteString> wrapper =
        new UnmodifiableIterableWrapper<>(j -> ByteString.of(j));
    return wrapper.createLuaObject(javaObject);
  }

  public @Nullable DelegatingTable<? extends String[]> wrapStrings(@Nullable String[] javaObject) {
    if (javaObject == null) {
      return null;
    }
    ModifiableArrayWrapper<String, ByteString> wrapper =
        new ModifiableArrayWrapper<>(ByteString.class, j -> ByteString.of(j), l -> l.decode());
    return wrapper.createLuaObject(javaObject);
  }
}
