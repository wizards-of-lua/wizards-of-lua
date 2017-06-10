package net.wizardsoflua.spell;

public class SpellException extends Exception {

  private static final long serialVersionUID = 1L;

  public SpellException() {}

  public SpellException(String message) {
    super(message);
  }

  public SpellException(Throwable cause) {
    super(cause);
  }

  public SpellException(String message, Throwable cause) {
    super(message, cause);
  }

  public SpellException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
