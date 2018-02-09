package net.wizardsoflua.lua.classes;

import javax.annotation.Nullable;

import com.google.common.reflect.TypeToken;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;

/**
 * A {@link LuaClass} that represents a Java{@link Class} in Lua and can convert java instances to
 * Lua and vice-versa.
 *
 * @author Adrodoc55
 */
public abstract class JavaLuaClass<J, L extends Table> extends LuaClass {
  private @Nullable Class<J> javaClass;

  public static String getNameOf(Class<? extends JavaLuaClass<?, ?>> luaClassClass) {
    DeclareLuaClass annotation = luaClassClass.getAnnotation(DeclareLuaClass.class);
    return annotation.name();
  }

  @Override
  public String getName() {
    return getNameOf(getClassWithGenerics());
  }

  public static Class<? extends LuaClass> getSuperClassClassOf(
      Class<? extends JavaLuaClass<?, ?>> luaClassClass) {
    DeclareLuaClass annotation = luaClassClass.getAnnotation(DeclareLuaClass.class);
    return annotation.superClass();
  }

  public Class<? extends LuaClass> getSuperClassClass() {
    return getSuperClassClassOf(getClassWithGenerics());
  }

  @Override
  public @Nullable LuaClass getSuperClass() {
    Class<? extends LuaClass> superClassClass = getSuperClassClass();
    return getClassLoader().getLuaClassOfType(superClassClass);
  }

  public Class<? extends JavaLuaClass<?, ?>> getClassWithGenerics() {
    @SuppressWarnings("unchecked")
    Class<? extends JavaLuaClass<?, ?>> luaClassClass =
        (Class<? extends JavaLuaClass<?, ?>>) getClass();
    return luaClassClass;
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

  public Converters getConverters() {
    return getClassLoader().getConverters();
  }

  public L getLuaInstance(J javaObj) {
    return toLua(javaObj);
  }

  public J getJavaInstance(Table luaObj) {
    checkAssignable(luaObj);
    return toJava(luaObj);
  }

  protected void checkAssignable(Object luaObj) {
    getConverters().getTypes().checkAssignable(getName(), luaObj);
  }

  protected abstract L toLua(J javaObj);

  protected abstract J toJava(Table luaObj);
}
