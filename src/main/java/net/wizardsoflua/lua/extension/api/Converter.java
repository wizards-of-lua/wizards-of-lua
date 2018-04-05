package net.wizardsoflua.lua.extension.api;

import java.util.List;
import java.util.Optional;

import net.wizardsoflua.config.ConversionException;
import net.wizardsoflua.lua.BadArgumentException;

public interface Converter {
  <J> List<J> toJavaList(Class<J> type, Object luaObject, int argumentIndex, String argumentName,
      String functionOrPropertyName) throws BadArgumentException;

  <J> Optional<J> toJavaOptional(Class<J> type, Object luaObject, int argumentIndex,
      String argumentName, String functionOrPropertyName) throws BadArgumentException;

  <J> J toJavaNullable(Class<J> type, Object luaObject, int argumentIndex, String argumentName,
      String functionOrPropertyName) throws BadArgumentException;

  <J> J toJava(Class<J> type, Object luaObject, int argumentIndex, String argumentName,
      String functionOrPropertyName) throws BadArgumentException;

  <J> List<J> toJavaList(Class<J> type, Object[] args, String functionOrPropertyName)
      throws BadArgumentException;

  <J> List<J> toJavaList(Class<J> type, Object luaObject, String functionOrPropertyName)
      throws BadArgumentException;

  <J> Optional<J> toJavaOptional(Class<J> type, Object luaObject, String functionOrPropertyName)
      throws BadArgumentException;

  <J> J toJavaNullable(Class<J> type, Object luaObject, String functionOrPropertyName)
      throws BadArgumentException;

  <J> J toJava(Class<J> type, Object luaObject, String functionOrPropertyName)
      throws BadArgumentException;

  Optional<? extends Object> toLuaOptional(Object value) throws ConversionException;

  Object toLuaNullable(Object value) throws ConversionException;

  <J> Object toLua(J javaObject) throws ConversionException;
}
