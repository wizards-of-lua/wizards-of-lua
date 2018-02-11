package net.wizardsoflua.annotation.processor.proxy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static net.wizardsoflua.annotation.processor.proxy.LuaApiProcessor.GENERATED_ANNOTATION;

import java.util.Collection;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.wizardsoflua.annotation.processor.Utils;
import net.wizardsoflua.annotation.processor.model.ArgumentModel;
import net.wizardsoflua.annotation.processor.model.FunctionModel;
import net.wizardsoflua.annotation.processor.model.ModuleModel;

public class LuaClassGenerator {
  private final ModuleModel module;

  public LuaClassGenerator(ModuleModel module) {
    this.module = checkNotNull(module, "module == null!");
  }

  public JavaFile generate() {
    String packageName = module.getPackageName();
    TypeSpec luaClassType = createLuaClass();
    return JavaFile.builder(packageName, luaClassType).build();
  }

  private TypeSpec createLuaClass() {
    TypeSpec.Builder luaClassType = classBuilder(module.getClassClassName())//
        .addAnnotation(GENERATED_ANNOTATION)//
        .addAnnotation(createDeclareLuaClassAnnotation(module))//
        .addModifiers(Modifier.PUBLIC)//
        .superclass(createSuperclassTypeName())//
        .addMethod(createToLuaMethod())//
        .addMethod(createConstructor(module))//
    ;
    for (FunctionModel function : module.getFunctions()) {
      luaClassType.addType(createInnerFunctionClass(function));
    }
    return luaClassType.build();
  }

  private AnnotationSpec createDeclareLuaClassAnnotation(ModuleModel module) {
    ClassName declareLuaClass = ClassName.get("net.wizardsoflua.lua.classes", "DeclareLuaClass");
    return AnnotationSpec.builder(declareLuaClass)//
        .addMember("name", "$S", module.getName())//
        .addMember("superClass", "$T.class", module.getSuperClass())//
        .build();
  }

  private ParameterizedTypeName createSuperclassTypeName() {
    ClassName raw = ClassName.get("net.wizardsoflua.lua.classes", "ProxyCachingLuaClass");
    TypeName delegate = module.getDelegateTypeName();
    TypeName proxy = module.getProxyClassName();
    return ParameterizedTypeName.get(raw, delegate, proxy);
  }

  private MethodSpec createToLuaMethod() {
    TypeName api = module.getApiClassName();
    TypeName delegate = module.getDelegateTypeName();
    TypeName proxy = module.getProxyClassName();
    return methodBuilder("toLua")//
        .addAnnotation(Override.class)//
        .addModifiers(Modifier.PROTECTED)//
        .returns(proxy)//
        .addParameter(delegate, "javaObject")//
        .addStatement("return new $T(new $T(this, javaObject))", proxy, api)//
        .build();
  }

  private MethodSpec createConstructor(ModuleModel module) {
    Builder constructor = constructorBuilder()//
        .addModifiers(Modifier.PUBLIC) //
    ;
    for (FunctionModel function : module.getFunctions()) {
      String Name = Utils.capitalize(function.getName());
      constructor.addStatement("add(new $LFunction())", Name);
    }
    return constructor.build();
  }

  private TypeSpec createInnerFunctionClass(FunctionModel function) {
    String name = function.getName();
    String Name = Utils.capitalize(name);
    int numberOfArgs = function.getArgs().size() + 1;
    ClassName superclass =
        ClassName.get("net.wizardsoflua.lua.function", "NamedFunction" + numberOfArgs);

    return classBuilder(Name + "Function")//
        .addModifiers(Modifier.PRIVATE)//
        .superclass(superclass)//
        .addMethod(createGetNameMethod(name))//
        .addMethod(createInvokeMethod(function))//
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

  private MethodSpec createInvokeMethod(FunctionModel function) {
    ClassName executionContextName =
        ClassName.get("net.sandius.rembulan.runtime", "ExecutionContext");

    Collection<ArgumentModel> args = function.getArgs();
    MethodSpec.Builder invokeMethod = methodBuilder("invoke")//
        .addAnnotation(Override.class)//
        .addModifiers(Modifier.PUBLIC)//
        .addParameter(executionContextName, "context")//
    ;
    for (int i = 1; i <= args.size() + 1; i++) {
      invokeMethod.addParameter(Object.class, "arg" + i);
    }
    invokeMethod.addStatement(createArgConversionStatement(1, "self", module.getApiClassName()));
    int argIndex = 2;
    for (ArgumentModel arg : args) {
      TypeMirror argType = arg.getType();
      String argName = arg.getName();
      invokeMethod.addStatement(createArgConversionStatement(argIndex, argName, argType));
      argIndex++;
    }
    String arguments = Joiner.on(", ").join(Iterables.transform(args, ArgumentModel::getName));
    CodeBlock callToApi = CodeBlock.of("self.$L($L)", function.getName(), arguments);
    TypeMirror returnType = function.getReturnType();
    if (returnType.getKind() == TypeKind.VOID) {
      invokeMethod.addStatement(callToApi);
      invokeMethod.addStatement("context.getReturnBuffer().setTo()");
    } else {
      invokeMethod.addStatement("$T result = $L", returnType, callToApi);
      invokeMethod.addStatement("context.getReturnBuffer().setTo(result)");
    }
    return invokeMethod.build();
  }

  private CodeBlock createArgConversionStatement(int argIndex, String argName, Object argType) {
    return CodeBlock.of("$T $L = getConverters().toJava($T.class, arg$L, $L, $S, getName())",
        argType, argName, argType, argIndex, argIndex, argName);
  }
}
