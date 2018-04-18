package net.wizardsoflua.lua.extension;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Throwables;

import net.wizardsoflua.lua.extension.api.inject.AfterInjection;
import net.wizardsoflua.lua.extension.api.inject.Resource;
import net.wizardsoflua.lua.extension.api.service.Injector;

public class ServiceInjector implements Injector {
  private final Map<Class<?>, Object> services = new HashMap<>();

  public <S> void registerService(Class<S> serviceInterface, S service) {
    services.put(serviceInterface, service);
  }

  @Override
  public <T> T inject(T t) {
    injectServicesInto(t);
    return t;
  }

  public void injectServicesInto(Object object) {
    injectFields(object);
    callAfterInjction(object);
  }

  private void injectFields(Object object) {
    for (Class<?> cls = object.getClass(); cls != null; cls = cls.getSuperclass()) {
      for (Field field : cls.getDeclaredFields()) {
        int modifiers = field.getModifiers();
        if (field.isAnnotationPresent(Resource.class)) {
          checkArgument(!Modifier.isStatic(modifiers), "The static field %s is annotated with @%s",
              field, Resource.class.getSimpleName());
          checkArgument(!Modifier.isFinal(modifiers), "The final field %s is annotated with @%s",
              field, Resource.class.getSimpleName());
          Object service = services.get(field.getType());
          checkArgument(service != null, "The field %s refers to an unknown service interface",
              field);
          boolean wasAccessible = field.isAccessible();
          field.setAccessible(true);
          try {
            field.set(object, service);
          } catch (IllegalAccessException ex) {
            throw new UndeclaredThrowableException(ex);
          } finally {
            field.setAccessible(wasAccessible);
          }
        }
      }
    }
  }

  private void callAfterInjction(Object object) {
    Method[] methods = object.getClass().getMethods();
    for (Method method : methods) {
      int modifiers = method.getModifiers();
      if (method.isAnnotationPresent(AfterInjection.class)) {
        checkArgument(!Modifier.isStatic(modifiers), "The static method %s is annotated with @%s",
            method, Resource.class.getSimpleName());
        try {
          method.invoke(object);
        } catch (IllegalAccessException ex) {
          throw new UndeclaredThrowableException(ex);
        } catch (InvocationTargetException ex) {
          Throwable cause = ex.getCause();
          Throwables.throwIfUnchecked(cause);
          throw new UndeclaredThrowableException(cause);
        }
      }
    }
  }
}
