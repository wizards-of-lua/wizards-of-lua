package net.wizardsoflua.lua.classes;

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
import com.google.common.base.Strings;
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

  private static final List<Class<? extends LuaClass<?, ?>>> LUA_CLASS_CLASSES =
      sortByClassHierachy(findClasses());
  private static final ImmutableMap<Class<?>, Class<? extends LuaClass<?, ?>>> LUA_CLASS_CLASS_BY_JAVA_CLASS =
      Maps.uniqueIndex(LUA_CLASS_CLASSES, LuaClassLoader::getJavaClass);

  private static Iterable<Class<? extends LuaClass<?, ?>>> findClasses() {
    try {
      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      ClassPath classpath = ClassPath.from(classloader);
      ImmutableSet<ClassInfo> classInfos = classpath.getTopLevelClassesRecursive(CLASSES_PACKAGE);
      Iterable<Class<?>> classes = transform(classInfos, ClassInfo::load);
      Iterable<Class<?>> x = filter(classes, cls -> cls.isAnnotationPresent(DeclareLuaClass.class));
      Iterable<Class<?>> luaClasses = filter(x, LuaClass.class::isAssignableFrom);
      @SuppressWarnings("unchecked")
      Iterable<Class<? extends LuaClass<?, ?>>> result =
          (Iterable<Class<? extends LuaClass<?, ?>>>) (Iterable<?>) luaClasses;
      return result;
    } catch (IOException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }

  // FIXME Adrodoc55 07.02.2018: sortieren sollte implizit bei load class passieren, indem die
  // superklasse fals nicht vorhanden geladen wird
  private static List<Class<? extends LuaClass<?, ?>>> sortByClassHierachy(
      Iterable<Class<? extends LuaClass<?, ?>>> luaClasses) {
    Multimap<String, Class<? extends LuaClass<?, ?>>> subclasses = ArrayListMultimap.create();
    for (Class<? extends LuaClass<?, ?>> luaClass : luaClasses) {
      DeclareLuaClass anno = luaClass.getAnnotation(DeclareLuaClass.class);
      subclasses.put(anno.superclassname(), luaClass);
    }

    Deque<Class<? extends LuaClass<?, ?>>> todo = new ArrayDeque<>();
    todo.addAll(subclasses.removeAll(""));

    List<Class<? extends LuaClass<?, ?>>> result = new ArrayList<>();
    while (!todo.isEmpty()) {
      Class<? extends LuaClass<?, ?>> cls = todo.pop();
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

  private static Class<?> getJavaClass(Class<? extends LuaClass<?, ?>> luaClass) {
    // TODO Adrodoc55 07.02.2018: Zusammenziehen mit LuaClass.getJavaClass() ?
    @SuppressWarnings("serial")
    TypeToken<LuaClass<?, ?>> token = new TypeToken<LuaClass<?, ?>>(luaClass) {};
    // FIXME Adrodoc55 07.02.2018: cls.getGenericSuperclass()? nicht eher nur cls
    TypeToken<?> resolveType = token.resolveType(luaClass.getGenericSuperclass());
    Type type = resolveType.getType();
    ParameterizedType ptype = (ParameterizedType) type;
    Type arg1 = ptype.getActualTypeArguments()[0];
    Class<?> typeArg = (Class<?>) arg1;
    return typeArg;
  }

  public static boolean isSupported(Class<?> javaClass) {
    return LUA_CLASS_CLASS_BY_JAVA_CLASS.containsKey(javaClass);
  }

  public static String getLuaClassNameOf(Class<?> javaClass) {
    Class<? extends LuaClass<?, ?>> luaClassClass = LUA_CLASS_CLASS_BY_JAVA_CLASS.get(javaClass);
    return luaClassClass.getAnnotation(DeclareLuaClass.class).name();
  }

  private final Set<LuaClass<?, ?>> luaClasses = new HashSet<>();
  private final Map<Class<?>, LuaClass<?, ?>> luaClassByJavaClass = new HashMap<>();
  private final Types types;
  private final Converters converters;

  public LuaClassLoader(Table env) {
    types = new Types(env);
    converters = new Converters(this);
  }

  public Types getTypes() {
    return types;
  }

  public Converters getConverters() {
    return converters;
  }

  public void loadAllLuaClasses() {
    for (Class<? extends LuaClass<?, ?>> luaClassClass : LUA_CLASS_CLASSES) {
      load(luaClassClass);
    }
  }

  public void load(Class<? extends LuaClass<?, ?>> luaClassClass) {
    DeclareLuaClass annotation = luaClassClass.getAnnotation(DeclareLuaClass.class);
    String luaClassName = annotation.name();
    String luaSuperClassName = Strings.emptyToNull(annotation.superclassname());
    Table metaTable = types.declare(luaClassName, luaSuperClassName);
    try {
      LuaClass<?, ?> luaClass = luaClassClass.newInstance();
      luaClass.setMetatable(metaTable);
      luaClass.setClassLoader(this);
      luaClasses.add(luaClass);
      luaClassByJavaClass.put(luaClass.getJavaClass(), luaClass);
    } catch (InstantiationException | IllegalAccessException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public @Nullable <J> LuaClass<J, ?> getByJavaClass(Class<J> javaClass) {
    requireNonNull(javaClass, "javaClass == null!");
    @SuppressWarnings("unchecked")
    LuaClass<J, ?> luaClass = (LuaClass<J, ?>) luaClassByJavaClass.get(javaClass);
    return luaClass;
  }

  public <J> LuaClass<? super J, ?> getByJavaClassOrSuperClass(Class<J> javaClass) {
    if (javaClass == null) {
      return null;
    }
    LuaClass<J, ?> luaClass = getByJavaClass(javaClass);
    if (luaClass != null) {
      return luaClass;
    }
    Class<? super J> superClass = javaClass.getSuperclass();
    return getByJavaClassOrSuperClass(superClass);
  }
}
