package net.wizardsoflua.annotation.processor.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import net.wizardsoflua.annotation.LuaFunction;

public class FunctionModel {
  public static FunctionModel of(ExecutableElement method, LuaFunction luaFunction,
      String docComment) {
    String name = method.getSimpleName().toString();
    TypeMirror returnType = method.getReturnType();
    List<ArgumentModel> args = Lists.transform(method.getParameters(), ArgumentModel::of);
    String description = Strings.nullToEmpty(docComment).trim();
    return new FunctionModel(name, returnType, args, description);
  }

  private final String name;
  private final TypeMirror returnType;
  private final List<ArgumentModel> args = new ArrayList<>();
  private final String description;

  public FunctionModel(String name, TypeMirror returnType, List<ArgumentModel> args,
      String description) {
    this.name = checkNotNull(name, "name == null!");
    this.returnType = checkNotNull(returnType, "returnType == null!");
    this.args.addAll(args);
    this.description = checkNotNull(description, "description == null!");
  }

  public String getName() {
    return name;
  }

  public Collection<ArgumentModel> getArgs() {
    return Collections.unmodifiableList(args);
  }

  public TypeMirror getReturnType() {
    return returnType;
  }

  public String getDescription() {
    return description;
  }
}
