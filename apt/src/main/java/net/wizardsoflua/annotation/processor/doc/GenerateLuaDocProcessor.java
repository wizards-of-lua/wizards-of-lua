package net.wizardsoflua.annotation.processor.doc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;

import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModule;
import net.wizardsoflua.annotation.processor.ExceptionHandlingProcessor;
import net.wizardsoflua.annotation.processor.MultipleProcessingExceptions;
import net.wizardsoflua.annotation.processor.ProcessingException;
import net.wizardsoflua.annotation.processor.ProcessorUtils;
import net.wizardsoflua.annotation.processor.doc.generator.LuaDocGenerator;
import net.wizardsoflua.annotation.processor.doc.model.LuaDocModel;

public class GenerateLuaDocProcessor extends ExceptionHandlingProcessor {
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    HashSet<String> result = new HashSet<>();
    result.add(GenerateLuaDoc.class.getName());
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
      LuaDocModel module = analyze((TypeElement) annotatedElement);
      generate(module);
    }
  }

  private LuaDocModel analyze(TypeElement annotatedElement)
      throws ProcessingException, MultipleProcessingExceptions {
    GenerateLuaClass generateLuaClass = annotatedElement.getAnnotation(GenerateLuaClass.class);
    if (generateLuaClass != null) {
      return LuaDocModel.forLuaClass(annotatedElement, processingEnv);
    }
    GenerateLuaModule generateLuaModule = annotatedElement.getAnnotation(GenerateLuaModule.class);
    if (generateLuaModule != null) {
      return LuaDocModel.forLuaModule(annotatedElement, processingEnv);
    }
    CharSequence msg = "Luadoc can only be generated if the class is also annotated with @"
        + GenerateLuaClass.class.getSimpleName() + " or @"
        + GenerateLuaModule.class.getSimpleName();
    Element e = annotatedElement;
    AnnotationMirror a = ProcessorUtils.getAnnotationMirror(annotatedElement, GenerateLuaDoc.class);
    throw new ProcessingException(msg, e, a);
  }

  private void generate(LuaDocModel model) {
    String luaDoc = new LuaDocGenerator(model, processingEnv).generate();

    Filer filer = processingEnv.getFiler();
    try (Writer docWriter = new BufferedWriter(filer.createResource(StandardLocation.SOURCE_OUTPUT,
        model.getPackageName(), model.getName() + ".md").openWriter())) {
      docWriter.write(luaDoc);
    } catch (IOException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }
}
