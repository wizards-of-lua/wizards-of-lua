package net.wizardsoflua.lua.classes;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.ITypes;

public class LuaClasses {

  private static final String CLASSES_PACKAGE = "net.wizardsoflua.lua.classes";

  private Set<Class<?>> classes;

  public LuaClasses() {
    classes = Sets.newHashSet(findClasses());
  }

  public Set<LuaClass<?>> load(Converters converters) {
    Map<String, Table> metatables = declareMetatables(converters.getTypes());

    Set<LuaClass<?>> result = new HashSet<>();
    for (Class<?> cls : classes) {
      LuaClass<?> elem = load(cls, converters, metatables);
      result.add(elem);
    }
    return result;
  }

  private Map<String, Table> declareMetatables(ITypes types) {
    Deque<DeclareLuaClass> todo = new ArrayDeque<>();
    for (Class<?> cls : classes) {
      todo.addLast(cls.getAnnotation(DeclareLuaClass.class));
    }
    Map<String, Table> result = new HashMap<>();
    int count = 0;
    int maxLoops = classes.size() * classes.size();
    while (!todo.isEmpty() && count <= maxLoops) {
      count++;
      DeclareLuaClass anno = todo.pop();
      if (anno.superclassname().isEmpty()) {
        Table mt = types.declare(anno.name());
        result.put(anno.name(), mt);
      } else if (result.get(anno.superclassname()) != null) {
        Table mt = types.declare(anno.name(), anno.superclassname());
        result.put(anno.name(), mt);
      } else {
        todo.addLast(anno);
      }
    }
    if (!todo.isEmpty()) {
      throw new IllegalStateException(
          String.format("Can't find Lua-Classes: %s", getSuperclassnames(todo)));
    }
    return result;
  }

  private String getSuperclassnames(Iterable<DeclareLuaClass> list) {
    return Joiner.on(", ").join(Iterables.transform(list, (e) -> e.superclassname()));
  }

  private LuaClass<?> load(Class<?> cls, Converters converters, Map<String, Table> metatables) {
    try {
      DeclareLuaClass anno = cls.getAnnotation(DeclareLuaClass.class);
      Table metatable = metatables.get(anno.name());
      Object obj = cls.newInstance();
      LuaClass<?> result = (LuaClass<?>) obj;
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
