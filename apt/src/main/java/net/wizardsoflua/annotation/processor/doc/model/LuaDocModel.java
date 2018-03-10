package net.wizardsoflua.annotation.processor.doc.model;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static javax.lang.model.util.ElementFilter.methodsIn;
import static net.wizardsoflua.annotation.processor.Constants.DECLARE_LUA_CLASS;
import static net.wizardsoflua.annotation.processor.Constants.OBJECT_CLASS_CLASS_NAME;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.checkAnnotated;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAnnotationMirror;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAnnotationValue;
import static net.wizardsoflua.annotation.processor.Utils.getQualifiedName;
import static net.wizardsoflua.annotation.processor.luaclass.GenerateLuaClassProcessor.getRelevantMethods;
import static net.wizardsoflua.annotation.processor.luaclass.model.LuaClassModel.getSuperClassAndProxy;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;

import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModule;
import net.wizardsoflua.annotation.HasLuaClass;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.annotation.processor.MultipleProcessingExceptions;
import net.wizardsoflua.annotation.processor.ProcessingException;
import net.wizardsoflua.annotation.processor.ProcessorUtils;
import net.wizardsoflua.annotation.processor.doc.generator.LuaDocGenerator;

public class LuaDocModel {
  public static LuaDocModel forLuaModule(TypeElement annotatedElement,
      Map<String, String> luaClassNames, ProcessingEnvironment env)
      throws ProcessingException, MultipleProcessingExceptions {
    GenerateLuaModule generateLuaClass = checkAnnotated(annotatedElement, GenerateLuaModule.class);
    String name = generateLuaClass.name();
    String type = "module";
    String superClass = null;
    List<ExecutableElement> methods = methodsIn(annotatedElement.getEnclosedElements());
    return of(annotatedElement, name, type, superClass, methods, luaClassNames, env);
  }

  public static LuaDocModel forGeneratedLuaClass(TypeElement annotatedElement,
      Map<String, String> luaClassNames, ProcessingEnvironment env)
      throws ProcessingException, MultipleProcessingExceptions {
    GenerateLuaClass generateLuaClass = checkAnnotated(annotatedElement, GenerateLuaClass.class);
    String name = generateLuaClass.name();
    return forLuaClass(annotatedElement, name, luaClassNames, env);
  }

  public static LuaDocModel forManualLuaClass(TypeElement annotatedElement,
      Map<String, String> luaClassNames, ProcessingEnvironment env)
      throws ProcessingException, MultipleProcessingExceptions {
    checkAnnotated(annotatedElement, HasLuaClass.class);

    AnnotationMirror mirror =
        ProcessorUtils.getAnnotationMirror(annotatedElement, HasLuaClass.class);
    DeclaredType luaClassType = ProcessorUtils.getClassValue(mirror, HasLuaClass.LUA_CLASS, env);
    TypeElement luaClassElement = (TypeElement) luaClassType.asElement();
    String name = getLuaClassName(luaClassElement, annotatedElement, env);

    return forLuaClass(annotatedElement, name, luaClassNames, env);
  }

  private static LuaDocModel forLuaClass(TypeElement annotatedElement, String name,
      Map<String, String> luaClassNames, ProcessingEnvironment env)
      throws ProcessingException, MultipleProcessingExceptions {
    Elements elements = env.getElementUtils();

    String type = "class";

    Entry<ClassName, ClassName> superClassAndProxy = getSuperClassAndProxy(annotatedElement, env);
    ClassName superClassName = superClassAndProxy.getKey();
    String superClass;
    if (OBJECT_CLASS_CLASS_NAME.equals(superClassName)) {
      superClass = null;
    } else {
      String qualifiedName = getQualifiedName(superClassName);
      TypeElement superElement = elements.getTypeElement(qualifiedName);
      checkState(superElement != null, "Could not find superclass " + qualifiedName);

      superClass = getLuaClassName(superElement, annotatedElement, env);
    }

    List<ExecutableElement> methods = getRelevantMethods(annotatedElement);

    return of(annotatedElement, name, type, superClass, methods, luaClassNames, env);
  }

  private static String getLuaClassName(TypeElement luaClassElement, TypeElement annotatedElement,
      ProcessingEnvironment env) throws ProcessingException {
    AnnotationMirror annotation = getAnnotationMirror(luaClassElement, DECLARE_LUA_CLASS);
    if (annotation == null) {
      String msg = "The class " + luaClassElement.getQualifiedName() + " must be annotated with @"
          + DECLARE_LUA_CLASS;
      AnnotationMirror mirror = getAnnotationMirror(annotatedElement, GenerateLuaDoc.class);
      throw new ProcessingException(msg, annotatedElement, mirror);
    }
    return (String) getAnnotationValue(annotation, "name", env).getValue();
  }

  private static LuaDocModel of(TypeElement annotatedElement, String name, String type,
      String superClass, List<ExecutableElement> methods, Map<String, String> luaClassNames,
      ProcessingEnvironment env) throws ProcessingException, MultipleProcessingExceptions {
    CharSequence packageName =
        env.getElementUtils().getPackageOf(annotatedElement).getQualifiedName();

    GenerateLuaDoc generateLuaDoc = checkAnnotated(annotatedElement, GenerateLuaDoc.class);
    String subtitle = generateLuaDoc.subtitle();

    String description = LuaDocGenerator.getDescription(annotatedElement, env);

    Map<String, PropertyDocModel> properties = new TreeMap<>();
    Map<String, FunctionDocModel> functions = new TreeMap<>();

    for (ExecutableElement method : methods) {
      if (method.getAnnotation(LuaProperty.class) != null) {
        PropertyDocModel property = PropertyDocModel.of(method, luaClassNames, env);
        PropertyDocModel existingProperty = properties.remove(property.getName());
        if (existingProperty != null) {
          property = existingProperty.merge(property);
        }
        properties.put(property.getName(), property);
      }
      if (method.getAnnotation(LuaFunction.class) != null) {
        FunctionDocModel function = FunctionDocModel.of(method, luaClassNames, env);
        functions.put(function.getName(), function);
      }
    }
    return new LuaDocModel(packageName, name, subtitle, type, superClass, description,
        properties.values(), functions.values());
  }

  private final CharSequence packageName;
  private final String name;
  private final @Nullable String superClass;
  private final String subtitle;
  private final String type;
  private final String description;
  private final ImmutableSet<PropertyDocModel> properties;
  private final ImmutableSet<FunctionDocModel> functions;

  public LuaDocModel(CharSequence packageName, String name, String subtitle, String type,
      @Nullable String superClass, String description, Iterable<PropertyDocModel> properties,
      Iterable<FunctionDocModel> functions) {
    this.packageName = requireNonNull(packageName, "packageName == null!");
    this.name = requireNonNull(name, "name == null!");
    this.subtitle = requireNonNull(subtitle, "subtitle == null!");
    this.type = requireNonNull(type, "type == null!");
    this.superClass = superClass;
    this.description = requireNonNull(description, "description == null!");
    this.properties = ImmutableSet.copyOf(properties);
    this.functions = ImmutableSet.copyOf(functions);
  }

  public CharSequence getPackageName() {
    return packageName;
  }

  public String getName() {
    return name;
  }

  public String getSubtitle() {
    return subtitle;
  }

  public String getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  public @Nullable String getSuperClass() {
    return superClass;
  }

  public ImmutableCollection<PropertyDocModel> getProperties() {
    return properties;
  }

  public ImmutableCollection<FunctionDocModel> getFunctions() {
    return functions;
  }
}
