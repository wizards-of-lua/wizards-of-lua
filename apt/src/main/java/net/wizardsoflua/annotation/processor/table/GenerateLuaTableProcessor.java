package net.wizardsoflua.annotation.processor.table;

import static net.wizardsoflua.annotation.processor.ProcessorUtils.write;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;

import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.processor.ExceptionHandlingProcessor;
import net.wizardsoflua.annotation.processor.MultipleProcessingExceptions;
import net.wizardsoflua.annotation.processor.ProcessingException;
import net.wizardsoflua.annotation.processor.table.generator.LuaTableGenerator;
import net.wizardsoflua.annotation.processor.table.model.LuaTableModel;

@AutoService(Processor.class)
public class GenerateLuaTableProcessor extends ExceptionHandlingProcessor {
  public static final AnnotationSpec GENERATED_ANNOTATION = AnnotationSpec.builder(Generated.class)//
      .addMember("value", "$S", GenerateLuaTableProcessor.class.getSimpleName())//
      .build();

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    HashSet<String> result = new HashSet<>();
    result.add(GenerateLuaClassTable.class.getName());
    result.add(GenerateLuaInstanceTable.class.getName());
    result.add(GenerateLuaModuleTable.class.getName());
    return result;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  protected void doProcess(TypeElement annotation, Element annotatedElement,
      RoundEnvironment roundEnv) throws ProcessingException, MultipleProcessingExceptions {
    if (annotatedElement.getKind() == ElementKind.CLASS) {
      LuaTableModel model = analyze((TypeElement) annotatedElement);
      generate(model);
    }
  }

  private LuaTableModel analyze(TypeElement annotatedElement) throws ProcessingException {
    return LuaTableModel.of(annotatedElement, processingEnv);
  }

  private void generate(LuaTableModel model) {
    JavaFile luaTable = new LuaTableGenerator(model, processingEnv).generate();

    Filer filer = processingEnv.getFiler();
    try {
      write(luaTable, filer);
    } catch (IOException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }
}
