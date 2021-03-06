package net.wizardsoflua.annotation.processor.table.model;

import static java.util.Objects.requireNonNull;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.checkAnnotated;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import com.google.common.collect.Lists;
import net.wizardsoflua.annotation.LuaFunction;

public class FunctionModel {
  public static FunctionModel of(ExecutableElement method) {
    LuaFunction luaFunction = checkAnnotated(method, LuaFunction.class);

    TypeElement enclosingElement = (TypeElement) method.getEnclosingElement();

    String name = luaFunction.name();
    if (name.isEmpty()) {
      name = method.getSimpleName().toString();
    }

    TypeMirror returnType = method.getReturnType();
    List<ArgumentModel> args = Lists.transform(method.getParameters(), ArgumentModel::of);
    return new FunctionModel(enclosingElement, name, returnType, args);
  }

  private final TypeElement enclosingElement;
  private final String name;
  private final TypeMirror returnType;
  private final List<ArgumentModel> args = new ArrayList<>();

  public FunctionModel(TypeElement enclosingElement, String name, TypeMirror returnType,
      List<ArgumentModel> args) {
    this.enclosingElement = requireNonNull(enclosingElement, "enclosingElement == null!");
    this.name = requireNonNull(name, "name == null!");
    this.returnType = requireNonNull(returnType, "returnType == null!");
    this.args.addAll(args);
  }

  public TypeElement getEnclosingElement() {
    return enclosingElement;
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
