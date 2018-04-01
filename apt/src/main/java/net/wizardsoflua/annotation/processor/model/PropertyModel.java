package net.wizardsoflua.annotation.processor.model;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static net.wizardsoflua.annotation.processor.LuaPropertyUtils.isGetter;
import static net.wizardsoflua.annotation.processor.LuaPropertyUtils.isSetter;

import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import net.wizardsoflua.annotation.processor.LuaPropertyUtils;
import net.wizardsoflua.annotation.processor.ProcessingException;

public class PropertyModel {
  public static PropertyModel of(ExecutableElement method, ProcessingEnvironment env)
      throws ProcessingException {
    String name = LuaPropertyUtils.getPropertyName(method);
    String methodName = method.getSimpleName().toString();
    if (isGetter(method)) {
      boolean nullable = false;
      String getterName = methodName;
      String setterName = null;
      TypeMirror getterType = method.getReturnType();
      TypeMirror setterType = null;
      return new PropertyModel(name, nullable, getterName, setterName, getterType, setterType);
    } else if (isSetter(method)) {
      VariableElement parameter = method.getParameters().get(0);
      boolean nullable = parameter.getAnnotation(Nullable.class) != null;
      String getterName = null;
      String setterName = methodName;
      TypeMirror getterType = null;
      TypeMirror setterType = parameter.asType();
      return new PropertyModel(name, nullable, getterName, setterName, getterType, setterType);
    } else {
      throw LuaPropertyUtils.neitherGetterNorSetter(method);
    }
  }

  private final String name;
  private boolean nullable;
  private @Nullable String getterName;
  private @Nullable String setterName;
  private @Nullable TypeMirror getterType;
  private @Nullable TypeMirror setterType;

  public PropertyModel(String name, boolean nullable, @Nullable String getterName,
      @Nullable String setterName, @Nullable TypeMirror getterType,
      @Nullable TypeMirror setterType) {
    this.name = requireNonNull(name, "name == null!");
    this.nullable = nullable;
    this.getterName = getterName;
    this.setterName = setterName;
    this.getterType = getterType;
    this.setterType = setterType;
  }

  public String getName() {
    return name;
  }

  public boolean isNullable() {
    return nullable;
  }

  public @Nullable String getGetterName() {
    return getterName;
  }

  public @Nullable String getSetterName() {
    return setterName;
  }

  public @Nullable TypeMirror getGetterType() {
    return getterType;
  }

  public @Nullable TypeMirror getSetterType() {
    return setterType;
  }

  public boolean isReadable() {
    return getterName != null;
  }

  public boolean isWriteable() {
    return setterName != null;
  }

  public void merge(PropertyModel other) {
    checkArgument(name.equals(other.name), "Cannot merge properties with different names");
    checkArgument(getterName == null || other.getterName == null, "duplicate property '%s'", name);
    checkArgument(setterName == null || other.setterName == null, "duplicate property '%s'", name);
    nullable |= other.nullable;
    getterName = getterName == null ? other.getterName : getterName;
    setterName = setterName == null ? other.setterName : setterName;
    getterType = getterType == null ? other.getterType : getterType;
    setterType = setterType == null ? other.setterType : setterType;
  }
}
