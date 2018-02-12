package net.wizardsoflua.annotation.processor.model;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.google.common.base.Strings;

import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.annotation.processor.generator.LuaDocGenerator;

public class PropertyModel {
  public static PropertyModel of(ExecutableElement method, LuaProperty luaProperty,
      @Nullable String docComment, ProcessingEnvironment env) {
    String methodName = method.getSimpleName().toString();
    String name = luaProperty.name();
    boolean nullable = false;
    String getterName = null;
    String setterName = null;
    String type = LuaDocGenerator.toReference(luaProperty.type());
    TypeMirror setterType = null;
    if (isGetter(method)) {
      if (name.isEmpty()) {
        name = extractPropertyNameFromGetter(methodName);
      }
      if (type.isEmpty()) {
        type = LuaDocGenerator.renderType(method.getReturnType(), env);
      }
      getterName = methodName;
    } else if (isSetter(method)) {
      if (name.isEmpty()) {
        name = extractPropertyNameFromSetter(methodName);
      }
      VariableElement parameter = method.getParameters().get(0);
      nullable = parameter.getAnnotation(Nullable.class) != null;
      setterType = parameter.asType();
      if (type.isEmpty()) {
        type = LuaDocGenerator.renderType(setterType, env);
      }
      setterName = methodName;
    } else {
      throw new IllegalArgumentException("method " + method + " is neither a getter nor a setter");
    }
    String description = Strings.nullToEmpty(docComment).trim();
    return new PropertyModel(name, type, description, nullable, getterName, setterName, setterType);
  }

  public static boolean isGetter(ExecutableElement method) {
    String methodName = method.getSimpleName().toString();
    return (methodName.startsWith(GET) || methodName.startsWith(IS))//
        && method.getReturnType().getKind() != TypeKind.VOID && method.getParameters().isEmpty();
  }

  public static boolean isSetter(ExecutableElement method) {
    String methodName = method.getSimpleName().toString();
    return methodName.startsWith(SET)//
        && method.getReturnType().getKind() == TypeKind.VOID && method.getParameters().size() == 1;
  }

  private static final String GET = "get";
  private static final int GET_LENGTH = GET.length();
  private static final String IS = "is";
  private static final int IS_LENGTH = IS.length();

  public static String extractPropertyNameFromGetter(String methodName) {
    if (methodName.startsWith(GET) && methodName.length() > GET_LENGTH) {
      char firstChar = methodName.charAt(GET_LENGTH);
      if (Character.isUpperCase(firstChar)) {
        return Character.toLowerCase(firstChar) + methodName.substring(GET_LENGTH + 1);
      }
    } else {
      if (methodName.startsWith(IS) && methodName.length() > IS_LENGTH) {
        char firstChar = methodName.charAt(IS_LENGTH);
        if (Character.isUpperCase(firstChar)) {
          return Character.toLowerCase(firstChar) + methodName.substring(IS_LENGTH + 1);
        }
      }
    }
    throw new IllegalArgumentException("'" + methodName + "' is not a name of a getter method");
  }

  private static final String SET = "set";
  private static final int SET_LENGTH = SET.length();

  public static String extractPropertyNameFromSetter(String methodName) {
    if (methodName.startsWith(SET) && methodName.length() > SET_LENGTH) {
      char firstChar = methodName.charAt(SET_LENGTH);
      if (Character.isUpperCase(firstChar)) {
        return Character.toLowerCase(firstChar) + methodName.substring(SET_LENGTH + 1);
      }
    }
    throw new IllegalArgumentException("'" + methodName + "' is not a name of a setter method");
  }

  private final String name;
  private final String type;
  private String description;
  private boolean nullable;
  private @Nullable String getterName;
  private @Nullable String setterName;
  private @Nullable TypeMirror setterType;

  public PropertyModel(String name, String type, String description, boolean nullable,
      @Nullable String getterName, @Nullable String setterName, @Nullable TypeMirror setterType) {
    this.name = requireNonNull(name, "name == null!");
    this.type = requireNonNull(type, "type == null!");
    this.description = requireNonNull(description, "description == null!");
    this.nullable = nullable;
    this.getterName = getterName;
    this.setterName = setterName;
    this.setterType = setterType;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public @Nullable String getGetterName() {
    return getterName;
  }

  public boolean isNullable() {
    return nullable;
  }

  public @Nullable String getSetterName() {
    return setterName;
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

  public String getDescription() {
    return description;
  }

  public void merge(PropertyModel other) {
    checkArgument(name.equals(other.name), "Cannot merge properties with different names");
    checkArgument(getterName == null || other.getterName == null, "duplicate property '%s'", name);
    checkArgument(setterName == null || other.setterName == null, "duplicate property '%s'", name);
    checkArgument(type.equals(other.type), "setter and getter are of different types", name);

    checkArgument(
        description.isEmpty() || other.description.isEmpty()
            || description.equals(other.description),
        "The description of property '%s' on the getter differs from the description on the setter",
        name);

    nullable |= other.nullable;
    getterName = getterName == null ? other.getterName : getterName;
    setterName = setterName == null ? other.setterName : setterName;
    setterType = setterType == null ? other.setterType : setterType;
    description = description.isEmpty() ? other.description : description;
  }

  public String renderAccess() {
    if (isReadable()) {
      if (isWriteable()) {
        return "r/w";
      } else {
        return "r";
      }
    } else {
      if (isWriteable()) {
        return "w";
      } else {
        throw new IllegalStateException("Property must be readable or writeable");
      }
    }
  }
}
