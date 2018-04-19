package net.wizardsoflua.lua.extension;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import net.wizardsoflua.lua.extension.api.inject.Resource;
import net.wizardsoflua.reflect.ReflectionUtils;

public class Injector implements net.wizardsoflua.lua.extension.api.service.Injector {
  private final Map<Class<?>, Object> resources = new HashMap<>();
  private final ClassIndex singletons = new ClassIndex();

  public <R> void registerResource(Class<R> resourceInterface, R resource) {
    resources.put(resourceInterface, resource);
  }

  public <R> R getResource(Class<R> resourceInterface) throws IllegalArgumentException {
    Object resource = resources.get(resourceInterface);
    checkArgument(resource != null, "Unknown resource " + resourceInterface.getName());
    return resourceInterface.cast(resource);
  }

  private Object getResource(Type type) throws IllegalArgumentException {
    return provideInstance(type, this::getResource);
  }

  @Override
  public <T> T inject(T instance) {
    injectMembers(instance);
    return instance;
  }

  private Object provideInstance(Type type, Function<Class<?>, Object> instanceForClass)
      throws IllegalStateException {
    if (type instanceof Class<?>) {
      Class<?> cls = (Class<?>) type;
      return instanceForClass.apply(cls);
    } else if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;
      Type rawType = parameterizedType.getRawType();
      if (Provider.class.equals(rawType)) {
        Type typeArgument = parameterizedType.getActualTypeArguments()[0];
        return new Provider<Object>() {
          @Override
          public Object get() {
            return provideInstance(typeArgument, instanceForClass);
          }
        };
      } else {
        return provideInstance(rawType, instanceForClass);
      }
    }
    throw fail(type, "unknown type kind");
  }

  private Object provideInstance(Type type) throws IllegalStateException {
    return provideInstance(type, this::provideInstance);
  }

  public <T> T provideInstance(Class<T> cls) {
    if (cls.isAnnotationPresent(Singleton.class)) {
      T result = singletons.get(cls);
      if (result == null) {
        result = provideNewInstance(cls);
        singletons.add(result);
      }
      return result;
    }
    return provideNewInstance(cls);
  }

  private <T> T provideNewInstance(Class<T> cls) throws IllegalStateException {
    T instance = createInstance(cls);
    return inject(instance);
  }

  private <T> T createInstance(Class<T> cls) throws IllegalStateException {
    List<Constructor<?>> constructors = new ArrayList<>();
    for (Constructor<?> constructor : cls.getConstructors()) {
      if (constructor.isAnnotationPresent(Inject.class)) {
        constructors.add(constructor);
      }
    }
    int size = constructors.size();
    if (size < 1) {
      try {
        return cls.newInstance();
      } catch (InstantiationException | IllegalAccessException ex) {
        throw fail(cls, "it does not have an injectable constructor", ex);
      }
    } else if (size == 1) {
      Constructor<?> constructor = constructors.get(0);
      Parameter[] parameters = constructor.getParameters();
      Object[] arguments;
      try {
        arguments = getArguments(parameters);
      } catch (Exception ex) {
        throw fail(cls, "failed to inject constructor " + constructor, ex);
      }
      return cls.cast(getAccessible(constructor, () -> {
        try {
          return constructor.newInstance(arguments);
        } catch (InvocationTargetException ex) {
          throw fail(cls, "the constructor threw an exeption", ex.getCause());
        } catch (InstantiationException ex) {
          throw fail(cls, "unexpected exception during construction", ex.getCause());
        } catch (IllegalAccessException ex) {
          throw fail(cls, "failed to access constructor " + constructor, ex);
        }
      }));
    } else {
      throw fail(cls,
          "it has multiple constructor annotated with @" + Inject.class.getSimpleName());
    }
  }

  private void injectMembers(Object instance) throws IllegalStateException {
    Deque<Class<?>> classes = getSortedSuperTypes(instance.getClass());
    for (Class<?> cls : classes) {
      injectFields(instance, cls);
      injectMethods(instance, cls, classes);
    }
  }

  private <T> void injectFields(T instance, Class<?> declaringClass) throws IllegalStateException {
    Field[] fields = declaringClass.getDeclaredFields();
    for (Field field : fields) {
      if (field.isAnnotationPresent(Inject.class) || field.isAnnotationPresent(Resource.class)) {
        int modifiers = field.getModifiers();
        if (Modifier.isStatic(modifiers)) {
          throw fail(instance.getClass(), "static fields like " + field + " cannot be injected");
        } else if (Modifier.isFinal(modifiers)) {
          throw fail(instance.getClass(), "final fields like " + field + " cannot be injected");
        }

        Type type = field.getGenericType();
        Object value;
        try {
          value = getArgument(type, field);
        } catch (Exception ex) {
          throw fail(instance.getClass(), "failed to inject field " + field, ex);
        }
        runAccessible(field, () -> {
          try {
            field.set(instance, value);
          } catch (IllegalAccessException ex) {
            throw fail(instance.getClass(), "failed to access field " + field, ex);
          }
        });
      }
    }
  }

  private <T> void injectMethods(T instance, Class<?> declaringClass, Deque<Class<?>> classes)
      throws IllegalStateException {
    Method[] methods = declaringClass.getDeclaredMethods();
    for (Method method : methods) {
      if (isAnnotated(method) && !isMethodOverridden(method, classes)) {
        int modifiers = method.getModifiers();
        if (Modifier.isStatic(modifiers)) {
          throw fail(instance.getClass(), "static methods like " + method + " cannot be injected");
        }

        Parameter[] parameters = method.getParameters();
        Object[] arguments;
        try {
          arguments = getArguments(parameters);
        } catch (Exception ex) {
          throw fail(instance.getClass(), "failed to inject method " + method, ex);
        }
        runAccessible(method, () -> {
          try {
            method.invoke(instance, arguments);
          } catch (InvocationTargetException ex) {
            throw fail(instance.getClass(), "the method " + method + " threw an exeption",
                ex.getCause());
          } catch (IllegalAccessException ex) {
            throw fail(instance.getClass(), "failed to access method " + method, ex);
          }
        });
      }
    }
  }

  private boolean isAnnotated(Method method) {
    if (method.isAnnotationPresent(Inject.class)) {
      return true;
    }
    for (Parameter parameter : method.getParameters()) {
      if (parameter.isAnnotationPresent(Resource.class)) {
        return true;
      }
    }
    return false;
  }

  private boolean isMethodOverridden(Method method, Deque<Class<?>> classes) {
    Class<?> cls = method.getDeclaringClass();
    for (Class<?> subClass : classes) {
      if (cls.equals(subClass) || !cls.isAssignableFrom(subClass)) {
        continue;
      }
      Method[] subMethods = subClass.getDeclaredMethods();
      for (Method subMethod : subMethods) {
        if (ReflectionUtils.isOverridden(method, subMethod)) {
          return true;
        }
      }
    }
    return false;
  }

  private Object[] getArguments(Parameter[] parameters) throws IllegalStateException {
    Object[] arguments = new Object[parameters.length];
    for (int i = 0; i < parameters.length; i++) {
      Parameter parameter = parameters[i];
      Object argument = getArgument(parameter);
      arguments[i] = argument;
    }
    return arguments;
  }

  private Object getArgument(Parameter parameter) throws IllegalStateException {
    return getArgument(parameter.getParameterizedType(), parameter);
  }

  private Object getArgument(Type type, AnnotatedElement annotatedElement)
      throws IllegalStateException {
    boolean isInject = annotatedElement.isAnnotationPresent(Inject.class);
    boolean isResource = annotatedElement.isAnnotationPresent(Resource.class);
    if (isInject && isResource) {
      throw new IllegalArgumentException("Use one of @" + Inject.class.getSimpleName() + " or @"
          + Resource.class.getSimpleName() + ", but not both");
    } else if (isResource) {
      return getResource(type);
    }
    return provideInstance(type);
  }

  private static void runAccessible(AccessibleObject object, Runnable runnable) {
    getAccessible(object, () -> {
      runnable.run();
      return null;
    });
  }

  private static <T> T getAccessible(AccessibleObject object, Supplier<T> supplier) {
    boolean accessible = object.isAccessible();
    if (!accessible) {
      object.setAccessible(true);
    }
    try {
      return supplier.get();
    } finally {
      if (!accessible) {
        object.setAccessible(false);
      }
    }
  }

  private IllegalStateException fail(Type type, String message) {
    return fail(type, message, null);
  }

  private IllegalStateException fail(Type type, String message, Throwable t) {
    throw new IllegalStateException(type + " cannot be injected - " + message, t);
  }

  /**
   * Returns a topologically sorted {@link Deque} of all super classes and interfaces of
   * {@code cls}. Types that are higher in the hierarchy are listed before types that are lower in
   * the hierarchy.
   *
   * @param cls
   * @return a topologically sorted {@link Deque}
   */
  private static <T> Deque<Class<?>> getSortedSuperTypes(Class<?> cls) {
    MutableGraph<Class<?>> classGraph = GraphBuilder.directed().build();
    classGraph.addNode(cls);
    Deque<Class<?>> todos = new ArrayDeque<>();
    todos.add(cls);
    while (!todos.isEmpty()) {
      Class<?> todo = todos.pop();
      Class<?> superclass = todo.getSuperclass();
      if (superclass != null) {
        if (!classGraph.nodes().contains(superclass)) {
          todos.add(superclass);
        }
        classGraph.putEdge(superclass, todo);
      }
      for (Class<?> i : todo.getInterfaces()) {
        if (!classGraph.nodes().contains(i)) {
          todos.add(i);
        }
        classGraph.putEdge(i, todo);
      }
    }

    // Topological sort (Kahn's algorithm)
    Deque<Class<?>> result = new ArrayDeque<>();
    Set<Class<?>> nodesWithoutPredecessors = new LinkedHashSet<>();
    for (Class<?> node : classGraph.nodes()) {
      if (classGraph.predecessors(node).isEmpty()) {
        nodesWithoutPredecessors.add(node);
      }
    }
    while (!nodesWithoutPredecessors.isEmpty()) {
      Class<?> node = nodesWithoutPredecessors.iterator().next();
      nodesWithoutPredecessors.remove(node);
      result.add(node);
      Iterable<Class<?>> successors = new ArrayList<>(classGraph.successors(node));
      for (Class<?> successor : successors) {
        classGraph.removeEdge(node, successor);
        Set<Class<?>> predecessors = classGraph.predecessors(successor);
        if (predecessors.isEmpty()) {
          nodesWithoutPredecessors.add(successor);
        }
      }
    }
    return result;
  }
}
