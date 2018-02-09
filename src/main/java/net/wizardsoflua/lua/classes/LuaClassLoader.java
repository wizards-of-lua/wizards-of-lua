package net.wizardsoflua.lua.classes;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.common.reflect.TypeToken;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.module.types.Types;

public class LuaClassLoader {
  private static final String CLASSES_PACKAGE = "net.wizardsoflua.lua.classes";

  private static final ImmutableList<Class<? extends JavaLuaClass<?, ?>>> JAVA_LUA_CLASS_CLASSES =
      findJavaLuaClassClasses();
  private static final ImmutableMap<Class<?>, Class<? extends JavaLuaClass<?, ?>>> JAVA_LUA_CLASS_CLASS_BY_JAVA_CLASS =
      Maps.uniqueIndex(JAVA_LUA_CLASS_CLASSES, LuaClassLoader::getJavaClass);

  private static ImmutableList<Class<? extends JavaLuaClass<?, ?>>> findJavaLuaClassClasses() {
    try {
      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      ClassPath classpath = ClassPath.from(classloader);
      ImmutableSet<ClassInfo> classInfos = classpath.getTopLevelClassesRecursive(CLASSES_PACKAGE);
      Iterable<Class<?>> classes = transform(classInfos, ClassInfo::load);
      Iterable<Class<?>> x = filter(classes, cls -> cls.isAnnotationPresent(DeclareLuaClass.class));
      Iterable<Class<?>> luaClasses = filter(x, LuaClass.class::isAssignableFrom);
      @SuppressWarnings("unchecked")
      Iterable<Class<? extends JavaLuaClass<?, ?>>> result =
          (Iterable<Class<? extends JavaLuaClass<?, ?>>>) (Iterable<?>) luaClasses;
      return ImmutableList.copyOf(result);
    } catch (IOException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }

  private static Class<?> getJavaClass(Class<? extends JavaLuaClass<?, ?>> luaClass) {
    // TODO Adrodoc55 07.02.2018: Zusammenziehen mit LuaClass.getJavaClass() ?
    @SuppressWarnings("serial")
    TypeToken<JavaLuaClass<?, ?>> token = new TypeToken<JavaLuaClass<?, ?>>(luaClass) {};
    // FIXME Adrodoc55 07.02.2018: cls.getGenericSuperclass()? nicht eher nur cls
    TypeToken<?> resolveType = token.resolveType(luaClass.getGenericSuperclass());
    Type type = resolveType.getType();
    ParameterizedType ptype = (ParameterizedType) type;
    Type arg1 = ptype.getActualTypeArguments()[0];
    Class<?> typeArg = (Class<?>) arg1;
    return typeArg;
  }

  public static boolean isSupported(Class<?> javaClass) {
    return JAVA_LUA_CLASS_CLASS_BY_JAVA_CLASS.containsKey(javaClass);
  }

  public static String getLuaClassNameOf(Class<?> javaClass) {
    Class<? extends JavaLuaClass<?, ?>> luaClassClass =
        JAVA_LUA_CLASS_CLASS_BY_JAVA_CLASS.get(javaClass);
    return JavaLuaClass.getNameOf(luaClassClass);
  }

  private final Table env;
  private final Map<Class<?>, JavaLuaClass<?, ?>> luaClassByJavaClass = new HashMap<>();
  private final Map<Class<? extends LuaClass>, LuaClass> luaClassByType = new HashMap<>();
  private final Map<Table, LuaClass> luaClassByMetaTable = new HashMap<>();
  private final Types types;
  private final Converters converters;

  public LuaClassLoader(Table env) {
    this.env = requireNonNull(env, "env == null!");
    types = new Types(this);
    converters = new Converters(this);
  }

  public Table getEnv() {
    return env;
  }

  public Types getTypes() {
    return types;
  }

  public Converters getConverters() {
    return converters;
  }

  public void loadStandardClasses() {
    load(ObjectClass.class);
    for (Class<? extends JavaLuaClass<?, ?>> luaClassClass : JAVA_LUA_CLASS_CLASSES) {
      load(luaClassClass);
    }
  }

  public void load(Class<? extends LuaClass> luaClassClass) {
    try {
      load(luaClassClass.newInstance());
    } catch (InstantiationException | IllegalAccessException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public void load(LuaClass luaClass) {
    if (luaClassByType.containsKey(luaClass.getClass())) {
      return; // LuaClass is already loaded
    }
    luaClass.init(this);
    luaClassByType.put(luaClass.getClass(), luaClass);
    luaClassByMetaTable.put(luaClass.getMetaTable(), luaClass);
    if (luaClass instanceof JavaLuaClass) {
      JavaLuaClass<?, ?> javaLuaClass = (JavaLuaClass<?, ?>) luaClass;
      luaClassByJavaClass.put(javaLuaClass.getJavaClass(), javaLuaClass);
    }
    get_G().rawset(luaClass.getName(), luaClass.getMetaTable());
  }

  public Table get_G() {
    Table _G = (Table) env.rawget("_G");
    return requireNonNull(_G, "_G == null!");
  }

  /**
   * Returns the {@link LuaClass} instance of the specified type, loading it if neccessary.
   *
   * @param luaClassClass
   * @return the {@link LuaClass} instance
   */
  public <LC extends LuaClass> LC getLuaClassOfType(Class<LC> luaClassClass) {
    requireNonNull(luaClassClass, "luaClassClass == null!");
    LuaClass luaClass = luaClassByType.get(luaClassClass);
    if (luaClass == null) {
      load(luaClassClass);
      luaClass = luaClassByType.get(luaClassClass);
      assert luaClass != null;
    }
    return luaClassClass.cast(luaClass);
  }

  public LuaClass getLuaClassForMetaTable(Table luaClassMetaTable) throws IllegalArgumentException {
    requireNonNull(luaClassMetaTable, "luaClassMetaTable == null!");
    LuaClass luaClass = luaClassByMetaTable.get(luaClassMetaTable);
    checkArgument(luaClass != null,
        "The table '%s' does not represent a LuaClass loaded by this LuaClassLoader",
        luaClassMetaTable);
    return luaClass;
  }

  public @Nullable <J> JavaLuaClass<J, ?> getLuaClassForJavaClass(Class<J> javaClass) {
    requireNonNull(javaClass, "javaClass == null!");
    @SuppressWarnings("unchecked")
    JavaLuaClass<J, ?> luaClass = (JavaLuaClass<J, ?>) luaClassByJavaClass.get(javaClass);
    return luaClass;
  }

  /**
   * Returns the corresponding {@link JavaLuaClass} for {@code javaClass} checking all superclasses
   * recursively.
   *
   * @param javaClass
   * @return the corresponding {@link JavaLuaClass}
   */
  public @Nullable <J> JavaLuaClass<? super J, ?> getLuaClassForJavaClassRecursively(
      @Nullable Class<J> javaClass) {
    if (javaClass == null) {
      return null;
    }
    JavaLuaClass<J, ?> luaClass = getLuaClassForJavaClass(javaClass);
    if (luaClass != null) {
      return luaClass;
    }
    Class<? super J> superClass = javaClass.getSuperclass();
    return getLuaClassForJavaClassRecursively(superClass);
  }
}
