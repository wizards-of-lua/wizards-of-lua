package net.wizardsoflua.annotation.processor.table.generator;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static java.util.Objects.requireNonNull;
import static javax.lang.model.element.Modifier.PRIVATE;
import static net.wizardsoflua.annotation.processor.Constants.LUA_CONVERTERS;
import static net.wizardsoflua.annotation.processor.Constants.LUA_INSTANCE_TABLE_SUPERCLASS;
import static net.wizardsoflua.annotation.processor.Constants.LUA_TABLE_SUPERCLASS;
import static net.wizardsoflua.annotation.processor.Constants.getNamedFunctionClassName;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getTypeParameter;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.isJavaLangObject;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.isLuaType;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.isSubType;
import static net.wizardsoflua.annotation.processor.table.GenerateLuaTableProcessor.GENERATED_ANNOTATION;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.annotation.processor.StringUtils;
import net.wizardsoflua.annotation.processor.table.model.ArgumentModel;
import net.wizardsoflua.annotation.processor.table.model.FunctionModel;
import net.wizardsoflua.annotation.processor.table.model.LuaTableModel;
import net.wizardsoflua.annotation.processor.table.model.ManualFunctionModel;
import net.wizardsoflua.annotation.processor.table.model.PropertyModel;

public class LuaTableGenerator {
  private final LuaTableModel model;
  private final ProcessingEnvironment env;

  public LuaTableGenerator(LuaTableModel model, ProcessingEnvironment env) {
    this.model = requireNonNull(model, "model == null!");
    this.env = requireNonNull(env, "env == null!");
  }

  public JavaFile generate() {
    String packageName = model.getGeneratedPackageName();
    TypeSpec luaModuleType = createLuaTable();
    return JavaFile.builder(packageName, luaModuleType).build();
  }

  private TypeSpec createLuaTable() {
    TypeSpec.Builder luaModuleType = classBuilder(model.getGeneratedSimpleName())//
        .addAnnotation(GENERATED_ANNOTATION)//
        .addModifiers(Modifier.PUBLIC)//
        .addTypeVariable(createTypeVariableD())//
        .superclass(createSuperClassTypeName())//
        .addMethod(createConstructor())//
    ;
    for (PropertyModel property : model.getProperties()) {
      if (property.isReadable()) {
        luaModuleType.addMethod(createDelegatingGetter(property, "getDelegate()", env));
      }
      if (property.isWriteable()) {
        luaModuleType.addMethod(createDelegatingSetter(property, "getDelegate()"));
      }
    }
    for (FunctionModel function : model.getFunctions()) {
      TypeElement enclosingElement = function.getEnclosingElement();
      if (model.getSourceElement().equals(enclosingElement)) {
        luaModuleType.addType(createFunctionClass(function, "getDelegate()", null, env));
      } else {
        Types types = env.getTypeUtils();
        TypeMirror[] typeArgs = new TypeMirror[enclosingElement.getTypeParameters().size()];
        for (int i = 0; i < typeArgs.length; i++) {
          typeArgs[i] = types.getWildcardType(null, null);
        }
        TypeMirror delegateType = types.getDeclaredType(enclosingElement, typeArgs);
        luaModuleType.addType(createFunctionClass(function, "self", delegateType, env));
      }
    }
    return luaModuleType.build();
  }

  private TypeVariableName createTypeVariableD() {
    TypeName upperBound = model.getParameterizedSourceClassName();
    return TypeVariableName.get("D", upperBound);
  }

  private ParameterizedTypeName createSuperClassTypeName() {
    ClassName raw = model.getSuperTableClassName();
    TypeVariableName d = createTypeVariableD();
    return ParameterizedTypeName.get(raw, d);
  }

  private MethodSpec createConstructor() {
    TypeName delegate = createTypeVariableD();
    Builder constructor = constructorBuilder()//
        .addModifiers(Modifier.PUBLIC)//
        .addParameter(delegate, "delegate")//
    ;
    if (model.hasMetatable()) {
      constructor.addParameter(Table.class, "metatable");
    }
    constructor.addParameter(LUA_CONVERTERS, "converters");
    ClassName superTableClassName = model.getSuperTableClassName();
    if (LUA_TABLE_SUPERCLASS.equals(superTableClassName)
        || LUA_INSTANCE_TABLE_SUPERCLASS.equals(superTableClassName)) {
      if (model.hasMetatable()) {
        constructor.addStatement("super(delegate, metatable, converters, $L)",
            model.isModifiable());
      } else {
        constructor.addStatement("super(delegate, converters, $L)", model.isModifiable());
      }
    } else {
      if (model.hasMetatable()) {
        constructor.addStatement("super(delegate, metatable, converters)");
      } else {
        constructor.addStatement("super(delegate, converters)");
      }
    }
    for (PropertyModel property : model.getProperties()) {
      String name = property.getName();
      String getterName = property.getGetterName();
      String setterName = property.getSetterName();
      if (property.isWriteable()) {
        constructor.addStatement("add($S, this::$L, this::$L)", name, getterName, setterName);
      } else {
        constructor.addStatement("addReadOnly($S, this::$L)", name, getterName);
      }
    }
    for (FunctionModel function : model.getFunctions()) {
      String Name = StringUtils.capitalize(function.getName());
      constructor.addStatement("addFunction(new $LFunction())", Name);
    }
    for (ManualFunctionModel function : model.getManualFunctions()) {
      String name = function.getName();
      TypeElement functionType = function.getFunctionType();
      if (functionType.getModifiers().contains(Modifier.STATIC)) {
        constructor.addStatement("addFunction($S, new $T(delegate))", name, functionType);
      } else {
        String functionTypeSimpleName = ClassName.get(functionType).simpleName();
        constructor.addStatement("addFunction($S, delegate.new $L())", name,
            functionTypeSimpleName);
      }
    }
    return constructor.build();
  }

  private MethodSpec createDelegatingGetter(PropertyModel property, String delegateExpression,
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
      getter.addStatement("return getConverters().toLuaNullable(result)");
    }
    return getter.build();
  }

  private MethodSpec createDelegatingSetter(PropertyModel property, String delegateExpression) {
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
      setter.addStatement("$T $L = getConverters().$L($T.class, luaObject, $S)", setterType, name,
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
  private TypeSpec createFunctionClass(FunctionModel function, String delegateExpression,
      @Nullable TypeMirror delegateType, ProcessingEnvironment env) {
    String name = function.getName();
    String Name = StringUtils.capitalize(name);
    int numberOfArgs = function.getArgs().size();
    if (delegateType != null) {
      numberOfArgs++; // Additional self arg
    }
    ClassName superclass = getNamedFunctionClassName(numberOfArgs);

    return classBuilder(Name + "Function")//
        .addModifiers(Modifier.PRIVATE)//
        .superclass(superclass)//
        .addMethod(createGetNameMethod(name))//
        .addMethod(createInvokeMethod(function, delegateExpression, delegateType, env))//
        .build();
  }

  private MethodSpec createGetNameMethod(String name) {
    return methodBuilder("getName")//
        .addAnnotation(Override.class)//
        .addModifiers(Modifier.PUBLIC)//
        .returns(String.class)//
        .addStatement("return $S", name)//
        .build();
  }

  private MethodSpec createInvokeMethod(FunctionModel function, String delegateVariable,
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
      if (isJavaLangObject(argType)) {
        invokeMethod.addStatement("$T $L = arg$L", argType, argName, argIndex);
      } else {
        boolean nullable = arg.isNullable();
        String convertersMethod;
        String iterableClassName = Iterable.class.getName();
        if (isSubType(argType, iterableClassName, env)) {
          rawArgType = getTypeParameter(argType, iterableClassName, 0, env);
          convertersMethod = nullable ? "toJavaListNullable" : "toJavaList";
        } else {
          convertersMethod = nullable ? "toJavaNullable" : "toJava";
        }
        invokeMethod.addStatement("$T $L = getConverters().$L($T.class, arg$L, $L, $S, getName())",
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
      invokeMethod.addStatement("Object luaResult = getConverters().toLuaNullable(result)");
      invokeMethod.addStatement("context.getReturnBuffer().setTo(luaResult)");
    }
    return invokeMethod.build();
  }
}
