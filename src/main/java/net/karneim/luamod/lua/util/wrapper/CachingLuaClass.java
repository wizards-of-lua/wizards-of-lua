package net.karneim.luamod.lua.util.wrapper;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import com.google.common.reflect.TypeToken;

import net.karneim.luamod.lua.classes.LuaClass;
import net.karneim.luamod.lua.classes.LuaTypesRepo;

public abstract class CachingLuaClass<J, L> extends LuaClass implements LuaWrapper<J, L> {
  private final Map<J, SoftReference<L>> luaObjects = new WeakHashMap<>();
  private final Map<L, WeakReference<J>> javaObjects = new WeakHashMap<>();
  private @Nullable Class<J> javaClass;

  public CachingLuaClass(LuaTypesRepo repo) {
    super(repo);
  }

  public Class<J> getJavaClass() {
    if (javaClass == null) {
      @SuppressWarnings("serial")
      TypeToken<J> token = new TypeToken<J>(getClass()) {};
      @SuppressWarnings("unchecked")
      Class<J> rawType = (Class<J>) token.getRawType();
      javaClass = rawType;
    }
    return javaClass;
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
