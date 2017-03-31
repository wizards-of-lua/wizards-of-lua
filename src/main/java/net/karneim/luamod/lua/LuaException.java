package net.karneim.luamod.lua;

public class LuaException extends Exception {

  public LuaException() {}

  public LuaException(String message) {
    super(message);
  }

  public LuaException(Throwable cause) {
    super(cause);
  }

  public LuaException(String message, Throwable cause) {
    super(message, cause);
  }

  public LuaException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
