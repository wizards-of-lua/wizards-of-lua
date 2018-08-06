package net.wizardsoflua.annotation.processor;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.lang.model.type.TypeKind.DECLARED;

import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;

import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.LuaFunction;

public class ProcessorUtils {
  public static <A extends Annotation> A checkAnnotated(Element element, Class<A> annotationClass)
      throws IllegalArgumentException {
    A annotation = element.getAnnotation(annotationClass);
    String kind = element.getKind().toString().toLowerCase();
    checkArgument(annotation != null, "%s %s is not annotated with @%s", kind, element,
        annotationClass.getSimpleName());
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
   * @param superTypeName
   * @param index
   * @param env
   * @return the type parameter of {@code superTypeName} in the context of {@code type} at the
   *         specified {@code index} or {@code null}
   */
  public static @Nullable TypeMirror getTypeParameter(TypeMirror type, String superTypeName,
      int index, ProcessingEnvironment env) {
    Set<TypeMirror> superTypes = getAllSuperTypes(type, env);
    for (TypeMirror superType : superTypes) {
      TypeKind kind = superType.getKind();
      if (kind == TypeKind.DECLARED || kind == TypeKind.ERROR) {
        DeclaredType declaredSuperType = (DeclaredType) superType;
        TypeElement superElement = (TypeElement) declaredSuperType.asElement();
        if (superElement.getQualifiedName().contentEquals(superTypeName)) {
          List<? extends TypeMirror> typeArguments = declaredSuperType.getTypeArguments();
          return typeArguments.get(index);
        }
      }
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

  public static @Nullable AnnotationMirror getAnnotationMirror(
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

  /**
   * Alternative to Types.isSubType(), because that does not work across processing rounds.
   *
   * @param type
   * @param superTypeName
   * @param env
   * @return {@code true} if {@code type} is a sub type of {@code superTypeName}, {@code false}
   *         otherwise
   * @see Types#isSubtype(TypeMirror, TypeMirror)
   */
  public static boolean isSubType(TypeMirror type, String superTypeName,
      ProcessingEnvironment env) {
    for (TypeMirror superType : getAllSuperTypes(type, env)) {
      if (superType.getKind() == TypeKind.DECLARED) {
        TypeElement superElement = (TypeElement) ((DeclaredType) superType).asElement();
        if (superElement.getQualifiedName().contentEquals(superTypeName)) {
          return true;
        }
      }
    }
    return false;
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

  public static DeclaredType getUpperBound(TypeMirror type) {
    return type.accept(new SimpleTypeVisitor8<DeclaredType, Void>() {
      @Override
      public DeclaredType visitDeclared(DeclaredType t, Void p) {
        return t;
      }

      @Override
      public DeclaredType visitError(ErrorType t, Void p) {
        return t;
      }

      @Override
      public DeclaredType visitIntersection(IntersectionType t, Void p) {
        return t.getBounds().iterator().next().accept(this, p);
      }

      @Override
      public DeclaredType visitTypeVariable(TypeVariable t, Void p) {
        return t.getUpperBound().accept(this, p);
      }

      @Override
      public DeclaredType visitWildcard(WildcardType t, Void p) {
        return t.getExtendsBound().accept(this, p);
      }
    }, null);
  }
}
