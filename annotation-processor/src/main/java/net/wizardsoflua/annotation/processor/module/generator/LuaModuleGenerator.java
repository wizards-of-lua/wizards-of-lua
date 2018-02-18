package net.wizardsoflua.annotation.processor.module.generator;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static java.util.Objects.requireNonNull;
import static net.wizardsoflua.annotation.processor.generator.GeneratorUtils.createDelegatingGetter;
import static net.wizardsoflua.annotation.processor.generator.GeneratorUtils.createDelegatingSetter;
import static net.wizardsoflua.annotation.processor.generator.GeneratorUtils.createFunctionClass;
import static net.wizardsoflua.annotation.processor.generator.GeneratorUtils.createGetNameMethod;
import static net.wizardsoflua.annotation.processor.module.GenerateLuaModuleProcessor.GENERATED_ANNOTATION;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Types;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.wizardsoflua.annotation.processor.Constants;
import net.wizardsoflua.annotation.processor.Utils;
import net.wizardsoflua.annotation.processor.model.FunctionModel;
import net.wizardsoflua.annotation.processor.model.PropertyModel;
import net.wizardsoflua.annotation.processor.module.model.LuaModuleModel;

public class LuaModuleGenerator {
  private final LuaModuleModel model;
  private final Types types;

  public LuaModuleGenerator(LuaModuleModel model, ProcessingEnvironment env) {
    this.model = requireNonNull(model, "model == null!");
    this.types = env.getTypeUtils();
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
        .addMethod(createGetNameMethod(model.getName()))//
    ;
    for (PropertyModel property : model.getProperties()) {
      if (property.isReadable()) {
        luaModuleType.addMethod(createDelegatingGetter(property, "delegate"));
      }
      if (property.isWriteable()) {
        luaModuleType.addMethod(createDelegatingSetter(property, "delegate"));
      }
    }
    for (FunctionModel function : model.getFunctions()) {
      luaModuleType.addType(createFunctionClass(function, "delegate", null, types));
    }
    return luaModuleType.build();
  }

  private ParameterizedTypeName createSuperclassTypeName() {
    ClassName raw = Constants.LUA_MODULE_CLASS_NAME;
    TypeName delegate = model.getClassName();
    return ParameterizedTypeName.get(raw, delegate);
  }

  private MethodSpec createConstructor() {
    ClassName luaClassLoader = Constants.LUA_CLASS_LOADER_CLASS_NAME;
    TypeName delegate = model.getClassName();
    Builder constructor = constructorBuilder()//
        .addModifiers(Modifier.PUBLIC)//
        .addParameter(luaClassLoader, "classLoader")//
        .addParameter(delegate, "delegate")//
        .addStatement("super(classLoader, delegate)")//
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
    return constructor.build();
  }
}
