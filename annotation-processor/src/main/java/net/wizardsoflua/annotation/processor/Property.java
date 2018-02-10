package net.wizardsoflua.annotation.processor;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import javax.annotation.Nullable;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import com.google.common.base.Strings;

import net.wizardsoflua.annotation.LuaProperty;

public class Property {
  public static Property of(ExecutableElement method, LuaProperty luaProperty,
      @Nullable String docComment) {
    String methodName = method.getSimpleName().toString();
    String name = luaProperty.name();
    TypeMirror type;
    boolean readable = false;
    boolean writeable = false;
    if (Utils.isGetter(method)) {
      if (name.isEmpty()) {
        name = Utils.extractPropertyNameFromGetter(methodName);
      }
      type = method.getReturnType();
      readable = true;
    } else if (Utils.isSetter(method)) {
      if (name.isEmpty()) {
        name = Utils.extractPropertyNameFromSetter(methodName);
      }
      type = method.getParameters().get(0).asType();
      writeable = true;
    } else {
      throw new IllegalArgumentException("method " + method + " is neither a getter nor a setter");
    }
    String description = Strings.nullToEmpty(docComment).trim();
    return new Property(name, type, readable, writeable, description);
  }

  private final String name;
  private final TypeMirror type;
  private boolean readable;
  private boolean writeable;
  private String description;

  @Deprecated
  public Property(String name, TypeMirror type, String description) {
    this.name = requireNonNull(name, "name == null!");
    this.type = requireNonNull(type, "type == null!");
    this.description = requireNonNull(description, "description == null!");
  }

  public Property(String name, TypeMirror type, boolean readable, boolean writeable,
      String description) {
    this.name = requireNonNull(name, "name == null!");
    this.type = requireNonNull(type, "type == null!");
    this.readable = readable;
    this.writeable = writeable;
    this.description = requireNonNull(description, "description == null!");
  }

  public String getName() {
    return name;
  }

  public TypeMirror getType() {
    return type;
  }

  public boolean isReadable() {
    return readable;
  }

  public void setReadable(boolean readable) {
    this.readable = readable;
  }

  public boolean isWriteable() {
    return writeable;
  }

  public void setWriteable(boolean writeable) {
    this.writeable = writeable;
  }

  public void merge(Property property) {
    checkArgument(this.name.equals(property.name));
    checkArgument(this.getType().equals(property.getType()));
    checkArgument(this.description.isEmpty() || property.description.isEmpty()
        || this.description.equals(property.description));
    this.readable |= property.readable;
    this.writeable |= property.writeable;
    this.description = this.description.isEmpty() ? property.description : this.description;
  }

  private String renderName() {
    return name;
  }

  private String renderType() {
    return Utils.typeToString(getType());
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
