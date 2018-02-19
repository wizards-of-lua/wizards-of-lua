package net.wizardsoflua.annotation.processor.doc.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.wizardsoflua.annotation.processor.doc.generator.LuaDocGenerator;

public class FunctionDocModel {
  public static FunctionDocModel of(ExecutableElement method, ProcessingEnvironment env) {
    String name = method.getSimpleName().toString();
    String returnType = LuaDocGenerator.renderType(method.getReturnType(), env);
    List<String> args = Lists.transform(method.getParameters(), p -> p.getSimpleName().toString());
    String description = LuaDocGenerator.getDescription(method, env);
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
