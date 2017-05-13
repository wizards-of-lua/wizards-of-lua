package net.karneim.luamod.lua.util.wrapper;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.MapMaker;

import net.karneim.luamod.lua.classes.LuaClass;
import net.karneim.luamod.lua.classes.LuaTypesRepo;

public abstract class CachingLuaClass<J, L> extends LuaClass implements LuaWrapper<J, L> {
  private final Map<J, SoftReference<L>> luaObjects = new MapMaker().weakKeys().makeMap();
  private final Map<L, WeakReference<J>> javaObjects = new MapMaker().weakKeys().makeMap();

  public CachingLuaClass(LuaTypesRepo repo) {
    super(repo);
  }

  public @Nullable L getLuaObjectNullable(@Nullable J javaObject) {
    if (javaObject == null) {
      return null;
    }
    return getLuaObject(javaObject);
  }

  public L getLuaObject(J javaObject) {
    checkNotNull(javaObject, "javaObject == null!");
    SoftReference<L> ref = luaObjects.get(javaObject);
    L result = ref == null ? null : ref.get();
    if (result == null) {
      result = createLuaObject(javaObject);
      luaObjects.put(javaObject, new SoftReference<>(result));
      javaObjects.put(result, new WeakReference<>(javaObject));
    }
    return result;
  }

  public @Nullable J getJavaObject(@Nullable L luaObject) {
    WeakReference<J> ref = javaObjects.get(luaObject);
    J result = ref == null ? null : ref.get();
    return result;
  }

  public void clearCache() {
    luaObjects.clear();
    javaObjects.clear();
  }
}
