package net.wizardsoflua.lua.extension.api.service;

public interface Injector {
  <T> T inject(T instance);
}
