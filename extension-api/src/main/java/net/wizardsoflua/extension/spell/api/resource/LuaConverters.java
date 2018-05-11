package net.wizardsoflua.extension.spell.api.resource;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.wizardsoflua.extension.spell.spi.LuaConverter;

public interface LuaConverters {
  @Nullable
  <J> List<J> toJavaListNullable(Class<J> type, @Nullable Object luaObject, int argumentIndex,
      String argumentName, String functionOrPropertyName);

  <J> List<J> toJavaList(Class<J> type, Object luaObject, int argumentIndex, String argumentName,
      String functionOrPropertyName);

  <J> Optional<J> toJavaOptional(Class<J> type, @Nullable Object luaObject, int argumentIndex,
      String argumentName, String functionOrPropertyName);

  @Nullable
  <J> J toJavaNullable(Class<J> type, @Nullable Object luaObject, int argumentIndex,
      String argumentName, String functionOrPropertyName);

  <J> J toJava(Class<J> type, Object luaObject, int argumentIndex, String argumentName,
      String functionOrPropertyName);

  <J> List<J> toJavaList(Class<J> type, Object[] args, String functionOrPropertyName);

  @Nullable
  <J> List<J> toJavaListNullable(Class<J> type, @Nullable Object luaObject,
      String functionOrPropertyName);

  <J> List<J> toJavaList(Class<J> type, Object luaObject, String functionOrPropertyName);

  <J> Optional<J> toJavaOptional(Class<J> type, @Nullable Object luaObject,
      String functionOrPropertyName);

  @Nullable
  <J> J toJavaNullable(Class<J> type, @Nullable Object luaObject, String functionOrPropertyName);

  <J> J toJava(Class<J> type, Object luaObject, String functionOrPropertyName);

  Optional<? extends Object> toLuaOptional(@Nullable Object value);

  @Nullable
  Object toLuaNullable(@Nullable Object value);

  <J> Object toLua(J javaObject);

  void registerLuaConverter(LuaConverter<?, ?> converter) throws IllegalArgumentException;

  <J> LuaConverter<? super J, ?> getLuaConverterForJavaClass(Class<J> javaClass);
}
