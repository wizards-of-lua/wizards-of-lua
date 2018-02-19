package net.wizardsoflua.annotation.processor.doc.model;

import static java.util.Objects.requireNonNull;
import static javax.lang.model.util.ElementFilter.methodsIn;
import static net.wizardsoflua.annotation.processor.Constants.DECLARE_LUA_CLASS;
import static net.wizardsoflua.annotation.processor.Constants.OBJECT_CLASS;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.checkAnnotated;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAnnotationMirror;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAnnotationValue;
import static net.wizardsoflua.annotation.processor.luaclass.GenerateLuaClassProcessor.getRelevantMethods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModule;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.annotation.processor.MultipleProcessingExceptions;
import net.wizardsoflua.annotation.processor.ProcessingException;
import net.wizardsoflua.annotation.processor.doc.generator.LuaDocGenerator;

public class LuaDocModel {
  public static LuaDocModel forLuaClass(TypeElement annotatedElement, ProcessingEnvironment env)
      throws ProcessingException, MultipleProcessingExceptions {
    GenerateLuaClass generateLuaClass = checkAnnotated(annotatedElement, GenerateLuaClass.class);
    String name = generateLuaClass.name();

    AnnotationMirror mirror = getAnnotationMirror(annotatedElement, GenerateLuaClass.class);
    AnnotationValue superClassValue = getAnnotationValue(mirror, "superClass", env);
    DeclaredType superType = (DeclaredType) superClassValue.getValue();
    TypeElement superElement = (TypeElement) superType.asElement();
    String superClass;
    if (superElement.getQualifiedName().contentEquals(OBJECT_CLASS)) {
      superClass = null;
    } else {
      AnnotationMirror superClassAnnotation = getAnnotationMirror(superElement, DECLARE_LUA_CLASS);
      if (superClassAnnotation == null) {
        String msg = "The super class must be " + OBJECT_CLASS + " or be annotated with @"
            + DECLARE_LUA_CLASS;
        throw new ProcessingException(msg, annotatedElement, mirror, superClassValue);
      }
      superClass = (String) getAnnotationValue(superClassAnnotation, "name", env).getValue();
    }

    List<ExecutableElement> methods = getRelevantMethods(annotatedElement);

    return of(annotatedElement, name, superClass, methods, env);
  }

  public static LuaDocModel forLuaModule(TypeElement annotatedElement, ProcessingEnvironment env)
      throws ProcessingException, MultipleProcessingExceptions {
    GenerateLuaModule generateLuaClass = checkAnnotated(annotatedElement, GenerateLuaModule.class);
    String name = generateLuaClass.name();
    String superClass = null;
    List<ExecutableElement> methods = methodsIn(annotatedElement.getEnclosedElements());
    return of(annotatedElement, name, superClass, methods, env);
  }

  private static LuaDocModel of(TypeElement annotatedElement, String name, String superClass,
      List<ExecutableElement> methods, ProcessingEnvironment env)
      throws ProcessingException, MultipleProcessingExceptions {
    CharSequence packageName =
        env.getElementUtils().getPackageOf(annotatedElement).getQualifiedName();

    GenerateLuaDoc generateLuaDoc = checkAnnotated(annotatedElement, GenerateLuaDoc.class);
    String subtitle = generateLuaDoc.subtitle();

    String description = LuaDocGenerator.getDescription(annotatedElement, env);

    Map<String, PropertyDocModel> properties = new HashMap<>();
    Collection<FunctionDocModel> functions = new ArrayList<>();

    for (ExecutableElement method : methods) {
      if (method.getAnnotation(LuaProperty.class) != null) {
        PropertyDocModel property = PropertyDocModel.of(method, env);
        PropertyDocModel existingProperty = properties.remove(property.getName());
        if (existingProperty != null) {
          property = existingProperty.merge(property);
        }
        properties.put(property.getName(), property);
      }
      if (method.getAnnotation(LuaFunction.class) != null) {
        functions.add(FunctionDocModel.of(method, env));
      }
    }
    return new LuaDocModel(packageName, name, subtitle, superClass, description,
        properties.values(), functions);
  }

  private final CharSequence packageName;
  private final String name;
  private final @Nullable String superClass;
  private final String subtitle;
  private final String description;
  private final ImmutableSet<PropertyDocModel> properties;
  private final ImmutableSet<FunctionDocModel> functions;

  public LuaDocModel(CharSequence packageName, String name, String subtitle,
      @Nullable String superClass, String description, Iterable<PropertyDocModel> properties,
      Iterable<FunctionDocModel> functions) {
    this.packageName = requireNonNull(packageName, "packageName == null!");
    this.name = requireNonNull(name, "name == null!");
    this.subtitle = requireNonNull(subtitle, "subtitle == null!");
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
