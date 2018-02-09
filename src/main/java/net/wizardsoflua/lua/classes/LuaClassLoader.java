package net.wizardsoflua.lua.classes;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.common.reflect.TypeToken;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.module.types.Types;

public class LuaClassLoader {
  private static final String CLASSES_PACKAGE = "net.wizardsoflua.lua.classes";

  private static final List<Class<? extends JavaLuaClass<?, ?>>> JAVA_LUA_CLASS_CLASSES =
      sortByClassHierachy(findJavaLuaClassClasses());
  private static final ImmutableMap<Class<?>, Class<? extends JavaLuaClass<?, ?>>> JAVA_LUA_CLASS_CLASS_BY_JAVA_CLASS =
      Maps.uniqueIndex(JAVA_LUA_CLASS_CLASSES, LuaClassLoader::getJavaClass);

  private static Iterable<Class<? extends JavaLuaClass<?, ?>>> findJavaLuaClassClasses() {
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
      return result;
    } catch (IOException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }

  // FIXME Adrodoc55 07.02.2018: sortieren sollte implizit bei load class passieren, indem die
  // superklasse fals nicht vorhanden geladen wird
  private static List<Class<? extends JavaLuaClass<?, ?>>> sortByClassHierachy(
      Iterable<Class<? extends JavaLuaClass<?, ?>>> luaClasses) {
    Multimap<String, Class<? extends JavaLuaClass<?, ?>>> subclasses = ArrayListMultimap.create();
    for (Class<? extends JavaLuaClass<?, ?>> luaClass : luaClasses) {
      DeclareLuaClass anno = luaClass.getAnnotation(DeclareLuaClass.class);
      subclasses.put(anno.superclassname(), luaClass);
    }

    Deque<Class<? extends JavaLuaClass<?, ?>>> todo = new ArrayDeque<>();
    todo.addAll(subclasses.removeAll(""));

    List<Class<? extends JavaLuaClass<?, ?>>> result = new ArrayList<>();
    while (!todo.isEmpty()) {
      Class<? extends JavaLuaClass<?, ?>> cls = todo.pop();
      result.add(cls);
      String name = cls.getAnnotation(DeclareLuaClass.class).name();
      todo.addAll(subclasses.removeAll(name));
    }
    if (!subclasses.isEmpty()) {
      throw new IllegalStateException(
          String.format("Could not create Lua class hierarchy from classes: %s!",
              getClassnames(subclasses.values())));
    }
    return result;
  }

  private static String getClassnames(Iterable<? extends Class<?>> list) {
    return Joiner.on(", ").join(transform(list, Class::getName));
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
  private final ObjectClass objectClass = new ObjectClass();
  private final Set<LuaClass> luaClasses = new HashSet<>();
  private final Map<Class<?>, JavaLuaClass<?, ?>> luaClassByJavaClass = new HashMap<>();
  private final Map<String, LuaClass> luaClassByName = new HashMap<>();
  private final Map<Table, LuaClass> luaClassByMetaTable = new HashMap<>();
  private final Types types;
  private final Converters converters;

  public LuaClassLoader(Table env) {
    this.env = requireNonNull(env, "env == null!");
    types = new Types(this);
    converters = new Converters(this);
  }

  public LuaClass getObjectClass() {
    return objectClass;
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
    load(objectClass);
    for (Class<? extends JavaLuaClass<?, ?>> luaClassClass : JAVA_LUA_CLASS_CLASSES) {
      load(luaClassClass);
    }
  }

  public void load(Class<? extends JavaLuaClass<?, ?>> luaClassClass) {
    try {
      JavaLuaClass<?, ?> luaClass = luaClassClass.newInstance();
      luaClassByJavaClass.put(luaClass.getJavaClass(), luaClass);
      load(luaClass);
    } catch (InstantiationException | IllegalAccessException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public void load(LuaClass luaClass) {
    luaClass.init(this);
    luaClasses.add(luaClass);
    luaClassByName.put(luaClass.getName(), luaClass);
    luaClassByMetaTable.put(luaClass.getMetaTable(), luaClass);
    get_G().rawset(luaClass.getName(), luaClass.getMetaTable());
  }

  public Table get_G() {
    Table _G = (Table) env.rawget("_G");
    return requireNonNull(_G, "_G == null!");
  }

  public @Nullable <J> JavaLuaClass<J, ?> getLuaClassForJavaClass(Class<J> javaClass) {
    requireNonNull(javaClass, "javaClass == null!");
    @SuppressWarnings("unchecked")
    JavaLuaClass<J, ?> luaClass = (JavaLuaClass<J, ?>) luaClassByJavaClass.get(javaClass);
    return luaClass;
  }

  public <J> JavaLuaClass<? super J, ?> getLuaClassForJavaClassRecursively(Class<J> javaClass) {
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

  public LuaClass getLuaClassForName(String luaClassName) throws IllegalArgumentException {
    requireNonNull(luaClassName, "luaClassName == null!");
    LuaClass luaClass = luaClassByName.get(luaClassName);
    checkArgument(luaClass != null, "No LuaClass with name '%s' was loaded by this LuaClassLoader",
        luaClassName);
    return luaClass;
  }

  public LuaClass getLuaClassForMetaTable(Table luaClassMetaTable) throws IllegalArgumentException {
    requireNonNull(luaClassMetaTable, "luaClassMetaTable == null!");
    LuaClass luaClass = luaClassByMetaTable.get(luaClassMetaTable);
    checkArgument(luaClass != null,
        "The table '%s' does not represent a LuaClass loaded by this LuaClassLoader",
        luaClassMetaTable);
    return luaClass;
  }
}
