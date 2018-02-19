package net.wizardsoflua.annotation.processor.doc.model;

import static java.util.Objects.requireNonNull;

public enum PropertyAccess {
  READONLY("r"), WRITEONLY("w"), READWRITE("r/w");
  private final String string;

  private PropertyAccess(String string) {
    this.string = requireNonNull(string, "string == null!");
  }

  @Override
  public String toString() {
    return string;
  }
}
