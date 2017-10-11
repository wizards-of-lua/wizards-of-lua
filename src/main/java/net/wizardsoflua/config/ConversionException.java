package net.wizardsoflua.config;

public class ConversionException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ConversionException() {}

  public ConversionException(String message) {
    super(message);
  }

  public ConversionException(Throwable cause) {
    super(cause);
  }

  public ConversionException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConversionException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
