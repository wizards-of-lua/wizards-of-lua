package net.wizardsoflua.lua.classes;

import javax.annotation.Nullable;

import com.google.common.base.Strings;
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

  public static @Nullable String getSuperClassNameOf(
      Class<? extends JavaLuaClass<?, ?>> luaClassClass) {
    DeclareLuaClass annotation = luaClassClass.getAnnotation(DeclareLuaClass.class);
    return Strings.emptyToNull(annotation.superclassname());
  }

  public @Nullable String getSuperClassName() {
    return getSuperClassNameOf(getClassWithGenerics());
  }

  public Class<? extends JavaLuaClass<?, ?>> getClassWithGenerics() {
    @SuppressWarnings("unchecked")
    Class<? extends JavaLuaClass<?, ?>> luaClassClass =
        (Class<? extends JavaLuaClass<?, ?>>) getClass();
    return luaClassClass;
  }

  @Override
  public @Nullable LuaClass getSuperClass() {
    String superClassName = getSuperClassName();
    if (superClassName == null) {
      return null;
    } else {
      return getClassLoader().getLuaClassForName(superClassName);
    }
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
