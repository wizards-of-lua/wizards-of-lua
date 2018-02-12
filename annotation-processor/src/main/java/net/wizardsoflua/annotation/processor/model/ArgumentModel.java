package net.wizardsoflua.annotation.processor.model;

import static com.google.common.base.Preconditions.checkNotNull;

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
    this.name = checkNotNull(name, "name == null!");
    this.type = checkNotNull(type, "type == null!");
    this.nullable = checkNotNull(nullable, "nullable == null!");
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
