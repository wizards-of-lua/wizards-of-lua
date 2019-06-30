package net.wizardsoflua.annotation.processor.doc;

import static javax.tools.StandardLocation.SOURCE_OUTPUT;
import static net.wizardsoflua.annotation.processor.Constants.JAVA_TO_LUA_CONVERTER;
import static net.wizardsoflua.annotation.processor.Constants.LUA_CONVERTER_ATTRIBUTES;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAnnotationMirror;
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
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.StandardLocation;
import com.google.auto.service.AutoService;
import com.google.common.collect.Maps;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.processor.ExceptionHandlingProcessor;
import net.wizardsoflua.annotation.processor.MultipleProcessingExceptions;
import net.wizardsoflua.annotation.processor.ProcessingException;
import net.wizardsoflua.annotation.processor.ProcessorUtils;
import net.wizardsoflua.annotation.processor.doc.generator.LuaDocGenerator;
import net.wizardsoflua.annotation.processor.doc.model.LuaDocModel;

@AutoService(Processor.class)
public class GenerateLuaDocProcessor extends ExceptionHandlingProcessor {
  private @Nullable Map<String, String> luaTypeNames;

  public Map<String, String> getLuaTypeNames() {
    if (luaTypeNames == null) {
      Filer filer = processingEnv.getFiler();
      Properties properties = new Properties();
      try (InputStream in = filer
          .getResource(PROPERTY_LOCATION, PROPERTY_PKG, PROPERTY_RELATIVE_NAME).openInputStream()) {
        properties.load(in);
      } catch (IOException ignore) {
      }
      luaTypeNames = new TreeMap<>(Maps.fromProperties(properties));
    }
    return luaTypeNames;
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    HashSet<String> result = new HashSet<>();
    result.add(GenerateLuaDoc.class.getName());
    result.add(LUA_CONVERTER_ATTRIBUTES);
    return result;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  protected void doProcess(TypeElement annotation, TypeElement annotatedElement,
      RoundEnvironment roundEnv)
      throws ProcessingException, MultipleProcessingExceptions, IOException {
    if (annotation.getQualifiedName().contentEquals(GenerateLuaDoc.class.getName())) {
      registerLuaType(annotatedElement);
      LuaDocModel module = LuaDocModel.of(annotatedElement, getLuaTypeNames(), processingEnv);
      generate(module);
    } else if (annotation.getQualifiedName().contentEquals(LUA_CONVERTER_ATTRIBUTES)) {
      registerLuaType(annotatedElement);
    }
  }

  private void registerLuaType(TypeElement annotatedElement)
      throws ProcessingException, IOException {
    TypeMirror annotatedType = annotatedElement.asType();
    TypeMirror javaType = getTypeParameter(annotatedType, JAVA_TO_LUA_CONVERTER, 0, processingEnv);
    if (javaType == null) {
      return;
    }
    DeclaredType javaClass = getUpperBound(javaType);
    TypeElement javaElement = (TypeElement) javaClass.asElement();
    String name = getName(annotatedElement);
    getLuaTypeNames().put(javaElement.getQualifiedName().toString(), name);
  }

  private String getName(TypeElement annotatedElement) throws ProcessingException {
    AnnotationMirror mirror = getAnnotationMirror(annotatedElement, LUA_CONVERTER_ATTRIBUTES);
    if (mirror != null) {
      return (String) ProcessorUtils.getAnnotationValue(mirror, "name", processingEnv).getValue();
    } else {
      return LuaDocModel.getName(annotatedElement, processingEnv);
    }
  }

  private static final StandardLocation PROPERTY_LOCATION = SOURCE_OUTPUT;
  private static final String PROPERTY_PKG = "net.wizardsoflua.lua.classes";
  private static final String PROPERTY_RELATIVE_NAME = "lua-classes.properties";

  @Override
  protected void processingOver() {
    Filer filer = processingEnv.getFiler();
    Properties properties = new Properties();
    properties.putAll(getLuaTypeNames());
    try (OutputStream out =
        filer.createResource(PROPERTY_LOCATION, PROPERTY_PKG, PROPERTY_RELATIVE_NAME)
            .openOutputStream()) {
      properties.store(out, null);
    } catch (IOException ex) {
      throw new UndeclaredThrowableException(ex);
    }
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
