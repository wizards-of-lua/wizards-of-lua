package net.wizardsoflua.extension.spell.spi;

public interface JavaToLuaConverter<J> extends SpellExtension {
  Class<J> getJavaClass();

  Object getLuaInstance(J javaInstance);

  @SuppressWarnings({"rawtypes", "unchecked"})
  static Class<JavaToLuaConverter<?>> getClassWithWildcards() {
    return (Class) JavaToLuaConverter.class;
  }
}
