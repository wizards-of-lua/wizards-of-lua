package net.wizardsoflua.annotation.processor.luaclass;

import static javax.lang.model.util.ElementFilter.methodsIn;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.write;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;

import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.annotation.OnCreateLuaProxy;
import net.wizardsoflua.annotation.OnLoadLuaClass;
import net.wizardsoflua.annotation.processor.ExceptionHandlingProcessor;
import net.wizardsoflua.annotation.processor.ProcessingException;
import net.wizardsoflua.annotation.processor.luaclass.generator.LuaClassGenerator;
import net.wizardsoflua.annotation.processor.luaclass.generator.LuaProxyGenerator;
import net.wizardsoflua.annotation.processor.luaclass.model.LuaClassModel;
import net.wizardsoflua.annotation.processor.model.FunctionModel;
import net.wizardsoflua.annotation.processor.model.PropertyModel;

public class GenerateLuaClassProcessor extends ExceptionHandlingProcessor {
  public static final AnnotationSpec GENERATED_ANNOTATION = AnnotationSpec.builder(Generated.class)//
      .addMember("value", "$S", GenerateLuaClass.class.getSimpleName())//
      .build();

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    HashSet<String> result = new HashSet<>();
    result.add(GenerateLuaClass.class.getName());
    return result;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  protected void doProcess(TypeElement annotation, Element annotatedElement,
      RoundEnvironment roundEnv) throws ProcessingException {
    if (annotatedElement.getKind() == ElementKind.CLASS) {
      LuaClassModel model = analyze((TypeElement) annotatedElement);
      generate(model);
    }
  }

  private LuaClassModel analyze(TypeElement moduleElement) throws ProcessingException {
    LuaClassModel model = LuaClassModel.of(moduleElement, processingEnv);
    List<ExecutableElement> methods = getRelevantMethods(moduleElement);
    for (ExecutableElement method : methods) {
      if (method.getAnnotation(LuaProperty.class) != null) {
        PropertyModel property = PropertyModel.of(method, processingEnv);
        model.addProperty(property);
      }
      if (method.getAnnotation(LuaFunction.class) != null) {
        FunctionModel function = FunctionModel.of(method);
        model.addFunction(function);
      }
      if (method.getAnnotation(OnCreateLuaProxy.class) != null) {
        model.addOnCreateLuaProxy(method);
      }
      if (method.getAnnotation(OnLoadLuaClass.class) != null) {
        model.addOnLoadLuaClass(method);
      }
    }
    return model;
  }

  private void generate(LuaClassModel module) {
    JavaFile luaProxy = new LuaProxyGenerator(module).generate();
    JavaFile luaClass = new LuaClassGenerator(module, processingEnv).generate();

    Filer filer = processingEnv.getFiler();
    try {
      write(luaProxy, filer);
      write(luaClass, filer);
    } catch (IOException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }

  /**
   * Returns all methods in that are declare in {@code moduleElement} or it's super classes. If a
   * super class is itself a {@link GenerateLuaClass} then its methods and those of its superclasses
   * are ignored, because they are already exported to Lua.
   *
   * @param moduleElement
   * @param methods
   * @return all methods in that are declare in {@code moduleElement} or it's super classes
   */
  public static List<ExecutableElement> getRelevantMethods(TypeElement moduleElement) {
    List<ExecutableElement> methods = new ArrayList<>();
    while (true) {
      methods.addAll(methodsIn(moduleElement.getEnclosedElements()));

      TypeMirror superclass = moduleElement.getSuperclass();
      if (superclass.getKind() == TypeKind.DECLARED) {
        DeclaredType superType = (DeclaredType) superclass;
        moduleElement = (TypeElement) superType.asElement();
        if (moduleElement.getAnnotation(GenerateLuaClass.class) != null) {
          return methods;
        }
      } else {
        return methods;
      }
    }
  }
}
