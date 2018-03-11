package net.wizardsoflua.annotation.processor.luaclass.generator;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static java.util.Objects.requireNonNull;
import static net.wizardsoflua.annotation.processor.generator.GeneratorUtils.createDelegatingGetter;
import static net.wizardsoflua.annotation.processor.generator.GeneratorUtils.createDelegatingSetter;
import static net.wizardsoflua.annotation.processor.luaclass.GenerateLuaClassProcessor.GENERATED_ANNOTATION;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import net.wizardsoflua.annotation.processor.luaclass.model.LuaClassModel;
import net.wizardsoflua.annotation.processor.model.PropertyModel;

public class LuaInstanceGenerator {
  private final LuaClassModel model;
  private final ProcessingEnvironment env;

  public LuaInstanceGenerator(LuaClassModel model, ProcessingEnvironment env) {
    this.model = requireNonNull(model, "model == null!");
    this.env = requireNonNull(env, "env == null!");
  }

  public JavaFile generate() {
    String packageName = model.getPackageName();
    TypeSpec luaInstanceType = createLuaInstanceType();
    return JavaFile.builder(packageName, luaInstanceType).build();
  }

  private TypeSpec createLuaInstanceType() {
    TypeSpec.Builder instanceType = classBuilder(model.getInstanceName())//
        .addAnnotation(GENERATED_ANNOTATION)//
        .addModifiers(Modifier.PUBLIC)//
        .addTypeVariable(createTypeVariableA())//
        .addTypeVariable(createTypeVariableD())//
        .superclass(createSuperClassTypeName())//
        .addMethod(createConstructor())//
    ;
    for (PropertyModel property : model.getProperties()) {
      if (property.isReadable()) {
        instanceType.addMethod(createDelegatingGetter(property, "api", env));
      }
      if (property.isWriteable()) {
        instanceType.addMethod(createDelegatingSetter(property, "api"));
      }
    }
    return instanceType.build();
  }

  private TypeVariableName createTypeVariableA() {
    TypeVariableName d = createTypeVariableD();
    ParameterizedTypeName upperBound = ParameterizedTypeName.get(model.getApiClassName(), d);
    return TypeVariableName.get("A", upperBound);
  }

  private TypeVariableName createTypeVariableD() {
    TypeName upperBound = model.getDelegateTypeName();
    return TypeVariableName.get("D", upperBound);
  }

  private ParameterizedTypeName createSuperClassTypeName() {
    ClassName raw = model.getSuperInstanceName();
    TypeVariableName a = createTypeVariableA();
    TypeVariableName d = createTypeVariableD();
    return ParameterizedTypeName.get(raw, a, d);
  }

  private MethodSpec createConstructor() {
    MethodSpec.Builder constructor = constructorBuilder()//
        .addModifiers(Modifier.PUBLIC)//
        .addParameter(createTypeVariableA(), "api")//
        .addStatement("super(api)")//
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
    for (ExecutableElement onCreateLuaInstance : model.getOnCreateLuaInstance()) {
      constructor.addStatement("api.$L(this)", onCreateLuaInstance.getSimpleName());
    }
    return constructor.build();
  }
}
