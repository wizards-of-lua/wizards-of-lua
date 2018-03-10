package net.wizardsoflua.annotation.processor.doc.model;

import static java.util.Objects.requireNonNull;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.checkAnnotated;
import static net.wizardsoflua.annotation.processor.doc.generator.LuaDocGenerator.renderType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaFunctionDoc;
import net.wizardsoflua.annotation.processor.ProcessingException;
import net.wizardsoflua.annotation.processor.ProcessorUtils;
import net.wizardsoflua.annotation.processor.doc.generator.LuaDocGenerator;

public class FunctionDocModel {
  public static FunctionDocModel of(ExecutableElement method, Map<String, String> luaClassNames,
      ProcessingEnvironment env) throws ProcessingException {
    LuaFunction luaFunction = checkAnnotated(method, LuaFunction.class);
    String name = luaFunction.name();
    if (name.isEmpty()) {
      name = method.getSimpleName().toString();
    }
    LuaFunctionDoc luaFunctionDoc = method.getAnnotation(LuaFunctionDoc.class);
    String returnType;
    List<String> args;
    if (luaFunctionDoc != null) {
      returnType = renderType(luaFunctionDoc.returnType());
      args = Arrays.asList(luaFunctionDoc.args());
    } else {
      returnType = renderType(method.getReturnType(), method, luaClassNames, env);
      args = Lists.transform(method.getParameters(), p -> p.getSimpleName().toString());
    }
    String description = LuaDocGenerator.getDescription(method, env);
    return new FunctionDocModel(name, returnType, args, description);
  }

  public static FunctionDocModel of(TypeElement typeElement, ProcessingEnvironment env)
      throws ProcessingException {
    LuaFunction luaFunction = checkAnnotated(typeElement, LuaFunction.class);
    LuaFunctionDoc luaFunctionDoc = checkAnnotated(typeElement, LuaFunctionDoc.class);
    String name = luaFunction.name();
    if (name.isEmpty()) {
      CharSequence msg = "You need to specify a function name for inner classes";
      Element e = typeElement;
      AnnotationMirror a = ProcessorUtils.getAnnotationMirror(typeElement, LuaFunction.class);
      throw new ProcessingException(msg, e, a);
    }
    String returnType = renderType(luaFunctionDoc.returnType());
    List<String> args = Arrays.asList(luaFunctionDoc.args());
    String description = LuaDocGenerator.getDescription(typeElement, env);
    return new FunctionDocModel(name, returnType, args, description);
  }

  private final String name;
  private final String returnType;
  private final ImmutableList<String> args;
  private final String description;

  public FunctionDocModel(String name, String returnType, List<String> args, String description) {
    this.name = requireNonNull(name, "name == null!");
    this.returnType = requireNonNull(returnType, "returnType == null!");
    this.args = ImmutableList.copyOf(args);
    this.description = requireNonNull(description, "description == null!");
  }

  /**
   * @return the value of {@link #name}
   */
  public String getName() {
    return name;
  }

  /**
   * @return the value of {@link #returnType}
   */
  public String getReturnType() {
    return returnType;
  }

  /**
   * @return the value of {@link #args}
   */
  public ImmutableList<String> getArgs() {
    return args;
  }

  /**
   * @return the value of {@link #description}
   */
  public String getDescription() {
    return description;
  }
}
