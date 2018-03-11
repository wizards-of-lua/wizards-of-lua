package net.wizardsoflua.annotation.processor.generator;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.PRIVATE;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAllSuperTypes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
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

import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.annotation.processor.Constants;
import net.wizardsoflua.annotation.processor.Utils;
import net.wizardsoflua.annotation.processor.model.ArgumentModel;
import net.wizardsoflua.annotation.processor.model.FunctionModel;
import net.wizardsoflua.annotation.processor.model.PropertyModel;

public class GeneratorUtils {
  public static MethodSpec createDelegatingGetter(PropertyModel property, String delegateVariable,
      ProcessingEnvironment env) {
    TypeMirror getterType = property.getGetterType();
    String getterName = property.getGetterName();
    Builder getter = methodBuilder(getterName)//
        .addModifiers(PRIVATE)//
        .returns(Object.class)//
    ;
    if (isLuaType(getterType, env)) {
      getter.addStatement("return $L.$L()", delegateVariable, getterName);
    } else {
      getter.addStatement("$T result = $L.$L()", getterType, delegateVariable, getterName);
      getter.addStatement("return getConverters().toLuaNullable(result)");
    }
    return getter.build();
  }

  public static MethodSpec createDelegatingSetter(PropertyModel property, String delegateVariable) {
    String name = property.getName();
    String setterName = property.getSetterName();
    TypeMirror setterType = property.getSetterType();
    String convertersMethod = property.isNullable() ? "toJavaNullable" : "toJava";
    return methodBuilder(setterName)//
        .addModifiers(PRIVATE)//
        .addParameter(Object.class, "luaObject")//
        .addStatement("$T $L = getConverters().$L($T.class, luaObject, $S)", setterType, name,
            convertersMethod, setterType, name)//
        .addStatement("$L.$L($L)", delegateVariable, setterName, name)//
        .build();
  }

  /**
   * Create a named lua function class that delegates calls to {@code function} via
   * {@code delegateVariable}. If {@code delegateType} is specified the first argument of the
   * function will be a self argument with name {@code delegateVariable}.
   *
   * @param function
   * @param delegateVariable
   * @param delegateType the self type or {@code null}
   * @param env
   * @return a named lua function class
   */
  public static TypeSpec createFunctionClass(FunctionModel function, String delegateVariable,
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
        .addMethod(createInvokeMethod(function, delegateVariable, delegateType, env))//
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
      String convertersMethod = nullable ? "toJavaNullable" : "toJava";
      invokeMethod.addStatement("$T $L = getConverters().$L($T.class, arg$L, $L, $S, getName())",
          argType, argName, convertersMethod, rawArgType, argIndex, argIndex, argName);
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
      invokeMethod.addStatement("Object luaResult = getConverters().toLuaNullable(result)");
      invokeMethod.addStatement("context.getReturnBuffer().setTo(luaResult)");
    }
    return invokeMethod.build();
  }

  private static boolean isLuaType(TypeMirror typeMirror, ProcessingEnvironment env) {
    TypeKind kind = typeMirror.getKind();
    if (kind.isPrimitive()) {
      return kind != TypeKind.CHAR;
    }
    // Using getAllSuperTypes, because Types.isSubType() does not work across processing rounds
    for (TypeMirror superType : getAllSuperTypes(typeMirror, env)) {
      if (superType.getKind() == TypeKind.DECLARED) {
        TypeElement superElement = (TypeElement) ((DeclaredType) superType).asElement();
        Name qualifiedName = superElement.getQualifiedName();
        if (qualifiedName.contentEquals(Table.class.getName())//
            || qualifiedName.contentEquals(ByteString.class.getName())//
            || qualifiedName.contentEquals(Number.class.getName())//
            || qualifiedName.contentEquals(Boolean.class.getName())//
            || qualifiedName.contentEquals(LuaFunction.class.getName())//
        ) {
          return true;
        }
      }
    }
    return false;
  }
}
