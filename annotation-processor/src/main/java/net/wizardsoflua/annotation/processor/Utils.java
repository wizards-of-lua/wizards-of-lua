package net.wizardsoflua.annotation.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class Utils {
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

  /**
   * <p>
   * Capitalizes a String changing the first letter to title case as per
   * {@link Character#toTitleCase(char)}. No other letters are changed.
   * </p>
   *
   * <p>
   * For a word based algorithm, see
   * {@link org.apache.commons.lang3.text.WordUtils#capitalize(String)}. A {@code null} input String
   * returns {@code null}.
   * </p>
   *
   * <pre>
   * StringUtils.capitalize(null)  = null
   * StringUtils.capitalize("")    = ""
   * StringUtils.capitalize("cat") = "Cat"
   * StringUtils.capitalize("cAt") = "CAt"
   * </pre>
   *
   * @param str the String to capitalize, may be null
   * @return the capitalized String, {@code null} if null String input
   * @see org.apache.commons.lang3.text.WordUtils#capitalize(String)
   * @see #uncapitalize(String)
   * @since 2.0
   */
  public static String capitalize(final String str) {
    int strLen;
    if (str == null || (strLen = str.length()) == 0) {
      return str;
    }

    char firstChar = str.charAt(0);
    if (Character.isTitleCase(firstChar)) {
      // already capitalized
      return str;
    }

    return new StringBuilder(strLen).append(Character.toTitleCase(firstChar))
        .append(str.substring(1)).toString();
  }

}
