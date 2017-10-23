package net.wizardsoflua.lua.classes;

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

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.common.reflect.TypeToken;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.ITypes;

public class LuaClasses {

  private static final String CLASSES_PACKAGE = "net.wizardsoflua.lua.classes";

  private List<Class<?>> luaClasses;
  private Map<Class<?>, Class<?>> luaClassByJavaClass;

  public LuaClasses() {
    luaClasses = sortByClassHierachy(findClasses());
    luaClassByJavaClass = createLuaClassByJavaClassMap(luaClasses);
  }

  private Map<Class<?>, Class<?>> createLuaClassByJavaClassMap(Iterable<Class<?>> classes) {
    Map<Class<?>, Class<?>> result = new HashMap<>();
    for (Class<?> cls : classes) {
      Class<?> typeArg = getGenericTypeArgument(cls);
      result.put(typeArg, cls);
    }
    return result;
  }

  private Class<?> getGenericTypeArgument(Class<?> cls) {
    @SuppressWarnings("serial")
    TypeToken<LuaClass<?, ?>> token = new TypeToken<LuaClass<?, ?>>(cls) {};
    TypeToken<?> resolveType = token.resolveType(cls.getGenericSuperclass());
    Type type = resolveType.getType();
    ParameterizedType ptype = (ParameterizedType) type;
    Type arg1 = ptype.getActualTypeArguments()[0];
    Class<?> typeArg = (Class<?>) arg1;
    return typeArg;
  }

  public String getLuaClassnameOf(Class<?> javaClass) {
    Class<?> luaClass = luaClassByJavaClass.get(javaClass);
    return luaClass.getAnnotation(DeclareLuaClass.class).name();
  }

  public boolean isSupported(Class<?> javaClass) {
    return luaClassByJavaClass.containsKey(javaClass);
  }

  public Set<LuaClass<?, ?>> load(Converters converters) {
    Map<String, Table> metatables = declareMetatables(converters.getTypes());

    Set<LuaClass<?, ?>> result = new HashSet<>();
    for (Class<?> cls : luaClasses) {
      LuaClass<?, ?> elem = load(cls, converters, metatables);
      result.add(elem);
    }
    return result;
  }

  private Map<String, Table> declareMetatables(ITypes types) {
    Map<String, Table> result = new HashMap<>();
    for (Class<?> luaClass : luaClasses) {
      DeclareLuaClass anno = luaClass.getAnnotation(DeclareLuaClass.class);
      if (anno.superclassname().isEmpty()) {
        Table mt = types.declare(anno.name());
        result.put(anno.name(), mt);
      } else {
        Table mt = types.declare(anno.name(), anno.superclassname());
        result.put(anno.name(), mt);
      }
    }
    return result;
  }

  private List<Class<?>> sortByClassHierachy(Iterable<Class<?>> luaClasses) {
    Multimap<String, Class<?>> subclasses = ArrayListMultimap.create();
    for (Class<?> luaClass : luaClasses) {
      DeclareLuaClass anno = luaClass.getAnnotation(DeclareLuaClass.class);
      subclasses.put(anno.superclassname(), luaClass);
    }

    Deque<Class<?>> todo = new ArrayDeque<>();
    todo.addAll(subclasses.removeAll(""));

    List<Class<?>> result = new ArrayList<>();
    while (!todo.isEmpty()) {
      Class<?> cls = todo.pop();
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

  private String getClassnames(Iterable<Class<?>> list) {
    return Joiner.on(", ").join(Iterables.transform(list, (e) -> e.getName()));
  }

  private LuaClass<?, ?> load(Class<?> cls, Converters converters, Map<String, Table> metatables) {
    try {
      DeclareLuaClass anno = cls.getAnnotation(DeclareLuaClass.class);
      Table metatable = metatables.get(anno.name());
      Object obj = cls.newInstance();
      LuaClass<?, ?> result = (LuaClass<?, ?>) obj;
      result.setConverters(converters);
      result.setMetatable(metatable);
      return result;
    } catch (InstantiationException | IllegalAccessException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  private Iterable<Class<?>> findClasses() {
    try {
      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      ClassPath classpath = ClassPath.from(classloader);
      ImmutableSet<ClassInfo> xx = classpath.getTopLevelClassesRecursive(CLASSES_PACKAGE);
      Iterable<ClassInfo> yy = Iterables.filter(xx, input -> isLuaClass(input));
      return Iterables.transform(yy, ClassInfo::load);
    } catch (IOException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  private boolean isLuaClass(ClassInfo input) {
    Class<?> cls = input.load();
    return LuaClass.class.isAssignableFrom(cls) && cls.getAnnotation(DeclareLuaClass.class) != null;
  }



}
