package net.wizardsoflua.spell;

import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.load.LoaderException;
import net.wizardsoflua.lua.SpellProgram;

public class SpellExceptionFactory {
  public SpellException create(Throwable throwable) {
    String message = getMessage(throwable);
    CharSequence luaStackTrace = getLuaStackTrace(throwable);
    return new SpellException(message + luaStackTrace, throwable);
  }

  private static String getMessage(Throwable throwable) {
    if (throwable instanceof LoaderException || throwable instanceof CallException) {
      throwable = throwable.getCause();
    }
    String message = throwable.getMessage();
    if (message != null) {
      return message;
    } else {
      return "Unknown error";
    }
  }

  private static CharSequence getLuaStackTrace(Throwable throwable) {
    Throwable deepestCause = null;
    for (Throwable t = throwable; t != null; t = t.getCause()) {
      deepestCause = t;
    }
    StringBuilder luaStackTrace = new StringBuilder();
    StackTraceElement[] stackTrace = deepestCause.getStackTrace();
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
