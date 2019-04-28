package net.wizardsoflua.extension;

import static java.util.Objects.requireNonNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Scope;
import javax.inject.Singleton;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import net.wizardsoflua.extension.api.inject.PostConstruct;
import net.wizardsoflua.extension.api.inject.PreDestroy;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.reflect.ReflectionUtils;

/**
 * An implementation of the {@link javax.inject} specification with support for registering
 * resources that can be injected using @{@link Resource}.
 *
 * @author Adrodoc
 * @see javax.inject
 */
public class InjectionScope implements AutoCloseable {
  private final @Nullable InjectionScope parent;
  private final Class<? extends Annotation> scopeAnnotationType;
  private final ClassIndex instances = new ClassIndex();
  private final ClassToInstanceMap<Object> resources = MutableClassToInstanceMap.create();
  private final List<Object> dependentScopedInstancesWithPreDestroyMethod = new ArrayList<>();

  public InjectionScope() {
    parent = null;
    scopeAnnotationType = Singleton.class;
    registerResource(InjectionScope.class, this);
  }

  public InjectionScope(InjectionScope parent, Class<? extends Annotation> scopeAnnotationType) {
    this.parent = requireNonNull(parent, "parent == null!");
    this.scopeAnnotationType = requireNonNull(scopeAnnotationType, "scopeAnnotationType == null!");
    for (InjectionScope scope = parent; scope != null; scope = scope.parent) {
      if (scope.scopeAnnotationType.equals(scopeAnnotationType)) {
        throw new IllegalArgumentException(
            "Trying to create nested scope for " + scopeAnnotationType);
      }
    }
    registerResource(InjectionScope.class, this);
  }

  public InjectionScope createSubScope(Class<? extends Annotation> scopeAnnotationType) {
    return new InjectionScope(this, scopeAnnotationType);
  }

  @Override
  public void close() {
    for (Object instance : instances.values()) {
      callLifecycleMethods(instance, PreDestroy.class);
    }
    for (Object instance : dependentScopedInstancesWithPreDestroyMethod) {
      callLifecycleMethods(instance, PreDestroy.class);
    }
  }

  public <R> void registerResource(Class<R> resourceInterface, R resource) {
    if (resources.containsKey(resourceInterface)) {
      throw new IllegalArgumentException(
          "Resource " + resourceInterface.getName() + " is already registered for this scope");
    }
    resources.putInstance(resourceInterface, resource);
  }

  public <R> R getResource(Class<R> resourceInterface) throws IllegalArgumentException {
    R resource = resources.getInstance(resourceInterface);
    if (resource != null) {
      return resource;
    } else if (parent != null) {
      return parent.getResource(resourceInterface);
    } else {
      throw new IllegalArgumentException("Unknown resource " + resourceInterface.getName());
    }
  }

  private Object provideResource(Type type) throws IllegalArgumentException {
    return provide(type, this::getResource);
  }

  private Object provide(Type type, Function<Class<?>, Object> instanceForClass)
      throws IllegalStateException {
    if (type instanceof Class<?>) {
      Class<?> cls = (Class<?>) type;
      return instanceForClass.apply(cls);
    } else if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;
      Type rawType = parameterizedType.getRawType();
      if (Provider.class.equals(rawType)) {
        Type typeArgument = parameterizedType.getActualTypeArguments()[0];
        return (Provider<Object>) () -> provide(typeArgument, instanceForClass);
      } else {
        return provide(rawType, instanceForClass);
      }
    }
    throw fail(type, "unsupported type kind");
  }

  private Object provideInstance(Type type) throws IllegalStateException {
    return provide(type, this::getInstance);
  }

  public <T> T getInstance(Class<T> cls) throws IllegalStateException {
    Class<? extends Annotation> scopeType = getScopeType(cls);
    if (scopeType == null) {
      T result = provideNewInstance(cls);
      if (lifecycleMethods(cls, PreDestroy.class).findAny().isPresent()) {
        dependentScopedInstancesWithPreDestroyMethod.add(result);
      }
      return result;
    } else if (scopeType.equals(scopeAnnotationType)) {
      T result = instances.get(cls);
      if (result == null) {
        result = provideNewInstance(cls);
        instances.add(result);
      }
      return result;
    } else if (parent != null) {
      return parent.getInstance(cls);
    } else {
      throw fail(cls, "There is no active scope for @" + scopeType.getSimpleName());
    }
  }

  protected @Nullable Class<? extends Annotation> getScopeType(Class<?> cls) {
    List<Annotation> scopeAnnotations = new ArrayList<>();
    Annotation[] annotations = cls.getAnnotations();
    for (Annotation annotation : annotations) {
      Class<? extends Annotation> annotationType = annotation.annotationType();
      if (annotationType.isAnnotationPresent(Scope.class)) {
        scopeAnnotations.add(annotation);
      }
    }
    int size = scopeAnnotations.size();
    if (size < 1) {
      return null;
    } else if (size == 1) {
      Annotation scopeAnnotation = scopeAnnotations.get(0);
      return scopeAnnotation.annotationType();
    } else {
      throw fail(cls, "encountered multiple scope annotations: " + scopeAnnotations);
    }
  }

  private <T> T provideNewInstance(Class<T> cls) throws IllegalStateException {
    T instance = createInstance(cls);
    injectMembers(instance);
    callLifecycleMethods(instance, PostConstruct.class);
    return instance;
  }

  private <T> T createInstance(Class<T> cls) throws IllegalStateException {
    List<Constructor<?>> constructors = new ArrayList<>();
    for (Constructor<?> constructor : cls.getConstructors()) {
      if (isAnnotated(constructor)) {
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
      throw fail(cls, "it has multiple constructor that are annotated with @"
          + Inject.class.getSimpleName() + " or have parameters annotated with @" + Resource.class);
    }
  }

  public <T> T injectMembers(T instance) throws IllegalStateException {
    Deque<Class<?>> classes = getSortedSuperTypes(instance.getClass());
    for (Class<?> cls : classes) {
      injectFields(instance, cls);
      injectMethods(instance, cls, classes);
    }
    return instance;
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

  private boolean isAnnotated(Executable method) {
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

  /**
   * Provides the resource or instance of the given {@link Type} which is to be injected into the
   * {@link AnnotatedElement}.
   *
   * @param type
   * @param annotatedElement
   * @return the resource or instance of the given {@link Type}
   * @throws IllegalStateException if the type is not a {@link Class} or {@link ParameterizedType}
   */
  private Object getArgument(Type type, AnnotatedElement annotatedElement)
      throws IllegalStateException {
    if (annotatedElement.isAnnotationPresent(Resource.class)) {
      return provideResource(type);
    }
    return provideInstance(type);
  }

  private void callLifecycleMethods(Object instance,
      Class<? extends Annotation> lifecycleAnnotation) {
    Class<?> cls = instance.getClass();
    lifecycleMethods(cls, lifecycleAnnotation).forEach(method -> {
      runAccessible(method, () -> {
        try {
          method.invoke(instance);
        } catch (IllegalAccessException ex) {
          throw fail(cls, "failed to access method " + method, ex);
        } catch (IllegalArgumentException ex) {
          throw fail(cls, "the method " + method + " is annotated with @"
              + lifecycleAnnotation.getSimpleName() + ", but declares a parameter", ex);
        } catch (InvocationTargetException ex) {
          throw fail(cls, "the method " + method + " threw an exeption", ex.getCause());
        }
      });
    });
  }

  private Stream<Method> lifecycleMethods(Class<?> cls,
      Class<? extends Annotation> lifecycleAnnotation) {
    Deque<Class<?>> classes = getSortedSuperTypes(cls);
    return classes.stream() //
        .map(it -> it.getDeclaredMethods()) //
        .flatMap(Stream::of) //
        .filter(it -> it.isAnnotationPresent(lifecycleAnnotation)) //
        .filter(it -> !isMethodOverridden(it, classes)) //
    ;
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
