package net.wizardsoflua.extension.spell.api.resource;

public interface Injector {
  <T> T injectMembers(T instance);
}
