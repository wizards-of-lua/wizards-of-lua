package net.wizardsoflua.spell;

import java.util.Optional;
import com.google.common.base.Throwables;
import net.wizardsoflua.lua.SpellProgram;

public class SpellExceptionFactory {
  public SpellException create(Throwable throwable) {
    String message = getMessage(throwable);
    CharSequence luaStackTrace = getLuaStackTrace(throwable);
    return new SpellException(message + luaStackTrace, throwable);
  }

  private static String getMessage(Throwable throwable) {
    Throwable rootCause = Throwables.getRootCause(throwable);
    String message = rootCause.getMessage();
    return Optional.ofNullable(message).orElse("Unknown error");
  }

  private static CharSequence getLuaStackTrace(Throwable throwable) {
    Throwable rootCause = Throwables.getRootCause(throwable);
    StringBuilder luaStackTrace = new StringBuilder();
    StackTraceElement[] stackTrace = rootCause.getStackTrace();
    for (StackTraceElement stackTraceElement : stackTrace) {
      String className = stackTraceElement.getClassName();
      int lineNumber = stackTraceElement.getLineNumber();
      if (className.startsWith(SpellProgram.ROOT_CLASS_PREFIX) && lineNumber >= 0) {
        String fileName = stackTraceElement.getFileName();
        luaStackTrace.append("\n at line ").append(lineNumber).append(" of ").append(fileName);
      }
    }
    return luaStackTrace;
  }
}
