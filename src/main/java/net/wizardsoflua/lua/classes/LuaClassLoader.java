package net.wizardsoflua.lua.classes;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.spi.DeclaredLuaClass;
import net.wizardsoflua.lua.extension.ServiceLoader;
import net.wizardsoflua.lua.module.events.EventsModule;
import net.wizardsoflua.lua.module.types.Types;
import net.wizardsoflua.lua.nbt.NbtConverter;
import net.wizardsoflua.lua.scheduling.LuaSchedulingContext;
import net.wizardsoflua.lua.view.ViewFactory;

public class LuaClassLoader {
  private static final String CLASSES_PACKAGE = "net.wizardsoflua.lua.classes";

  public static void initialize(Logger logger) {
    JAVA_LUA_CLASS_CLASSES = findJavaLuaClassClasses(logger);
    JAVA_LUA_CLASS_CLASS_BY_JAVA_CLASS =
        Maps.uniqueIndex(JAVA_LUA_CLASS_CLASSES, new Function<Class<?>, Class<?>>() {
          @Override // for some reason the JDK does not like our generics here so we use raw types
          @SuppressWarnings({"rawtypes", "unchecked"})
          public Class<?> apply(Class<?> input) {
            return JavaLuaClass.getJavaClassOf((Class) input);
          }
        });
  }

  private static ImmutableList<Class<? extends JavaLuaClass<?, ?>>> JAVA_LUA_CLASS_CLASSES;
  private static ImmutableMap<Class<?>, Class<? extends JavaLuaClass<?, ?>>> JAVA_LUA_CLASS_CLASS_BY_JAVA_CLASS;

  private static ImmutableList<Class<? extends JavaLuaClass<?, ?>>> findJavaLuaClassClasses(
      Logger logger) {
    try {
      logger.debug("Searching for Lua classes...");
      Set<Class<? extends DeclaredLuaClass>> luaClasses =
          ServiceLoader.load(logger, DeclaredLuaClass.class).stream()
              .filter(c -> JavaLuaClass.class.isAssignableFrom(c)).collect(Collectors.toSet());

      Iterable<Class<? extends JavaLuaClass<?, ?>>> result =
          (Iterable<Class<? extends JavaLuaClass<?, ?>>>) (Iterable<?>) luaClasses;
      ImmutableList<Class<? extends JavaLuaClass<?, ?>>> r = ImmutableList.copyOf(result);
      logger.debug("Found Lua classes: "
          + r.stream().map(c -> c.getName()).collect(Collectors.joining(", ")));
      return r;
    } catch (Exception ex) {
      throw new UndeclaredThrowableException(ex);
    }
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
  private final Map<Class<? extends LuaClass>, LuaClass> luaClassByType = new HashMap<>();
  private final Map<String, LuaClass> luaClassByName = new HashMap<>();
  private final Map<Table, LuaClass> luaClassByMetaTable = new HashMap<>();
  private final Map<Class<?>, JavaLuaClass<?, ?>> luaClassByJavaClass = new HashMap<>();
  private final Types types;
  private final Converters converters = new Converters(this);
  private final Context context;

  @Inject
  private EventsModule events;
  @Inject
  private NbtConverter nbtConverter;
  @Inject
  private ViewFactory viewFactory;

  public interface Context {
    @Nullable
    LuaSchedulingContext getCurrentSchedulingContext();
  }

  public LuaClassLoader(Table env, Context context) {
    this.env = requireNonNull(env, "env == null!");
    this.context = requireNonNull(context, "context == null!");
    types = new Types(this, converters);
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

  /**
   * @deprecated Use @{@link Inject}
   */
  @Deprecated
  public ViewFactory getViewFactory() {
    return viewFactory;
  }

  /**
   * @deprecated Use @{@link Inject}
   */
  @Deprecated
  public EventsModule getEventsModule() {
    return events;
  }

  /**
   * @deprecated Use @{@link Inject}
   */
  @Deprecated
  public NbtConverter getNbtConverter() {
    return nbtConverter;
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
    if (luaClassByName.containsKey(luaClass.getName())) {
      return; // LuaClass is already loaded
    }
    luaClass.load(this);
    luaClassByType.put(luaClass.getClass(), luaClass);
    luaClassByName.put(luaClass.getName(), luaClass);
    luaClassByMetaTable.put(luaClass.getMetaTable(), luaClass);
    if (luaClass instanceof JavaLuaClass) {
      JavaLuaClass<?, ?> javaLuaClass = (JavaLuaClass<?, ?>) luaClass;
      luaClassByJavaClass.put(javaLuaClass.getJavaClass(), javaLuaClass);
    }
    types.registerLuaClass(luaClass.getName(), luaClass.getMetaTable());
    env.rawset(luaClass.getName(), luaClass.getMetaTable());
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

  /**
   * Returns the {@link LuaClass} with the specified name or {@code null} if no such
   * {@link LuaClass} was loaded by {@code this} {@link LuaClassLoader}.
   *
   * @param luaClassName the name of the {@link LuaClass}
   * @return the {@link LuaClass} with the specified name or {@code null}
   * @throws NullPointerException if the specified name is {@code null}
   */
  public @Nullable LuaClass getLuaClassForName(String luaClassName) throws NullPointerException {
    requireNonNull(luaClassName, "luaClassName == null!");
    return luaClassByName.get(luaClassName);
  }

  /**
   * Returns the {@link LuaClass} with the specified table or {@code null} if no such
   * {@link LuaClass} was loaded by {@code this} {@link LuaClassLoader}.
   *
   * @param luaClassTable the table of the {@link LuaClass}
   * @return the {@link LuaClass} with the specified table or {@code null}
   * @throws NullPointerException if the specified table is {@code null}
   */
  public @Nullable LuaClass getLuaClassForClassTable(Table luaClassMetaTable)
      throws NullPointerException {
    requireNonNull(luaClassMetaTable, "luaClassMetaTable == null!");
    LuaClass luaClass = luaClassByMetaTable.get(luaClassMetaTable);
    return luaClass;
  }

  /**
   * Returns the {@link LuaClass} of the specified {@link Table}. If {@code luaObject} is not an
   * instance of a class then {@code null} is returned.
   *
   * @param luaObject
   * @return the {@link LuaClass} or {@code null}
   */
  public @Nullable LuaClass getLuaClassOfInstance(Table luaObject) {
    LuaClass result = getLuaClassForClassTable(luaObject);
    if (result != null) {
      return null; // luaObject is a class itself and we don't want to return the superclass
    }
    Table metatable = luaObject.getMetatable();
    if (metatable != null) {
      return getLuaClassForClassTable(metatable);
    }
    return null;
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

  public LuaSchedulingContext getCurrentSchedulingContext() {
    return context.getCurrentSchedulingContext();
  }
}
