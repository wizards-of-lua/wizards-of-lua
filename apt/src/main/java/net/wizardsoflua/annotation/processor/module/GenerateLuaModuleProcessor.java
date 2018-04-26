package net.wizardsoflua.annotation.processor.module;

import static javax.lang.model.util.ElementFilter.methodsIn;
import static javax.lang.model.util.ElementFilter.typesIn;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.write;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;

import net.wizardsoflua.annotation.GenerateLuaModule;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.annotation.processor.ExceptionHandlingProcessor;
import net.wizardsoflua.annotation.processor.ProcessingException;
import net.wizardsoflua.annotation.processor.model.FunctionModel;
import net.wizardsoflua.annotation.processor.model.ManualFunctionModel;
import net.wizardsoflua.annotation.processor.model.PropertyModel;
import net.wizardsoflua.annotation.processor.module.generator.LuaModuleGenerator;
import net.wizardsoflua.annotation.processor.module.model.LuaModuleModel;

@AutoService(Processor.class)
public class GenerateLuaModuleProcessor extends ExceptionHandlingProcessor {
  public static final AnnotationSpec GENERATED_ANNOTATION = AnnotationSpec.builder(Generated.class)//
      .addMember("value", "$S", GenerateLuaModule.class.getSimpleName())//
      .build();

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    HashSet<String> result = new HashSet<>();
    result.add(GenerateLuaModule.class.getName());
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
      LuaModuleModel model = analyze((TypeElement) annotatedElement);
      generate(model);
    }
  }

  private LuaModuleModel analyze(TypeElement moduleElement) throws ProcessingException {
    LuaModuleModel model = LuaModuleModel.of(moduleElement, processingEnv);
    List<? extends Element> elements = moduleElement.getEnclosedElements();
    List<ExecutableElement> methods = methodsIn(elements);
    for (ExecutableElement method : methods) {
      if (method.getAnnotation(LuaProperty.class) != null) {
        PropertyModel property = PropertyModel.of(method, processingEnv);
        model.addProperty(property);
      }
      if (method.getAnnotation(LuaFunction.class) != null) {
        FunctionModel function = FunctionModel.of(method);
        model.addFunction(function);
      }
    }
    List<TypeElement> types = typesIn(elements);
    for (TypeElement typeElement : types) {
      if (typeElement.getAnnotation(LuaFunction.class) != null) {
        ManualFunctionModel function = ManualFunctionModel.of(typeElement);
        model.addManualFunction(function);
      }
    }
    return model;
  }

  private void generate(LuaModuleModel model) {
    JavaFile luaModule = new LuaModuleGenerator(model, processingEnv).generate();
    Filer filer = processingEnv.getFiler();
    try {
      write(luaModule, filer);
    } catch (IOException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }
}
