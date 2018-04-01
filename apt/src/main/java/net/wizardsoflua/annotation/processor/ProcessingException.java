package net.wizardsoflua.annotation.processor;

import static java.util.Objects.requireNonNull;
import static javax.tools.Diagnostic.Kind.ERROR;

import javax.annotation.Nullable;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;

public class ProcessingException extends Exception {
  private static final long serialVersionUID = 1L;

  private final CharSequence msg;
  private @Nullable Element e;
  private @Nullable AnnotationMirror a;
  private @Nullable AnnotationValue v;

  public ProcessingException(CharSequence msg) {
    super(msg.toString());
    this.msg = requireNonNull(msg, "msg == null!");
  }

  public ProcessingException(CharSequence msg, Element e) {
    this(msg);
    this.e = requireNonNull(e, "e == null!");
  }

  public ProcessingException(CharSequence msg, Element e, AnnotationMirror a) {
    this(msg, e);
    this.a = requireNonNull(a, "a == null!");
  }

  public ProcessingException(CharSequence msg, Element e, AnnotationMirror a, AnnotationValue v) {
    this(msg, e, a);
    this.v = requireNonNull(v, "v == null!");
  }

  public void printTo(Messager messager) {
    if (v != null) {
      messager.printMessage(ERROR, msg, e, a, v);
    } else if (a != null) {
      messager.printMessage(ERROR, msg, e, a);
    } else if (e != null) {
      messager.printMessage(ERROR, msg, e);
    } else {
      messager.printMessage(ERROR, msg);
    }
  }
}
