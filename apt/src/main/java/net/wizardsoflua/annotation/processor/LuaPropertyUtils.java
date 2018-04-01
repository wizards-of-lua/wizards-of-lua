package net.wizardsoflua.annotation.processor;

import static net.wizardsoflua.annotation.processor.ProcessorUtils.checkAnnotated;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAnnotationMirror;
import static net.wizardsoflua.annotation.processor.doc.model.PropertyAccess.READONLY;
import static net.wizardsoflua.annotation.processor.doc.model.PropertyAccess.WRITEONLY;

import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.annotation.processor.doc.generator.LuaDocGenerator;
import net.wizardsoflua.annotation.processor.doc.model.PropertyAccess;

public class LuaPropertyUtils {
  private static final String GET = "get";
  private static final int GET_LENGTH = GET.length();
  private static final String IS = "is";
  private static final int IS_LENGTH = IS.length();

  private static final String SET = "set";
  private static final int SET_LENGTH = SET.length();

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

  public static String getPropertyName(ExecutableElement method) throws ProcessingException {
    LuaProperty annotation = checkAnnotated(method, LuaProperty.class);
    String name = annotation.name();
    if (!name.isEmpty()) {
      return name;
    }
    String methodName = method.getSimpleName().toString();
    if (isGetter(method)) {
      return extractPropertyNameFromGetter(methodName);
    }
    if (isSetter(method)) {
      return extractPropertyNameFromSetter(methodName);
    }
    throw neitherGetterNorSetter(method);
  }

  private static String extractPropertyNameFromGetter(String methodName) {
    if (methodName.startsWith(GET) && methodName.length() > GET_LENGTH) {
      char firstChar = methodName.charAt(GET_LENGTH);
      return Character.toLowerCase(firstChar) + methodName.substring(GET_LENGTH + 1);
    }
    if (methodName.startsWith(IS) && methodName.length() > IS_LENGTH) {
      char firstChar = methodName.charAt(IS_LENGTH);
      return Character.toLowerCase(firstChar) + methodName.substring(IS_LENGTH + 1);
    }
    return methodName;
  }

  private static String extractPropertyNameFromSetter(String methodName) {
    if (methodName.startsWith(SET) && methodName.length() > SET_LENGTH) {
      char firstChar = methodName.charAt(SET_LENGTH);
      return Character.toLowerCase(firstChar) + methodName.substring(SET_LENGTH + 1);
    }
    return methodName;
  }

  public static String getPropertyType(ExecutableElement method, Map<String, String> luaClassNames,
      ProcessingEnvironment env) throws ProcessingException {
    LuaProperty luaProperty = checkAnnotated(method, LuaProperty.class);
    String type = LuaDocGenerator.renderType(luaProperty.type());
    if (!type.isEmpty()) {
      return type;
    }
    if (isGetter(method)) {
      TypeMirror returnType = method.getReturnType();
      return LuaDocGenerator.renderType(returnType, method, luaClassNames, env);
    }
    if (isSetter(method)) {
      TypeMirror parameterType = method.getParameters().get(0).asType();
      return LuaDocGenerator.renderType(parameterType, method, luaClassNames, env);
    }
    throw neitherGetterNorSetter(method);
  }

  public static PropertyAccess getPropertyAccess(ExecutableElement method)
      throws ProcessingException {
    if (isGetter(method)) {
      return READONLY;
    }
    if (isSetter(method)) {
      return WRITEONLY;
    }
    throw neitherGetterNorSetter(method);
  }

  public static ProcessingException neitherGetterNorSetter(ExecutableElement method) {
    CharSequence msg =
        "@" + LuaProperty.class.getSimpleName() + " is only allowed on getter/setter methods";
    AnnotationMirror a = getAnnotationMirror(method, LuaProperty.class);
    return new ProcessingException(msg, method, a);
  }
}
