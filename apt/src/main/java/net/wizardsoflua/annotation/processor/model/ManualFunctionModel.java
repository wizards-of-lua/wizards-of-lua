package net.wizardsoflua.annotation.processor.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.checkAnnotated;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.processor.ProcessingException;
import net.wizardsoflua.annotation.processor.ProcessorUtils;

public class ManualFunctionModel {
  public static ManualFunctionModel of(TypeElement typeElement) throws ProcessingException {
    LuaFunction luaFunction = checkAnnotated(typeElement, LuaFunction.class);
    String name = luaFunction.name();
    if (name.isEmpty()) {
      CharSequence msg = "You need to specify a function name for inner classes";
      Element e = typeElement;
      AnnotationMirror a = ProcessorUtils.getAnnotationMirror(typeElement, LuaFunction.class);
      throw new ProcessingException(msg, e, a);
    }
    return new ManualFunctionModel(name, typeElement);
  }

  private final String name;
  private final TypeElement functionType;

  public ManualFunctionModel(String name, TypeElement functionType) {
    this.name = checkNotNull(name, "name == null!");
    this.functionType = checkNotNull(functionType, "functionType == null!");
  }

  public String getName() {
    return name;
  }

  public TypeElement getFunctionType() {
    return functionType;
  }
}
