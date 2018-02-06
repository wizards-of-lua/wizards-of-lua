package net.wizardsoflua.annotation.lua.doc.jekyll;

import static java.util.Objects.requireNonNull;

public class Property {
  private final String name;
  private final String type;
  private boolean readable;
  private boolean writeable;
  private String description;

  public Property(String name, String type, String description) {
    this.name = requireNonNull(name, "name == null!");
    this.type = requireNonNull(type, "type == null!");
    this.description = requireNonNull(description, "description == null!");
  }

  public void setReadable(boolean readable) {
    this.readable = readable;
  }

  public void setWriteable(boolean writeable) {
    this.writeable = writeable;
  }

  private String renderName() {
    return name;
  }

  private String renderType() {
    return type;
  }

  private String renderAccess() {
    if (readable) {
      if (writeable) {
        return "r/w";
      } else {
        return "r";
      }
    } else {
      if (writeable) {
        return "w";
      } else {
        throw new IllegalStateException("Property must be readable or writeable");
      }
    }
  }

  private String renderDescription() {
    if (description.contains("\n")) {
      return '"' + description + '"';
    } else {
      return description;
    }
  }

  @Override
  public String toString() {
    return "  - name: " + renderName() //
        + "\n    type: " + renderType() //
        + "\n    access: " + renderAccess()//
        + "\n    description: " + renderDescription();
  }
}
