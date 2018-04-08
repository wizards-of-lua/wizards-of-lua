package net.wizardsoflua.annotation.processor.module.generator;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static java.util.Objects.requireNonNull;
import static net.wizardsoflua.annotation.processor.Constants.CONVERTER_CLASS;
import static net.wizardsoflua.annotation.processor.Constants.LUA_MODULE_SUPERCLASS;
import static net.wizardsoflua.annotation.processor.generator.GeneratorUtils.createDelegatingGetter;
import static net.wizardsoflua.annotation.processor.generator.GeneratorUtils.createDelegatingSetter;
import static net.wizardsoflua.annotation.processor.generator.GeneratorUtils.createFunctionClass;
import static net.wizardsoflua.annotation.processor.module.GenerateLuaModuleProcessor.GENERATED_ANNOTATION;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.wizardsoflua.annotation.processor.Utils;
import net.wizardsoflua.annotation.processor.model.FunctionModel;
import net.wizardsoflua.annotation.processor.model.ManualFunctionModel;
import net.wizardsoflua.annotation.processor.model.PropertyModel;
import net.wizardsoflua.annotation.processor.module.model.LuaModuleModel;

public class LuaModuleGenerator {
  private final LuaModuleModel model;
  private final ProcessingEnvironment env;

  public LuaModuleGenerator(LuaModuleModel model, ProcessingEnvironment env) {
    this.model = requireNonNull(model, "model == null!");
    this.env = requireNonNull(env, "env == null!");
  }

  public JavaFile generate() {
    String packageName = model.getPackageName();
    TypeSpec luaModuleType = createLuaModule();
    return JavaFile.builder(packageName, luaModuleType).build();
  }

  private TypeSpec createLuaModule() {
    TypeSpec.Builder luaModuleType = classBuilder(model.getModuleClassName())//
        .addAnnotation(GENERATED_ANNOTATION)//
        .addModifiers(Modifier.PUBLIC)//
        .superclass(createSuperclassTypeName())//
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
      luaModuleType.addType(createFunctionClass(function, "getDelegate()", null, env));
    }
    return luaModuleType.build();
  }

  private ParameterizedTypeName createSuperclassTypeName() {
    ClassName raw = LUA_MODULE_SUPERCLASS;
    TypeName delegate = model.getClassName();
    return ParameterizedTypeName.get(raw, delegate);
  }

  private MethodSpec createConstructor() {
    TypeName delegate = model.getClassName();
    TypeName converter = CONVERTER_CLASS;
    Builder constructor = constructorBuilder()//
        .addModifiers(Modifier.PUBLIC)//
        .addParameter(delegate, "delegate")//
        .addParameter(converter, "converter")//
        .addStatement("super(delegate, converter)")//
    ;
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
      String Name = Utils.capitalize(function.getName());
      constructor.addStatement("addReadOnly(new $LFunction())", Name);
    }
    for (ManualFunctionModel function : model.getManualFunctions()) {
      String name = function.getName();
      String functionTypeSimpleName = ClassName.get(function.getFunctionType()).simpleName();
      constructor.addStatement("addReadOnly($S, delegate.new $L())", name, functionTypeSimpleName);
    }
    return constructor.build();
  }
}
