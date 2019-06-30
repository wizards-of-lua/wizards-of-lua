package net.wizardsoflua.annotation.processor;

import static javax.tools.Diagnostic.Kind.ERROR;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

public abstract class ExceptionHandlingProcessor extends AbstractProcessor {
  private final Map<Entry<TypeElement, TypeElement>, Exception> retryNextRound = new HashMap<>();

  @Override
  public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (!roundEnv.processingOver()) {
      Set<Entry<TypeElement, TypeElement>> retryNow = new HashSet<>(retryNextRound.keySet());
      retryNextRound.clear();
      for (TypeElement annotation : annotations) {
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
        for (TypeElement annotatedElement : ElementFilter.typesIn(annotatedElements)) {
          process(annotation, annotatedElement, roundEnv);
        }
      }
      for (Entry<TypeElement, TypeElement> entry : retryNow) {
        TypeElement annotation = entry.getKey();
        TypeElement annotatedElement = entry.getValue();
        Elements elements = processingEnv.getElementUtils();
        annotatedElement = elements.getTypeElement(annotatedElement.getQualifiedName());
        process(annotation, annotatedElement, roundEnv);
      }
    } else {
      processingOver();
      logExceptions();
    }
    return areAnnotationsClaimed(annotations);
  }

  protected boolean areAnnotationsClaimed(Set<? extends TypeElement> annotations) {
    return false;
  }

  protected void processingOver() {}

  private void logExceptions() {
    for (Entry<Entry<TypeElement, TypeElement>, Exception> entry : retryNextRound.entrySet()) {
      Exception ex = entry.getValue();
      Messager messager = processingEnv.getMessager();
      if (ex instanceof ProcessingException) {
        ((ProcessingException) ex).printTo(messager);
      } else if (ex instanceof MultipleProcessingExceptions) {
        for (ProcessingException pex : ((MultipleProcessingExceptions) ex).getExceptions()) {
          pex.printTo(messager);
        }
      } else {
        CharSequence message = ex.getMessage();
        if (message == null) {
          message = Throwables.getStackTraceAsString(ex);
        }
        Element annotatedElement = entry.getKey().getValue();
        if (annotatedElement == null) {
          messager.printMessage(ERROR, message);
        } else {
          messager.printMessage(ERROR, message, annotatedElement);
        }
        ex.printStackTrace();
      }
    }
  }

  private void process(TypeElement annotation, TypeElement annotatedElement,
      RoundEnvironment roundEnv) {
    try {
      doProcess(annotation, annotatedElement, roundEnv);
    } catch (Exception ex) {
      Entry<TypeElement, TypeElement> entry = Maps.immutableEntry(annotation, annotatedElement);
      retryNextRound.put(entry, ex);
    }
  }

  protected abstract void doProcess(TypeElement annotation, TypeElement annotatedElement,
      RoundEnvironment roundEnv) throws Exception;
}
