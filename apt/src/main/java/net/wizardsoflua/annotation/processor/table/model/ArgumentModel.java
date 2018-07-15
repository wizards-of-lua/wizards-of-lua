package net.wizardsoflua.annotation.processor.table.model;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nullable;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class ArgumentModel {
  public static ArgumentModel of(VariableElement element) {
    String name = element.getSimpleName().toString();
    TypeMirror type = element.asType();
    boolean nullable = element.getAnnotation(Nullable.class) != null;
    return new ArgumentModel(name, type, nullable);
  }

  private final String name;
  private final TypeMirror type;
  private final boolean nullable;

  public ArgumentModel(String name, TypeMirror type, boolean nullable) {
    this.name = requireNonNull(name, "name == null!");
    this.type = requireNonNull(type, "type == null!");
    this.nullable = requireNonNull(nullable, "nullable == null!");
  }

  public String getName() {
    return name;
  }

  public TypeMirror getType() {
    return type;
  }

  public boolean isNullable() {
    return nullable;
  }
}
