package net.wizardsoflua.annotation.processor.doc.model;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static javax.lang.model.util.ElementFilter.methodsIn;
import static javax.lang.model.util.ElementFilter.typesIn;
import static net.wizardsoflua.annotation.processor.Constants.DECLARE_LUA_CLASS_NAME;
import static net.wizardsoflua.annotation.processor.Constants.OBJECT_CLASS;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.checkAnnotated;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAnnotationMirror;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAnnotationValue;
import static net.wizardsoflua.annotation.processor.Utils.getQualifiedName;
import static net.wizardsoflua.annotation.processor.luaclass.GenerateLuaClassProcessor.getRelevantElements;
import static net.wizardsoflua.annotation.processor.luaclass.model.LuaClassModel.getSuperClassAndInstance;
import static net.wizardsoflua.annotation.processor.table.model.LuaTableModel.relevantElements;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;

import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModule;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
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
    LuaDocType type = LuaDocType.MODULE;
    String superClass = null;
    List<? extends Element> elements = annotatedElement.getEnclosedElements();
    return of(annotatedElement, name, type, superClass, elements, luaClassNames, env);
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

    AnnotationMirror mirror = getAnnotationMirror(annotatedElement, HasLuaClass.class);
    DeclaredType luaClassType = ProcessorUtils.getClassValue(mirror, HasLuaClass.LUA_CLASS, env);
    TypeElement luaClassElement = (TypeElement) luaClassType.asElement();
    String name = getLuaClassName(luaClassElement, annotatedElement, env);

    return forLuaClass(annotatedElement, name, luaClassNames, env);
  }

  private static LuaDocModel forLuaClass(TypeElement annotatedElement, String name,
      Map<String, String> luaClassNames, ProcessingEnvironment env)
      throws ProcessingException, MultipleProcessingExceptions {
    LuaDocType type = LuaDocType.CLASS;

    Entry<ClassName, ClassName> superClassAndInstance =
        getSuperClassAndInstance(annotatedElement, env);
    ClassName superClassName = superClassAndInstance.getKey();
    String superClass;
    if (OBJECT_CLASS.equals(superClassName)) {
      superClass = null;
    } else {
      String qualifiedName = getQualifiedName(superClassName);
      TypeElement superElement = env.getElementUtils().getTypeElement(qualifiedName);
      checkState(superElement != null, "Could not find superclass " + qualifiedName);

      superClass = getLuaClassName(superElement, annotatedElement, env);
    }

    List<Element> elements = getRelevantElements(annotatedElement);

    return of(annotatedElement, name, type, superClass, elements, luaClassNames, env);
  }

  private static String getLuaClassName(TypeElement luaClassElement, TypeElement annotatedElement,
      ProcessingEnvironment env) throws ProcessingException {
    AnnotationMirror annotation = getAnnotationMirror(luaClassElement, DECLARE_LUA_CLASS_NAME);
    if (annotation == null) {
      String msg = "The class " + luaClassElement.getQualifiedName() + " must be annotated with @"
          + DECLARE_LUA_CLASS_NAME;
      AnnotationMirror mirror = getAnnotationMirror(annotatedElement, GenerateLuaDoc.class);
      throw new ProcessingException(msg, annotatedElement, mirror);
    }
    return (String) getAnnotationValue(annotation, "name", env).getValue();
  }

  private static LuaDocModel of(TypeElement annotatedElement, String name, LuaDocType type,
      String superClass, Iterable<? extends Element> elements, Map<String, String> luaClassNames,
      ProcessingEnvironment env) throws ProcessingException, MultipleProcessingExceptions {
    CharSequence packageName =
        env.getElementUtils().getPackageOf(annotatedElement).getQualifiedName();

    GenerateLuaDoc generateLuaDoc = checkAnnotated(annotatedElement, GenerateLuaDoc.class);
    String subtitle = generateLuaDoc.subtitle();

    String description = LuaDocGenerator.getDescription(annotatedElement, env);

    Map<String, PropertyDocModel> properties = new TreeMap<>();
    Map<String, FunctionDocModel> functions = new TreeMap<>();

    for (ExecutableElement method : methodsIn(elements)) {
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
    for (TypeElement typeElement : typesIn(elements)) {
      if (typeElement.getAnnotation(LuaFunction.class) != null) {
        FunctionDocModel function = FunctionDocModel.of(typeElement, env);
        functions.put(function.getName(), function);
      }
    }
    return new LuaDocModel(packageName, name, subtitle, type, superClass, description,
        properties.values(), functions.values());
  }

  public static LuaDocModel of(TypeElement annotatedElement, Map<String, String> luaClassNames,
      ProcessingEnvironment env) throws ProcessingException, MultipleProcessingExceptions {
    GenerateLuaDoc annotation = checkAnnotated(annotatedElement, GenerateLuaDoc.class);
    Elements elements = env.getElementUtils();
    CharSequence packageName = elements.getPackageOf(annotatedElement).getQualifiedName();
    String name = annotation.name();
    String subtitle = annotation.subtitle();
    LuaDocType type = getType(annotatedElement);
    String superClass = null; // TODO Adrodoc55 12.04.2018: superClass
    String description = LuaDocGenerator.getDescription(annotatedElement, env);
    Map<String, PropertyDocModel> properties = new TreeMap<>();
    Map<String, FunctionDocModel> functions = new TreeMap<>();

    Iterator<? extends Element> it = relevantElements(annotatedElement, env).iterator();
    while (it.hasNext()) {
      Element element = it.next();
      ElementKind kind = element.getKind();
      if (kind == ElementKind.METHOD) {
        ExecutableElement method = (ExecutableElement) element;
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
      } else if (kind == ElementKind.CLASS) {
        TypeElement typeElement = (TypeElement) element;
        if (typeElement.getAnnotation(LuaFunction.class) != null) {
          FunctionDocModel function = FunctionDocModel.of(typeElement, env);
          functions.put(function.getName(), function);
        }
      }
    }
    return new LuaDocModel(packageName, name, subtitle, type, superClass, description,
        properties.values(), functions.values());
  }

  private static LuaDocType getType(TypeElement annotatedElement) {
    if (annotatedElement.getAnnotation(GenerateLuaClassTable.class) != null) {
      return LuaDocType.CLASS;
    } else if (annotatedElement.getAnnotation(GenerateLuaModuleTable.class) != null) {
      return LuaDocType.MODULE;
    } else {
      throw new IllegalArgumentException(annotatedElement + " is not annotated with @"
          + GenerateLuaClassTable.class.getSimpleName() + " or @" + GenerateLuaModuleTable.class);
    }
  }

  private final CharSequence packageName;
  private final String name;
  private final @Nullable String superClass;
  private final String subtitle;
  private final LuaDocType type;
  private final String description;
  private final ImmutableSet<PropertyDocModel> properties;
  private final ImmutableSet<FunctionDocModel> functions;

  public LuaDocModel(CharSequence packageName, String name, String subtitle, LuaDocType type,
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

  public LuaDocType getType() {
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
