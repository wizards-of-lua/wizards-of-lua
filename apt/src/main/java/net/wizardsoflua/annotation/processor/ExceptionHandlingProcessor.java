package net.wizardsoflua.annotation.processor;

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
import javax.tools.Diagnostic;

import com.google.common.collect.Maps;

public abstract class ExceptionHandlingProcessor extends AbstractProcessor {
  private final Map<Entry<TypeElement, Element>, Exception> retryNextRound = new HashMap<>();

  @Override
  public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (!roundEnv.processingOver()) {
      Set<Entry<TypeElement, Element>> retryNow = new HashSet<>(retryNextRound.keySet());
      retryNextRound.clear();
      for (TypeElement annotation : annotations) {
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
        for (Element annotatedElement : annotatedElements) {
          process(annotation, annotatedElement, roundEnv);
        }
      }
      for (Entry<TypeElement, Element> entry : retryNow) {
        TypeElement annotation = entry.getKey();
        Element annotatedElement = entry.getValue();
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
    for (Entry<Entry<TypeElement, Element>, Exception> entry : retryNextRound.entrySet()) {
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
        message = message != null ? message : ex.toString();
        Element annotatedElement = entry.getKey().getValue();
        messager.printMessage(Diagnostic.Kind.ERROR, message, annotatedElement);
        ex.printStackTrace();
      }
    }
  }

  private void process(TypeElement annotation, Element annotatedElement,
      RoundEnvironment roundEnv) {
    try {
      doProcess(annotation, annotatedElement, roundEnv);
    } catch (Exception ex) {
      Entry<TypeElement, Element> entry = Maps.immutableEntry(annotation, annotatedElement);
      retryNextRound.put(entry, ex);
    }
  }

  protected abstract void doProcess(TypeElement annotation, Element annotatedElement,
      RoundEnvironment roundEnv) throws Exception;
}
