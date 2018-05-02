package net.wizardsoflua.lua.classes;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

import com.google.common.reflect.TypeToken;

import net.sandius.rembulan.Table;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.lua.classes.spi.DeclaredLuaClass;

/**
 * A {@link LuaClass} that represents a Java{@link Class} in Lua and can convert java instances to
 * Lua and vice-versa.
 *
 * @author Adrodoc55
 */
public abstract class JavaLuaClass<J, L extends Table> extends LuaClass implements DeclaredLuaClass {
  public static String getNameOf(Class<? extends JavaLuaClass<?, ?>> luaClassClass) {
    DeclareLuaClass annotation = luaClassClass.getAnnotation(DeclareLuaClass.class);
    return annotation.name();
  }

  private @Nullable String name;

  @Override
  public String getName() {
    if (name == null) {
      name = getNameOf(getClassWithGenerics());
    }
    return name;
  }

  private static Class<? extends LuaClass> getSuperClassClassOf(
      Class<? extends JavaLuaClass<?, ?>> luaClassClass) {
    DeclareLuaClass annotation = luaClassClass.getAnnotation(DeclareLuaClass.class);
    return annotation.superClass();
  }

  private Class<? extends LuaClass> getSuperClassClass() {
    return getSuperClassClassOf(getClassWithGenerics());
  }

  @Override
  public @Nullable LuaClass getSuperClass() {
    Class<? extends LuaClass> superClassClass = getSuperClassClass();
    return getClassLoader().getLuaClassOfType(superClassClass);
  }

  public static <C extends JavaLuaClass<J, L>, J, L extends Table> Class<J> getJavaClassOf(
      Class<C> luaClass) {
    TypeToken<? extends JavaLuaClass<?, ?>> token = TypeToken.of(luaClass);
    Type superType = token.getSupertype(JavaLuaClass.class).getType();
    ParameterizedType parameterizedSuperType = (ParameterizedType) superType;
    Type arg0 = parameterizedSuperType.getActualTypeArguments()[0];
    @SuppressWarnings("unchecked")
    Class<J> typeArg0 = (Class<J>) arg0;
    return typeArg0;
  }

  public Class<J> getJavaClass() {
    return getJavaClassOf(getClassWithGenerics());
  }

  public Class<? extends JavaLuaClass<J, L>> getClassWithGenerics() {
    @SuppressWarnings("unchecked")
    Class<? extends JavaLuaClass<J, L>> luaClassClass =
        (Class<? extends JavaLuaClass<J, L>>) getClass();
    return luaClassClass;
  }

  public LuaConverters getConverters() {
    return getClassLoader().getConverters();
  }

  public L getLuaInstance(J javaObject) {
    return toLua(javaObject);
  }

  public J getJavaInstance(Table luaObject) throws ClassCastException {
    return toJava(luaObject);
  }

  protected abstract L toLua(J javaObject);

  protected abstract J toJava(Table luaObject) throws ClassCastException;
}
