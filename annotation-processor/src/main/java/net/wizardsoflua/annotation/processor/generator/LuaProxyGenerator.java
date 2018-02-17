package net.wizardsoflua.annotation.processor.generator;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.PRIVATE;
import static net.wizardsoflua.annotation.processor.LuaApiProcessor.GENERATED_ANNOTATION;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import net.wizardsoflua.annotation.processor.model.ModuleModel;
import net.wizardsoflua.annotation.processor.model.PropertyModel;

public class LuaProxyGenerator {
  private final ModuleModel module;

  public LuaProxyGenerator(ModuleModel module) {
    this.module = checkNotNull(module, "module == null!");
  }

  public JavaFile generate() {
    String packageName = module.getPackageName();
    TypeSpec luaProxyType = createLuaProxyType();
    return JavaFile.builder(packageName, luaProxyType).build();
  }

  private TypeSpec createLuaProxyType() {
    TypeSpec.Builder proxyType = classBuilder(module.getProxyClassName())//
        .addAnnotation(GENERATED_ANNOTATION)//
        .addModifiers(Modifier.PUBLIC)//
        .addTypeVariable(createTypeVariableA())//
        .addTypeVariable(createTypeVariableD())//
        .superclass(createSuperClassTypeName())//
        .addMethod(createConstructor())//
    ;
    for (PropertyModel property : module.getProperties()) {
      if (property.isReadable()) {
        proxyType.addMethod(createGetter(property));
      }
      if (property.isWriteable()) {
        proxyType.addMethod(createSetter(property));
      }
    }
    return proxyType.build();
  }

  private TypeVariableName createTypeVariableA() {
    TypeVariableName d = createTypeVariableD();
    ParameterizedTypeName upperBound = ParameterizedTypeName.get(module.getApiClassName(), d);
    return TypeVariableName.get("A", upperBound);
  }

  private TypeVariableName createTypeVariableD() {
    TypeName upperBound = module.getDelegateTypeName();
    return TypeVariableName.get("D", upperBound);
  }

  private ParameterizedTypeName createSuperClassTypeName() {
    ClassName raw = module.getSuperProxyClassName();
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
    for (PropertyModel property : module.getProperties()) {
      String name = property.getName();
      String getterName = property.getGetterName();
      String setterName = property.getSetterName();
      if (property.isWriteable()) {
        constructor.addStatement("add($S, this::$L, this::$L)", name, getterName, setterName);
      } else {
        constructor.addStatement("addReadOnly($S, this::$L)", name, getterName);
      }
    }
    for (ExecutableElement onCreateLuaProxy : module.getOnCreateLuaProxy()) {
      constructor.addStatement("api.$L(this)", onCreateLuaProxy.getSimpleName());
    }
    return constructor.build();
  }

  private MethodSpec createGetter(PropertyModel property) {
    String getterName = property.getGetterName();
    return methodBuilder(getterName)//
        .addModifiers(PRIVATE)//
        .returns(Object.class)//
        .addStatement("Object result = api.$L()", getterName)//
        .addStatement("return getConverters().toLuaNullable(result)") //
        .build();
  }

  private MethodSpec createSetter(PropertyModel property) {
    String name = property.getName();
    String setterName = property.getSetterName();
    TypeName propertyType = TypeName.get(property.getSetterType());
    String convertersMethod = property.isNullable() ? "toJavaNullable" : "toJava";
    return methodBuilder(setterName)//
        .addModifiers(PRIVATE)//
        .addParameter(Object.class, "luaObject")//
        .addStatement("$T $L = getConverters().$L($T.class, luaObject, $S)", propertyType, name,
            convertersMethod, propertyType, name)//
        .addStatement("api.$L($L)", setterName, name)//
        .build();
  }
}
