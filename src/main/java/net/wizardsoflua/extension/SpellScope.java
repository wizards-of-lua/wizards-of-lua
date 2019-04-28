package net.wizardsoflua.extension;

import java.lang.annotation.Annotation;
import javax.annotation.Nullable;
import net.wizardsoflua.extension.spell.api.SpellScoped;
import net.wizardsoflua.extension.spell.spi.SpellExtension;

public class SpellScope extends InjectionScope {
  public SpellScope(InjectionScope parent) {
    super(parent, SpellScoped.class);
  }

  @Override
  protected @Nullable Class<? extends Annotation> getScopeType(Class<?> cls) {
    Class<? extends Annotation> scopeType = super.getScopeType(cls);
    if (scopeType == null && SpellExtension.class.isAssignableFrom(cls)) {
      return SpellScoped.class;
    } else {
      return scopeType;
    }
  }
}
