package net.wizardsoflua.lua;

import static java.util.Objects.requireNonNull;
import javax.annotation.Nullable;

public class BadArgumentException extends IllegalArgumentException {
  private static final long serialVersionUID = 1L;

  private String detailMessage;
  private @Nullable Integer argumentIndex;
  private @Nullable String argumentName;
  private @Nullable String functionOrPropertyName;

  public BadArgumentException(String expected, String actual) {
    this("expected " + expected + " but got " + actual);
  }

  public BadArgumentException(String detailMessage) {
    setDetailMessage(detailMessage);
  }

  public BadArgumentException(String detailMessage, int argumentIndex, String argumentName,
      String functionOrPropertyName) {
    setDetailMessage(detailMessage);
    setArgumentIndex(argumentIndex);
    setArgumentName(argumentName);
    setFunctionOrPropertyName(functionOrPropertyName);
  }

  /**
   * @return the value of {@link #detailMessage}
   */
  public String getDetailMessage() {
    return detailMessage;
  }

  /**
   * @param detailMessage the new value for {@link #detailMessage}
   * @return
   */
  public BadArgumentException setDetailMessage(String detailMessage) {
    this.detailMessage = requireNonNull(detailMessage, "detailMessage == null!");
    return this;
  }

  public BadArgumentException setArgumentIndex(int argumentIndex) {
    this.argumentIndex = argumentIndex;
    return this;
  }

  public BadArgumentException setArgumentName(String argumentName) {
    this.argumentName = requireNonNull(argumentName, "argumentName == null!");
    return this;
  }

  public BadArgumentException setFunctionOrPropertyName(String functionOrPropertyName) {
    this.functionOrPropertyName =
        requireNonNull(functionOrPropertyName, "functionOrPropertyName == null!");
    return this;
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
    sb.append(": ").append(detailMessage);
    return sb.toString();
  }
}
