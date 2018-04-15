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
import net.wizardsoflua.lua.extension.api.inject.Inject;
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
        if (field.isAnnotationPresent(Inject.class)) {
          checkArgument(!Modifier.isStatic(modifiers), "The static field %s is annotated with @%s",
              field, Inject.class.getSimpleName());
          checkArgument(!Modifier.isFinal(modifiers), "The final field %s is annotated with @%s",
              field, Inject.class.getSimpleName());
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
      if (!Modifier.isStatic(modifiers)//
          && !Modifier.isAbstract(modifiers)//
          && method.isAnnotationPresent(AfterInjection.class)) {
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
