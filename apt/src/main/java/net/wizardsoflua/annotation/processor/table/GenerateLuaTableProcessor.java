package net.wizardsoflua.annotation.processor.table;

import static net.wizardsoflua.annotation.processor.ProcessorUtils.write;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
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
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;

import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaTable;
import net.wizardsoflua.annotation.processor.ExceptionHandlingProcessor;
import net.wizardsoflua.annotation.processor.MultipleProcessingExceptions;
import net.wizardsoflua.annotation.processor.ProcessingException;
import net.wizardsoflua.annotation.processor.table.generator.LuaTableGenerator;
import net.wizardsoflua.annotation.processor.table.model.LuaTableModel;

@AutoService(Processor.class)
public class GenerateLuaTableProcessor extends ExceptionHandlingProcessor {
  public static final AnnotationSpec GENERATED_ANNOTATION = AnnotationSpec.builder(Generated.class)//
      .addMember("value", "$S", GenerateLuaTable.class.getSimpleName())//
      .build();

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    HashSet<String> result = new HashSet<>();
    result.add(GenerateLuaTable.class.getName());
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
      LuaTableModel model = LuaTableModel.of((TypeElement) annotatedElement, processingEnv);
      generate(model);
    }
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

  /**
   * Returns all elements in that are declared in {@code moduleElement} or it's super classes. If a
   * super class is itself a {@link GenerateLuaClass} then its elements and those of its
   * superclasses are ignored, because they are already exported to Lua.
   *
   * @param moduleElement
   * @return all elements in that are declared in {@code moduleElement} or it's super classes
   */
  public static List<Element> getRelevantElements(TypeElement moduleElement) {
    List<Element> elements = new ArrayList<>();
    while (true) {
      elements.addAll(moduleElement.getEnclosedElements());

      TypeMirror superclass = moduleElement.getSuperclass();
      if (superclass.getKind() == TypeKind.DECLARED) {
        DeclaredType superType = (DeclaredType) superclass;
        moduleElement = (TypeElement) superType.asElement();
        if (moduleElement.getAnnotation(GenerateLuaClass.class) != null) {
          return elements;
        }
      } else {
        return elements;
      }
    }
  }

}
