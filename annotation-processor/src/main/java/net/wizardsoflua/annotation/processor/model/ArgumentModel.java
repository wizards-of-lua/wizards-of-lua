package net.wizardsoflua.annotation.processor.model;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class ArgumentModel {
  public static ArgumentModel of(VariableElement element) {
    String name = element.getSimpleName().toString();
    TypeMirror type = element.asType();
    return new ArgumentModel(name, type);
  }

  private final String name;
  private final TypeMirror type;

  public ArgumentModel(String name, TypeMirror type) {
    this.name = checkNotNull(name, "name == null!");
    this.type = checkNotNull(type, "type == null!");
  }

  public String getName() {
    return name;
  }

  public TypeMirror getType() {
    return type;
  }
}
