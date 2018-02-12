package net.wizardsoflua.annotation.processor;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class ProcessorUtils {
  /**
   * Returns the type parameter of {@code superClass} in the context of {@code type} at the
   * specified {@code index} or {@code null} if {@code superClass} is not a super class of
   * {@code type}.
   * <p>
   * Example: For a type {@code StringList} which extends {@code AbstractList<String>}, a call like
   * {@code getParameterType(StringList, AbstractList, 0, types)} would return a {@link TypeMirror}
   * representing String.class.
   *
   * @param type
   * @param superClass
   * @param index
   * @param types
   * @return the type parameter of {@code superClass} in the context of {@code type} at the
   *         specified {@code index} or {@code null}
   */
  public static @Nullable TypeMirror getTypeParameter(DeclaredType type, String superClass,
      int index, ProcessingEnvironment env) {
    Types types = env.getTypeUtils();
    while (true) {
      TypeElement element = (TypeElement) type.asElement();
      if (element.getQualifiedName().contentEquals(superClass)) {
        return type.getTypeArguments().get(index);
      }
      Iterator<? extends TypeMirror> supertypes = types.directSupertypes(type).iterator();
      if (supertypes.hasNext()) {
        type = (DeclaredType) supertypes.next();
      } else {
        return null;
      }
    }
  }

  public static @Nullable DeclaredType getClassValue(AnnotationMirror mirror, String key,
      ProcessingEnvironment env) {
    Elements elements = env.getElementUtils();
    for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elements
        .getElementValuesWithDefaults(mirror).entrySet()) {
      if (key.equals(entry.getKey().getSimpleName().toString())) {
        return (DeclaredType) entry.getValue().getValue();
      }
    }
    return null;
  }

  public static @Nullable AnnotationMirror getAnnoationMirror(Element element,
      Class<? extends Annotation> annoationClass) {
    for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
      if (annoationClass.getName().equals(mirror.getAnnotationType().toString())) {
        return mirror;
      }
    }
    return null;
  }
}
