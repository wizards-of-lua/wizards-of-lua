package net.wizardsoflua.spell;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sandius.rembulan.LuaRuntimeException;
import net.sandius.rembulan.parser.ParseException;

public class SpellExceptionFactory {

  private final Pattern stackTracePattern;

  public SpellExceptionFactory(String rootClassPrefix) {
    stackTracePattern = Pattern.compile(rootClassPrefix + ".*\\.run\\((.+):(.*)\\)");
  }

  public SpellException create(Throwable throwable) {
    if (throwable.getCause() instanceof ParseException) {
      throwable = throwable.getCause();
    }

    String exMessage = getExceptionMessage(throwable);
    if (exMessage == null) {
      exMessage = "Unknown error";
    }
    String stackTrace = getStackTrace(throwable);
    Matcher m = stackTracePattern.matcher(stackTrace);
    StringBuilder modules = new StringBuilder();
    while (m.find()) {
      String module = m.group(1);
      String line = m.group(2);
      if (modules.length() > 0) {
        modules.append("\n");
      }
      modules.append(" at line ").append(line).append(" of ").append(module);
    }
    if (modules.length() > 0) {
      String message = String.format("%s\n%s!", exMessage, modules.toString());
      return new SpellException(message, throwable);
    }
    return new SpellException(exMessage, throwable);
  }

  private String getExceptionMessage(Throwable top) {
    Throwable cause = top;
    while (cause != null && !(cause instanceof ParseException)) {
      cause = cause.getCause();
    }
    if (cause instanceof ParseException) {
      return cause.getMessage();
    }
    cause = top;
    while (cause != null && !(cause instanceof LuaRuntimeException)) {
      cause = cause.getCause();
    }
    if (cause instanceof LuaRuntimeException) {
      return cause.getMessage();
    }
    return top.getMessage();
  }

  private String getStackTrace(Throwable throwable) {
    StringWriter writer = new StringWriter();
    throwable.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }
}
