package net.wizardsoflua.annotation.processor.doc;

import static javax.tools.StandardLocation.SOURCE_OUTPUT;
import static net.wizardsoflua.annotation.processor.Constants.DECLARE_LUA_CLASS;
import static net.wizardsoflua.annotation.processor.Constants.JAVA_LUA_CLASS;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAnnotationMirror;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAnnotationValue;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getTypeParameter;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
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
  private Map<Name, String> luaClassNames = new HashMap<>();

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    HashSet<String> result = new HashSet<>();
    result.add(DECLARE_LUA_CLASS);
    result.add(GenerateLuaDoc.class.getName());
    return result;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  protected void doProcess(TypeElement annotation, Element annotatedElement,
      RoundEnvironment roundEnv)
      throws ProcessingException, MultipleProcessingExceptions, IOException {
    if (annotation.getQualifiedName().contentEquals(DECLARE_LUA_CLASS)
        && annotatedElement.getKind() == ElementKind.CLASS) {
      registerLuaClass(annotatedElement);
    }
    if (annotation.getQualifiedName().contentEquals(GenerateLuaDoc.class.getName())
        && annotatedElement.getKind() == ElementKind.CLASS) {
      LuaDocModel module = analyze((TypeElement) annotatedElement);
      generate(module);
    }
  }

  private void registerLuaClass(Element annotatedElement) throws IOException {
    TypeElement typeElement = (TypeElement) annotatedElement;
    DeclaredType type = (DeclaredType) typeElement.asType();
    TypeMirror javaType = getTypeParameter(type, JAVA_LUA_CLASS, 0, processingEnv);
    DeclaredType javaClass = (DeclaredType) javaType;
    TypeElement javaElement = (TypeElement) javaClass.asElement();
    AnnotationMirror declareLuaClass = getAnnotationMirror(annotatedElement, DECLARE_LUA_CLASS);
    String name = (String) getAnnotationValue(declareLuaClass, "name", processingEnv).getValue();
    luaClassNames.put(javaElement.getQualifiedName(), name);
  }

  @Override
  protected void processingOver() {
    Filer filer = processingEnv.getFiler();
    CharSequence fileContent;
    try {
      fileContent =
          filer.getResource(SOURCE_OUTPUT, "net.wizardsoflua.lua.classes", "lua-classes.properties")
              .getCharContent(false);
    } catch (IOException ex) {
      fileContent = "";
    }
    try (Writer w = filer
        .createResource(SOURCE_OUTPUT, "net.wizardsoflua.lua.classes", "lua-classes.properties")
        .openWriter();) {
      w.append(fileContent);
      for (Entry<Name, String> entry : luaClassNames.entrySet()) {
        w.append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
      }
    } catch (IOException ex) {
      throw new UndeclaredThrowableException(ex);
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
    try (Writer docWriter = filer.createResource(StandardLocation.SOURCE_OUTPUT,
        model.getPackageName(), model.getName() + ".md").openWriter()) {
      docWriter.write(luaDoc);
    } catch (IOException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }
}
