package net.wizardsoflua.annotation.processor.proxy;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.util.ElementFilter.methodsIn;
import static javax.lang.model.util.ElementFilter.typesIn;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaModule;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.annotation.processor.Module;
import net.wizardsoflua.annotation.processor.Property;
import net.wizardsoflua.annotation.processor.Utils;

public class LuaProxyProcessor extends AbstractProcessor {
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    HashSet<String> result = new HashSet<>();
    result.add(LuaModule.class.getName());
    return result;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.RELEASE_8;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Elements elements = processingEnv.getElementUtils();
    Types types = processingEnv.getTypeUtils();
    for (TypeElement annotation : annotations) {
      Set<TypeElement> moduleElements = typesIn(roundEnv.getElementsAnnotatedWith(annotation));
      for (TypeElement moduleElement : moduleElements) {
        LuaModule luaModule = moduleElement.getAnnotation(LuaModule.class);
        String moduleName = luaModule.name();
        AnnotationMirror mirror = getAnnoationMirror(moduleElement, LuaModule.class);
        DeclaredType superClass = getClassValue(mirror, "superClass");
        Module module = new Module(moduleName, superClass);

        List<ExecutableElement> methods = methodsIn(elements.getAllMembers(moduleElement));
        for (ExecutableElement method : methods) {
          LuaProperty luaProperty = method.getAnnotation(LuaProperty.class);
          if (luaProperty != null) {
            String docComment = elements.getDocComment(method);
            Property property = Property.of(method, luaProperty, docComment);
            module.addProperty(property);
          }
          LuaFunction luaFunction = method.getAnnotation(LuaFunction.class);
          if (luaFunction != null) {
            System.out.println(luaFunction);
          }
        }

        TypeMirror subType = moduleElement.asType();
        String superType = "net.wizardsoflua.scribble.LuaApi";
        int typeParameterIndex = 0;
        TypeMirror delegateType = getTypeParameter(subType, superType, typeParameterIndex, types);

        ClassName apiClassName = ClassName.get(moduleElement);
        TypeName delegateTypeName = ClassName.get(delegateType);
        String packageName = elements.getPackageOf(moduleElement).getQualifiedName().toString();


        ClassName declareLuaClass =
            ClassName.get("net.wizardsoflua.lua.classes", "DeclareLuaClass");

        AnnotationSpec anno = AnnotationSpec.builder(declareLuaClass)//
            .addMember("name", "$S", module.getName())//
            .addMember("superClass", "$T.class", module.getSuperClass())//
            .build();
        TypeSpec luaClassType = classBuilder(module.getName() + "Class")//
            .addAnnotation(anno)//
            .build();
        JavaFile luaClass = JavaFile.builder(packageName, luaClassType).build();

        JavaFile proxy = createProxy(module, packageName, apiClassName, delegateTypeName);

        Filer filer = processingEnv.getFiler();
        try {
          write(luaClass, filer);
          write(proxy, filer);
        } catch (IOException ex) {
          throw new UndeclaredThrowableException(ex);
        }
      }
    }
    return false;
  }

  private void write(JavaFile file, Filer filer) throws IOException {
    String qualifiedName = file.packageName + '.' + file.typeSpec.name;
    try (Writer writer = new BufferedWriter(filer.createSourceFile(qualifiedName).openWriter())) {
      file.writeTo(writer);
    }
  }

  private JavaFile createProxy(Module module, String packageName, ClassName apiClassName,
      TypeName delegateTypeName) {
    TypeSpec proxyType = createProxyType(module, apiClassName, delegateTypeName);
    return JavaFile.builder(packageName, proxyType).build();
  }

  private TypeSpec createProxyType(Module module, ClassName apiClassName,
      TypeName delegateTypeName) {
    ClassName proxySuperclass = ClassName.get("net.wizardsoflua.scribble", "LuaApiProxy");
    ParameterizedTypeName parameterizedProxySuperclass =
        ParameterizedTypeName.get(proxySuperclass, apiClassName, delegateTypeName);

    TypeSpec.Builder type = classBuilder(module.getName() + "Proxy")//
        .addModifiers(Modifier.PUBLIC)//
        .superclass(parameterizedProxySuperclass)//
        .addMethod(createConstructor(module, apiClassName))//
    ;
    for (Property property : module.getProperties()) {
      if (property.isReadable()) {
        type.addMethod(createProxyGetter(property));
      }
      if (property.isWriteable()) {
        type.addMethod(createProxySetter(property));
      }
    }
    return type.build();
  }

  private MethodSpec createConstructor(Module module, ClassName apiClassName) {
    MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()//
        .addModifiers(Modifier.PUBLIC)//
        .addParameter(apiClassName, "api")//
        .addStatement("super(api)")//
    ;
    for (Property property : module.getProperties()) {
      String name = property.getName();
      String Name = Utils.capitalize(name);
      if (property.isWriteable()) {
        constructorBuilder.addStatement("add($S, this::get$L, this::set$L)", name, Name, Name);
      } else {
        constructorBuilder.addStatement("addReadOnly($S, this::get$L)", name, Name);
      }
    }
    return constructorBuilder.build();
  }

  private MethodSpec createProxyGetter(Property property) {
    String name = property.getName();
    String Name = Utils.capitalize(name);
    return methodBuilder("get" + Name)//
        .addModifiers(PRIVATE)//
        .returns(Object.class)//
        .addStatement("$T result = api.get$L()", property.getType(), Name)//
        .addStatement("return getConverters().toLuaNullable(result)") //
        .build();
  }

  private MethodSpec createProxySetter(Property property) {
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

  private TypeMirror getTypeParameter(TypeMirror subType, String superType, int typeParameterIndex,
      Types types) {
    TypeMirror typeParameter = null;
    TypeMirror superMirror = subType;
    do {
      TypeElement superElement = (TypeElement) types.asElement(superMirror);
      if (superType.equals(superElement.getQualifiedName().toString())) {
        typeParameter = ((DeclaredType) superMirror).getTypeArguments().get(typeParameterIndex);
        break;
      }
      superMirror = superElement.getSuperclass();
    } while (superMirror != null);
    return typeParameter;
  }

  private @Nullable DeclaredType getClassValue(AnnotationMirror mirror, String key) {
    for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror
        .getElementValues().entrySet()) {
      if (key.equals(entry.getKey().getSimpleName().toString())) {
        return (DeclaredType) entry.getValue().getValue();
      }
    }
    return null;
  }

  private @Nullable AnnotationMirror getAnnoationMirror(TypeElement moduleElement,
      Class<? extends Annotation> annoationClass) {
    for (AnnotationMirror mirror : moduleElement.getAnnotationMirrors()) {
      if (annoationClass.getName().equals(mirror.getAnnotationType().toString())) {
        return mirror;
      }
    }
    return null;
  }

}
