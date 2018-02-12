package net.wizardsoflua.annotation.processor.proxy;

import static javax.lang.model.util.ElementFilter.methodsIn;
import static javax.lang.model.util.ElementFilter.typesIn;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Generated;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;

import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaModule;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.annotation.processor.model.FunctionModel;
import net.wizardsoflua.annotation.processor.model.ModuleModel;
import net.wizardsoflua.annotation.processor.model.PropertyModel;

public class LuaApiProcessor extends AbstractProcessor {
  public static final AnnotationSpec GENERATED_ANNOTATION = AnnotationSpec.builder(Generated.class)//
      .addMember("value", "$S", "LuaApi")//
      .build();

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

  private final Map<TypeElement, Exception> retryNextRound = new HashMap<>();
  private final Map<Element, Exception> failedInLastRound = new HashMap<>();

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (!roundEnv.processingOver()) {
      Collection<ModuleModel> modules = analyze(annotations, roundEnv);
      generate(modules);
    } else {
      for (Entry<Element, Exception> entry : failedInLastRound.entrySet()) {
        Exception ex = entry.getValue();
        CharSequence message = ex.getMessage();
        message = message != null ? message : ex.toString();
        Element element = entry.getKey();
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
        ex.printStackTrace();
      }
    }
    return false;
  }

  private Collection<ModuleModel> analyze(Set<? extends TypeElement> annotations,
      RoundEnvironment roundEnv) {
    Collection<ModuleModel> modules = new ArrayList<>();
    for (TypeElement annotation : annotations) {
      Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
      Set<TypeElement> moduleElements = typesIn(annotatedElements);
      moduleElements.addAll(retryNextRound.keySet());
      retryNextRound.clear();
      failedInLastRound.clear();
      for (TypeElement moduleElement : moduleElements) {
        try {
          ModuleModel module = analyze(moduleElement);
          modules.add(module);
        } catch (Exception ex) {
          retryNextRound.put(moduleElement, ex);
          failedInLastRound.put(moduleElement, ex);
        }
      }
    }
    return modules;
  }

  private ModuleModel analyze(TypeElement moduleElement) {
    Elements elements = processingEnv.getElementUtils();
    ModuleModel module = ModuleModel.of(moduleElement, processingEnv);
    List<ExecutableElement> methods = getRelevantMethods(moduleElement);

    for (ExecutableElement method : methods) {
      try {
        LuaProperty luaProperty = method.getAnnotation(LuaProperty.class);
        if (luaProperty != null) {
          String docComment = elements.getDocComment(method);
          PropertyModel property = PropertyModel.of(method, luaProperty, docComment, processingEnv);
          module.addProperty(property, processingEnv);
        }
        LuaFunction luaFunction = method.getAnnotation(LuaFunction.class);
        if (luaFunction != null) {
          String docComment = elements.getDocComment(method);
          FunctionModel function = FunctionModel.of(method, luaFunction, docComment);
          module.addFunction(function);
        }
      } catch (Exception ex) {
        failedInLastRound.put(method, ex);
      }
    }
    return module;
  }

  /**
   * Returns all methods in that are declare in {@code moduleElement} or it's super classes. If a
   * super class is itself a {@link LuaModule} then its methods and those of its superclasses are
   * ignored, because they are already exported to Lua.
   *
   * @param moduleElement
   * @param methods
   * @return all methods in that are declare in {@code moduleElement} or it's super classes
   */
  private List<ExecutableElement> getRelevantMethods(TypeElement moduleElement) {
    List<ExecutableElement> methods = new ArrayList<>();
    while (true) {
      methods.addAll(methodsIn(moduleElement.getEnclosedElements()));

      TypeMirror superclass = moduleElement.getSuperclass();
      if (superclass.getKind() == TypeKind.DECLARED) {
        DeclaredType superType = (DeclaredType) superclass;
        moduleElement = (TypeElement) superType.asElement();
        if (moduleElement.getAnnotation(LuaModule.class) != null) {
          return methods;
        }
      } else {
        return methods;
      }
    }
  }

  private void generate(Collection<ModuleModel> modules) {
    for (ModuleModel module : modules) {
      JavaFile luaProxy = new LuaProxyGenerator(module).generate();
      JavaFile luaClass = new LuaClassGenerator(module).generate();

      Filer filer = processingEnv.getFiler();
      try {
        write(luaProxy, filer);
        write(luaClass, filer);
      } catch (IOException ex) {
        throw new UndeclaredThrowableException(ex);
      }
    }
  }

  private void write(JavaFile file, Filer filer) throws IOException {
    String qualifiedName = file.packageName + '.' + file.typeSpec.name;
    try (Writer writer = new BufferedWriter(filer.createSourceFile(qualifiedName).openWriter())) {
      file.writeTo(writer);
    }
  }
}
