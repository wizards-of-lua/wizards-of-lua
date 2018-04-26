package net.wizardsoflua.lua.extension;

import java.lang.annotation.Annotation;

import javax.annotation.Nullable;

import net.wizardsoflua.extension.spell.api.SpellScoped;
import net.wizardsoflua.extension.spell.spi.SpellExtension;

public class SpellScope extends InjectionScope {
  public SpellScope(InjectionScope parent) {
    super(parent, SpellScoped.class);
  }

  @Override
  protected @Nullable Annotation getScopeAnnotation(Class<?> cls) {
    Annotation annotation = super.getScopeAnnotation(cls);
    if (annotation == null && SpellExtension.class.isAssignableFrom(cls)) {
      return () -> SpellScoped.class;
    } else {
      return null;
    }
  }
}
