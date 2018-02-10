package net.wizardsoflua.lua;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nullable;

public class BadArgumentException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private final String message;
  private @Nullable Integer argumentIndex;
  private @Nullable String argumentName;
  private @Nullable String functionOrPropertyName;

  public BadArgumentException(String expected, String actual) {
    this("expected " + expected + " but got " + actual);
  }

  public BadArgumentException(String message) {
    this.message = requireNonNull(message, "message == null!");
  }

  public void setArgumentIndex(int argumentIndex) {
    this.argumentIndex = argumentIndex;
  }

  public void setArgumentName(String argumentName) {
    this.argumentName = requireNonNull(argumentName, "argumentName == null!");
  }

  public void setFunctionOrPropertyName(String functionOrPropertyName) {
    this.functionOrPropertyName =
        requireNonNull(functionOrPropertyName, "functionOrPropertyName == null!");
  }

  @Override
  public String getMessage() {
    StringBuilder sb = new StringBuilder();
    sb.append("bad argument");
    if (argumentIndex != null) {
      sb.append(" #").append(argumentIndex);
    }
    if (argumentName != null) {
      sb.append(" (").append(argumentName).append(')');
    }
    if (functionOrPropertyName != null) {
      sb.append(" to '").append(functionOrPropertyName).append('\'');
    }
    sb.append(": ").append(message);
    return sb.toString();
  }
}
