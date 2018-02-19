package net.wizardsoflua.annotation.processor.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import com.google.common.collect.Lists;

public class FunctionModel {
  public static FunctionModel of(ExecutableElement method) {
    String name = method.getSimpleName().toString();
    TypeMirror returnType = method.getReturnType();
    List<ArgumentModel> args = Lists.transform(method.getParameters(), ArgumentModel::of);
    return new FunctionModel(name, returnType, args);
  }

  private final String name;
  private final TypeMirror returnType;
  private final List<ArgumentModel> args = new ArrayList<>();

  public FunctionModel(String name, TypeMirror returnType, List<ArgumentModel> args) {
    this.name = requireNonNull(name, "name == null!");
    this.returnType = requireNonNull(returnType, "returnType == null!");
    this.args.addAll(args);
  }

  public String getName() {
    return name;
  }

  public List<ArgumentModel> getArgs() {
    return Collections.unmodifiableList(args);
  }

  public TypeMirror getReturnType() {
    return returnType;
  }
}
