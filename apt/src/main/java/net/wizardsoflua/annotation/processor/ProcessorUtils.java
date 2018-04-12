package net.wizardsoflua.annotation.processor;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.lang.model.type.TypeKind.DECLARED;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.squareup.javapoet.JavaFile;

import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.annotation.LuaProperty;

public class ProcessorUtils {
  public static <A extends Annotation> A checkAnnotated(Element element, Class<A> annotationClass) {
    A annotation = element.getAnnotation(annotationClass);
    String kind = element.getKind().toString().toLowerCase();
    checkArgument(annotation != null, "%s %s is not annotated with @%s", kind, element,
        LuaProperty.class.getSimpleName());
    return annotation;
  }

  /**
   * Returns the type parameter of {@code superType} in the context of {@code type} at the specified
   * {@code index} or {@code null} if {@code superType} is not a super type of {@code type}.
   * <p>
   * Example: For a type {@code StringList} which implements {@code List<String>}, a call like
   * {@code getParameterType(StringList, List, 0, types)} would return a {@link TypeMirror}
   * representing String.class.
   *
   * @param type
   * @param superType
   * @param index
   * @param types
   * @return the type parameter of {@code superClass} in the context of {@code type} at the
   *         specified {@code index} or {@code null}
   */
  public static @Nullable TypeMirror getTypeParameter(DeclaredType type, String superType,
      int index, ProcessingEnvironment env) {
    Types types = env.getTypeUtils();
    Deque<TypeMirror> todos = new ArrayDeque<>();
    todos.add(type);
    while (!todos.isEmpty()) {
      TypeMirror todo = todos.pop();
      if (todo.getKind() == TypeKind.DECLARED) {
        DeclaredType declaredTodo = (DeclaredType) todo;
        TypeElement todoElement = (TypeElement) declaredTodo.asElement();
        if (todoElement.getQualifiedName().contentEquals(superType)) {
          return declaredTodo.getTypeArguments().get(index);
        }
      }
      todos.addAll(types.directSupertypes(todo));
    }
    return null;
  }

  public static @Nullable DeclaredType getClassValue(AnnotationMirror mirror, String key,
      ProcessingEnvironment env) {
    AnnotationValue value = getAnnotationValue(mirror, key, env);
    if (value != null) {
      return (DeclaredType) value.getValue();
    }
    return null;
  }

  public static @Nullable AnnotationValue getAnnotationValue(AnnotationMirror mirror, String key,
      ProcessingEnvironment env) {
    Elements elements = env.getElementUtils();
    for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elements
        .getElementValuesWithDefaults(mirror).entrySet()) {
      if (entry.getKey().getSimpleName().contentEquals(key)) {
        return entry.getValue();
      }
    }
    return null;
  }

  public static @Nullable AnnotationMirror getAnnotationMirror(
      AnnotatedConstruct annotatedConstruct, Class<? extends Annotation> annoationClass) {
    return getAnnotationMirror(annotatedConstruct, annoationClass.getName());
  }

  public @Nullable static AnnotationMirror getAnnotationMirror(
      AnnotatedConstruct annotatedConstruct, String annotationClassName) {
    for (AnnotationMirror mirror : annotatedConstruct.getAnnotationMirrors()) {
      TypeElement annotationElement = (TypeElement) mirror.getAnnotationType().asElement();
      if (annotationElement.getQualifiedName().contentEquals(annotationClassName)) {
        return mirror;
      }
    }
    return null;
  }

  public static Set<TypeMirror> getAllSuperTypes(TypeMirror typeMirror, ProcessingEnvironment env) {
    Types types = env.getTypeUtils();
    Set<TypeMirror> result = new HashSet<>();
    Deque<TypeMirror> todos = new ArrayDeque<>();
    todos.add(typeMirror);
    while (!todos.isEmpty()) {
      TypeMirror todo = todos.pop();
      if (result.add(todo)) {
        List<? extends TypeMirror> directSupertypes = types.directSupertypes(todo);
        todos.addAll(directSupertypes);
      }
    }
    return result;
  }

  public static boolean isJavaLangObject(TypeMirror typeMirror) {
    if (typeMirror.getKind() == DECLARED) {
      DeclaredType declaredType = (DeclaredType) typeMirror;
      TypeElement typeElement = (TypeElement) declaredType.asElement();
      return typeElement.getQualifiedName().contentEquals(java.lang.Object.class.getName());
    }
    return false;
  }

  public static boolean isLuaType(TypeMirror typeMirror, ProcessingEnvironment env) {
    TypeKind kind = typeMirror.getKind();
    if (kind.isPrimitive()) {
      return kind != TypeKind.CHAR;
    }
    // Using getAllSuperTypes, because Types.isSubType() does not work across processing rounds
    for (TypeMirror superType : getAllSuperTypes(typeMirror, env)) {
      if (superType.getKind() == TypeKind.DECLARED) {
        TypeElement superElement = (TypeElement) ((DeclaredType) superType).asElement();
        Name qualifiedName = superElement.getQualifiedName();
        if (qualifiedName.contentEquals(Table.class.getName())//
            || qualifiedName.contentEquals(ByteString.class.getName())//
            || qualifiedName.contentEquals(Number.class.getName())//
            || qualifiedName.contentEquals(Boolean.class.getName())//
            || qualifiedName.contentEquals(LuaFunction.class.getName())//
        ) {
          return true;
        }
      }
    }
    return false;
  }

  public static void write(JavaFile file, Filer filer) throws IOException {
    String qualifiedName = file.packageName + '.' + file.typeSpec.name;
    try (Writer writer = new BufferedWriter(filer.createSourceFile(qualifiedName).openWriter())) {
      file.writeTo(writer);
    }
  }
}
