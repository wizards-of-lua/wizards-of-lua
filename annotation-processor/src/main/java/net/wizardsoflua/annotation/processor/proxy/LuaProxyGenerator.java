package net.wizardsoflua.annotation.processor.proxy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.PRIVATE;
import static net.wizardsoflua.annotation.processor.proxy.LuaApiProcessor.GENERATED_ANNOTATION;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.wizardsoflua.annotation.processor.Utils;
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

  private ParameterizedTypeName createSuperClassTypeName() {
    ClassName raw = ClassName.get("net.wizardsoflua.scribble", "LuaApiProxy");
    TypeName api = module.getApiClassName();
    TypeName delegate = module.getDelegateTypeName();
    return ParameterizedTypeName.get(raw, api, delegate);
  }

  private MethodSpec createConstructor() {
    MethodSpec.Builder constructor = constructorBuilder()//
        .addModifiers(Modifier.PUBLIC)//
        .addParameter(module.getApiClassName(), "api")//
        .addStatement("super(api)")//
    ;
    for (PropertyModel property : module.getProperties()) {
      String name = property.getName();
      String Name = Utils.capitalize(name);
      if (property.isWriteable()) {
        constructor.addStatement("add($S, this::get$L, this::set$L)", name, Name, Name);
      } else {
        constructor.addStatement("addReadOnly($S, this::get$L)", name, Name);
      }
    }
    return constructor.build();
  }

  private MethodSpec createGetter(PropertyModel property) {
    String name = property.getName();
    String Name = Utils.capitalize(name);
    return methodBuilder("get" + Name)//
        .addModifiers(PRIVATE)//
        .returns(Object.class)//
        .addStatement("$T result = api.get$L()", property.getType(), Name)//
        .addStatement("return getConverters().toLuaNullable(result)") //
        .build();
  }

  private MethodSpec createSetter(PropertyModel property) {
    String name = property.getName();
    String Name = Utils.capitalize(name);
    TypeName propertyType = TypeName.get(property.getType());
    return methodBuilder("set" + Name)//
        .addModifiers(PRIVATE)//
        .addParameter(Object.class, "luaObject")//
        .addStatement("$T $L = getConverters().toJava($T.class, luaObject, $S)", propertyType, name,
            propertyType, name)//
        .addStatement("api.set$L($L)", Name, name)//
        .build();
  }
}
