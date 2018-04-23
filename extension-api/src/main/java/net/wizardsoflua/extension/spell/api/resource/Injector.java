package net.wizardsoflua.extension.spell.api.resource;

public interface Injector {
  <T> T injectMembers(T instance) throws IllegalStateException;

  <T> T getInstance(Class<T> cls) throws IllegalStateException;
}
