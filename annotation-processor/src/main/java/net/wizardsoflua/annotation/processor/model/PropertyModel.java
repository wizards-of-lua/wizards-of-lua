package net.wizardsoflua.annotation.processor.model;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import javax.annotation.Nullable;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.google.common.base.Strings;

import net.wizardsoflua.annotation.LuaProperty;

public class PropertyModel {
  public static PropertyModel of(ExecutableElement method, LuaProperty luaProperty,
      @Nullable String docComment) {
    String methodName = method.getSimpleName().toString();
    String name = luaProperty.name();
    String getterName = null;
    String setterName = null;
    TypeMirror type;
    if (isGetter(method)) {
      if (name.isEmpty()) {
        name = extractPropertyNameFromGetter(methodName);
      }
      type = method.getReturnType();
      getterName = methodName;
    } else if (isSetter(method)) {
      if (name.isEmpty()) {
        name = extractPropertyNameFromSetter(methodName);
      }
      type = method.getParameters().get(0).asType();
      setterName = methodName;
    } else {
      throw new IllegalArgumentException("method " + method + " is neither a getter nor a setter");
    }
    String description = Strings.nullToEmpty(docComment).trim();
    return new PropertyModel(name, getterName, setterName, type, description);
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

  public static String typeToString(TypeMirror typeMirror) {
    TypeKind typeKind = typeMirror.getKind();
    switch (typeKind) {
      case ARRAY:
        return "table";
      case BOOLEAN:
        return "boolean";
      case BYTE:
      case DOUBLE:
      case FLOAT:
      case INT:
      case LONG:
      case SHORT:
        return "number (" + typeKind.toString().toLowerCase() + ")";
      case CHAR:
        return "string";
      case DECLARED:
        Name simpleName = ((DeclaredType) typeMirror).asElement().getSimpleName();
        return "[" + simpleName + "](!SITE_URL!/modules/" + simpleName + "/)";
      case EXECUTABLE:
      case ERROR:
      case INTERSECTION:
      case NONE:
      case NULL:
      case OTHER:
      case PACKAGE:
      case TYPEVAR:
      case UNION:
      case WILDCARD:
      default:
        throw new IllegalArgumentException("Unknown type: " + typeMirror);
    }
  }

  private final String name;
  private final TypeMirror type;
  private @Nullable String getterName;
  private @Nullable String setterName;
  private String description;

  public PropertyModel(String name, @Nullable String getterName, @Nullable String setterName,
      TypeMirror type, String description) {
    this.name = requireNonNull(name, "name == null!");
    this.type = requireNonNull(type, "type == null!");
    this.getterName = getterName;
    this.setterName = setterName;
    this.description = requireNonNull(description, "description == null!");
  }

  public String getName() {
    return name;
  }

  public TypeMirror getType() {
    return type;
  }

  public @Nullable String getGetterName() {
    return getterName;
  }

  public @Nullable String getSetterName() {
    return setterName;
  }

  public boolean isReadable() {
    return getterName != null;
  }

  public boolean isWriteable() {
    return setterName != null;
  }

  public void merge(PropertyModel property) {
    checkArgument(this.name.equals(property.name));
    checkArgument(this.getterName == null || property.getterName == null
        || this.getterName.equals(property.getterName));
    checkArgument(this.setterName == null || property.setterName == null
        || this.setterName.equals(property.setterName));

    checkArgument(this.getType().equals(property.getType()));
    checkArgument(this.description.isEmpty() || property.description.isEmpty()
        || this.description.equals(property.description));

    this.getterName = this.getterName == null ? property.getterName : this.getterName;
    this.setterName = this.setterName == null ? property.setterName : this.setterName;
    this.description = this.description.isEmpty() ? property.description : this.description;
  }

  private String renderName() {
    return name;
  }

  private String renderType() {
    return typeToString(getType());
  }

  private String renderAccess() {
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
