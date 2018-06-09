package net.wizardsoflua.extension.spell.spi;

public interface LuaConverter<J, L> extends LuaToJavaConverter<J, L>, JavaToLuaConverter<J> {
  @Override
  L getLuaInstance(J javaInstance);

  @SuppressWarnings({"rawtypes", "unchecked"})
  static Class<LuaConverter<?, ?>> getClassWithWildcards() {
    return (Class) LuaConverter.class;
  }
}
