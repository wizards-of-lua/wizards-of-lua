package net.wizardsoflua.annotation.processor.generator;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.PRIVATE;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.isJavaLangObject;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.isLuaType;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeSpec;

import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.annotation.processor.Constants;
import net.wizardsoflua.annotation.processor.Utils;
import net.wizardsoflua.annotation.processor.model.ArgumentModel;
import net.wizardsoflua.annotation.processor.model.FunctionModel;
import net.wizardsoflua.annotation.processor.model.PropertyModel;

public class GeneratorUtils {
  public static MethodSpec createDelegatingGetter(PropertyModel property, String delegateExpression,
      ProcessingEnvironment env) {
    TypeMirror getterType = property.getGetterType();
    String getterName = property.getGetterName();
    Builder getter = methodBuilder(getterName)//
        .addModifiers(PRIVATE)//
        .returns(Object.class)//
    ;
    if (isLuaType(getterType, env)) {
      getter.addStatement("return $L.$L()", delegateExpression, getterName);
    } else {
      getter.addStatement("$T result = $L.$L()", getterType, delegateExpression, getterName);
      getter.addStatement("return getConverter().toLuaNullable(result)");
    }
    return getter.build();
  }

  public static MethodSpec createDelegatingSetter(PropertyModel property, String delegateExpression) {
    String name = property.getName();
    String setterName = property.getSetterName();
    TypeMirror setterType = property.getSetterType();
    Builder setter = methodBuilder(setterName)//
        .addModifiers(PRIVATE)//
        .addParameter(Object.class, "luaObject")//
    ;
    if (isJavaLangObject(setterType)) {
      setter.addStatement("$T $L = luaObject", setterType, name);
    } else {
      String convertersMethod = property.isNullable() ? "toJavaNullable" : "toJava";
      setter.addStatement("$T $L = getConverter().$L($T.class, luaObject, $S)", setterType, name,
          convertersMethod, setterType, name);
    }
    setter.addStatement("$L.$L($L)", delegateExpression, setterName, name);
    return setter.build();
  }

  /**
   * Create a named lua function class that delegates calls to {@code function} via
   * {@code delegateExpression}. If {@code delegateType} is specified the first argument of the
   * function will be a self argument with name {@code delegateExpression}.
   *
   * @param function
   * @param delegateExpression
   * @param delegateType the self type or {@code null}
   * @param env
   * @return a named lua function class
   */
  public static TypeSpec createFunctionClass(FunctionModel function, String delegateExpression,
      @Nullable TypeMirror delegateType, ProcessingEnvironment env) {
    String name = function.getName();
    String Name = Utils.capitalize(name);
    int numberOfArgs = function.getArgs().size();
    if (delegateType != null) {
      numberOfArgs++; // Additional self arg
    }
    ClassName superclass = Constants.getNamedFunctionClassName(numberOfArgs);

    return classBuilder(Name + "Function")//
        .addModifiers(Modifier.PRIVATE)//
        .superclass(superclass)//
        .addMethod(createGetNameMethod(name))//
        .addMethod(createInvokeMethod(function, delegateExpression, delegateType, env))//
        .build();
  }

  public static MethodSpec createGetNameMethod(String name) {
    return methodBuilder("getName")//
        .addAnnotation(Override.class)//
        .addModifiers(Modifier.PUBLIC)//
        .returns(String.class)//
        .addStatement("return $S", name)//
        .build();
  }

  private static MethodSpec createInvokeMethod(FunctionModel function, String delegateVariable,
      @Nullable TypeMirror delegateType, ProcessingEnvironment env) {
    Types types = env.getTypeUtils();
    MethodSpec.Builder invokeMethod = methodBuilder("invoke")//
        .addAnnotation(Override.class)//
        .addModifiers(Modifier.PUBLIC)//
        .addException(ResolvedControlThrowable.class)//
        .addParameter(ExecutionContext.class, "context")//
    ;
    List<ArgumentModel> args = function.getArgs();
    List<ArgumentModel> luaArgs = new ArrayList<>(args);
    if (delegateType != null) {
      luaArgs.add(0, new ArgumentModel(delegateVariable, delegateType, false));
    }
    for (int i = 1; i <= luaArgs.size(); i++) {
      invokeMethod.addParameter(Object.class, "arg" + i);
    }
    int argIndex = 1;
    for (ArgumentModel arg : luaArgs) {
      TypeMirror argType = arg.getType();
      TypeMirror rawArgType = types.erasure(argType);
      String argName = arg.getName();
      boolean nullable = arg.isNullable();
      if (isJavaLangObject(argType)) {
        invokeMethod.addStatement("$T $L = arg$L", argType, argName, argIndex);
      } else {
        String convertersMethod = nullable ? "toJavaNullable" : "toJava";
        invokeMethod.addStatement("$T $L = getConverter().$L($T.class, arg$L, $L, $S, getName())",
            argType, argName, convertersMethod, rawArgType, argIndex, argIndex, argName);
      }
      argIndex++;
    }
    String arguments = Joiner.on(", ").join(Iterables.transform(args, ArgumentModel::getName));
    CodeBlock callDelegate =
        CodeBlock.of("$L.$L($L)", delegateVariable, function.getName(), arguments);
    TypeMirror returnType = function.getReturnType();
    if (returnType.getKind() == TypeKind.VOID) {
      invokeMethod.addStatement(callDelegate);
      invokeMethod.addStatement("context.getReturnBuffer().setTo()");
    } else if (isLuaType(returnType, env)) {
      invokeMethod.addStatement("$T result = $L", returnType, callDelegate);
      invokeMethod.addStatement("context.getReturnBuffer().setTo(result)");
    } else {
      invokeMethod.addStatement("$T result = $L", returnType, callDelegate);
      invokeMethod.addStatement("Object luaResult = getConverter().toLuaNullable(result)");
      invokeMethod.addStatement("context.getReturnBuffer().setTo(luaResult)");
    }
    return invokeMethod.build();
  }
}
