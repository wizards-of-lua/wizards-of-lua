package net.wizardsoflua.annotation.processor.doc;

import static javax.tools.StandardLocation.SOURCE_OUTPUT;
import static net.wizardsoflua.annotation.processor.Constants.DELEGATOR;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAnnotationMirror;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getClassValue;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getTypeParameter;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getUpperBound;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Nullable;
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
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.StandardLocation;

import com.google.common.collect.Maps;

import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModule;
import net.wizardsoflua.annotation.HasLuaClass;
import net.wizardsoflua.annotation.processor.ExceptionHandlingProcessor;
import net.wizardsoflua.annotation.processor.MultipleProcessingExceptions;
import net.wizardsoflua.annotation.processor.ProcessingException;
import net.wizardsoflua.annotation.processor.doc.generator.LuaDocGenerator;
import net.wizardsoflua.annotation.processor.doc.model.LuaDocModel;

// @AutoService(Processor.class)
public class GenerateLuaDocProcessor extends ExceptionHandlingProcessor {
  private @Nullable Map<String, String> luaClassNames;

  public Map<String, String> getLuaClassNames() {
    if (luaClassNames == null) {
      Filer filer = processingEnv.getFiler();
      Properties properties = new Properties();
      try (InputStream in = filer
          .getResource(PROPERTY_LOCATION, PROPERTY_PKG, PROPERTY_RELATIVE_NAME).openInputStream()) {
        properties.load(in);
      } catch (IOException ignore) {
      }
      luaClassNames = new TreeMap<>(Maps.fromProperties(properties));
    }
    return luaClassNames;
  }

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
      RoundEnvironment roundEnv)
      throws ProcessingException, MultipleProcessingExceptions, IOException {
    if (annotation.getQualifiedName().contentEquals(GenerateLuaDoc.class.getName())
        && annotatedElement.getKind() == ElementKind.CLASS) {
      TypeElement typeElement = (TypeElement) annotatedElement;
      registerLuaClass(typeElement);
      LuaDocModel module = analyze(typeElement);
      generate(module);
    }
  }

  private void registerLuaClass(TypeElement annotatedElement)
      throws ProcessingException, IOException {
    AnnotationMirror mirror = getAnnotationMirror(annotatedElement, GenerateLuaClassTable.class);
    if (mirror == null) {
      return;
    }
    DeclaredType instance = getClassValue(mirror, GenerateLuaClassTable.INSTANCE, processingEnv);
    TypeMirror derasured = derasure(instance);
    TypeMirror javaType = getTypeParameter(derasured, DELEGATOR, 0, processingEnv);
    DeclaredType javaClass = getUpperBound(javaType);
    TypeElement javaElement = (TypeElement) javaClass.asElement();
    String name = LuaDocModel.getName(annotatedElement, processingEnv);
    getLuaClassNames().put(javaElement.getQualifiedName().toString(), name);
  }

  /**
   * Opposite of {@link Types#erasure(TypeMirror)} for {@link DeclaredType}s.
   *
   * @param type
   * @return the de-erased type
   */
  private TypeMirror derasure(DeclaredType type) {
    TypeElement instanceElement = (TypeElement) type.asElement();
    Elements elements = processingEnv.getElementUtils();
    Name qualifiedName = instanceElement.getQualifiedName();
    TypeElement capturedElement = elements.getTypeElement(qualifiedName);
    return capturedElement.asType();
  }

  private static final StandardLocation PROPERTY_LOCATION = SOURCE_OUTPUT;
  private static final String PROPERTY_PKG = "net.wizardsoflua.lua.classes";
  private static final String PROPERTY_RELATIVE_NAME = "lua-classes.properties";

  @Override
  protected void processingOver() {
    Filer filer = processingEnv.getFiler();
    Properties properties = new Properties();
    properties.putAll(getLuaClassNames());
    try (OutputStream out =
        filer.createResource(PROPERTY_LOCATION, PROPERTY_PKG, PROPERTY_RELATIVE_NAME)
            .openOutputStream()) {
      properties.store(out, null);
    } catch (IOException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }

  private LuaDocModel analyze(TypeElement annotatedElement)
      throws ProcessingException, MultipleProcessingExceptions {
    if (annotatedElement.getAnnotation(GenerateLuaModule.class) != null) {
      return LuaDocModel.forLuaModule(annotatedElement, getLuaClassNames(), processingEnv);
    }
    if (annotatedElement.getAnnotation(GenerateLuaClass.class) != null) {
      return LuaDocModel.forGeneratedLuaClass(annotatedElement, getLuaClassNames(), processingEnv);
    }
    if (annotatedElement.getAnnotation(HasLuaClass.class) != null) {
      return LuaDocModel.forManualLuaClass(annotatedElement, getLuaClassNames(), processingEnv);
    }
    return LuaDocModel.of(annotatedElement, getLuaClassNames(), processingEnv);
  }

  private void generate(LuaDocModel model) {
    String luaDoc = new LuaDocGenerator(model).generate();

    Filer filer = processingEnv.getFiler();
    try (Writer docWriter = filer.createResource(StandardLocation.SOURCE_OUTPUT,
        model.getPackageName(), model.getName() + ".md").openWriter()) {
      docWriter.write(luaDoc);
    } catch (IOException ex) {
      throw new UndeclaredThrowableException(ex);
    }
  }
}
