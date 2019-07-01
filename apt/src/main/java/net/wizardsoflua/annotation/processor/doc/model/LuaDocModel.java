package net.wizardsoflua.annotation.processor.doc.model;

import static java.util.Objects.requireNonNull;
import static net.wizardsoflua.annotation.processor.Constants.LUA_CLASS_ATTRIBUTES;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.checkAnnotated;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAnnotationMirror;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAnnotationValue;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.annotation.processor.Constants;
import net.wizardsoflua.annotation.processor.MultipleProcessingExceptions;
import net.wizardsoflua.annotation.processor.ProcessingException;
import net.wizardsoflua.annotation.processor.ProcessorUtils;
import net.wizardsoflua.annotation.processor.doc.generator.LuaDocGenerator;
import net.wizardsoflua.annotation.processor.table.model.LuaTableModel;

public class LuaDocModel {
  public static LuaDocModel of(TypeElement annotatedElement, Map<String, String> luaTypeNames,
      ProcessingEnvironment env) throws ProcessingException, MultipleProcessingExceptions {
    GenerateLuaDoc annotation = checkAnnotated(annotatedElement, GenerateLuaDoc.class);
    String name = getName(annotatedElement, env);
    String title = getTitle(annotatedElement, env);
    String subtitle = annotation.subtitle();
    String type = getType(annotatedElement);
    String superClass = getSuperClassName(annotatedElement, luaTypeNames, env);
    String description = LuaDocGenerator.getDescription(annotatedElement, env);
    Map<String, PropertyDocModel> properties = new TreeMap<>();
    Map<String, FunctionDocModel> functions = new TreeMap<>();

    Iterable<? extends Element> relevantElements = annotatedElement.getEnclosedElements();
    TypeElement additionalElement = LuaTableModel.getAdditionalElement(annotatedElement, env);
    if (additionalElement != null) {
      relevantElements =
          Iterables.concat(relevantElements, additionalElement.getEnclosedElements());
    }
    for (Element element : relevantElements) {
      ElementKind kind = element.getKind();
      if (kind == ElementKind.METHOD) {
        ExecutableElement method = (ExecutableElement) element;
        if (method.getAnnotation(LuaProperty.class) != null) {
          PropertyDocModel property = PropertyDocModel.of(method, luaTypeNames, env);
          PropertyDocModel existingProperty = properties.remove(property.getName());
          if (existingProperty != null) {
            property = existingProperty.merge(property);
          }
          properties.put(property.getName(), property);
        }
        if (method.getAnnotation(LuaFunction.class) != null) {
          FunctionDocModel function = FunctionDocModel.of(method, luaTypeNames, env);
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
    return new LuaDocModel(name, title, subtitle, type, superClass, description,
        properties.values(), functions.values());
  }

  public static String getName(TypeElement annotatedElement, ProcessingEnvironment env)
      throws ProcessingException {
    GenerateLuaDoc annotation = checkAnnotated(annotatedElement, GenerateLuaDoc.class);
    String result = annotation.name();
    if (!result.isEmpty()) {
      return result;
    }
    AnnotationMirror mirror = getAnnotationMirror(annotatedElement, LUA_CLASS_ATTRIBUTES);
    if (mirror != null) {
      return (String) getAnnotationValue(mirror, "name", env).getValue();
    }
    CharSequence msg = "Missing required attribute 'name'";
    Element e = annotatedElement;
    AnnotationMirror a = getAnnotationMirror(annotatedElement, GenerateLuaDoc.class);
    throw new ProcessingException(msg, e, a);
  }

  public static String getTitle(TypeElement annotatedElement, ProcessingEnvironment env)
      throws ProcessingException {
    GenerateLuaDoc annotation = checkAnnotated(annotatedElement, GenerateLuaDoc.class);
    String result = annotation.title();
    return result;
  }

  private static String getType(TypeElement annotatedElement) throws ProcessingException {
    GenerateLuaDoc annotation = checkAnnotated(annotatedElement, GenerateLuaDoc.class);
    String result = annotation.type();
    if (!result.isEmpty()) {
      return result;
    } else if (annotatedElement.getAnnotation(GenerateLuaClassTable.class) != null) {
      return "class";
    } else if (annotatedElement.getAnnotation(GenerateLuaModuleTable.class) != null) {
      return "module";
    } else {
      CharSequence msg = "Missing required attribute 'type'";
      Element e = annotatedElement;
      AnnotationMirror a = getAnnotationMirror(annotatedElement, GenerateLuaDoc.class);
      throw new ProcessingException(msg, e, a);
    }
  }

  private static @Nullable String getSuperClassName(TypeElement annotatedElement,
      Map<String, String> luaTypeNames, ProcessingEnvironment env) throws ProcessingException {
    if (annotatedElement.getQualifiedName().contentEquals(Constants.OBJECT_CLASS)) {
      return null;
    }
    AnnotationMirror mirror = getAnnotationMirror(annotatedElement, LUA_CLASS_ATTRIBUTES);
    if (mirror != null) {
      DeclaredType superClassType = ProcessorUtils.getClassValue(mirror, "superClass", env);
      TypeElement superClassElement = (TypeElement) superClassType.asElement();
      return getName(superClassElement, env);
    }
    return null;
  }

  private final String name;
  private final @Nullable String superClass;
  private final String title;
  private final String subtitle;
  private final String type;
  private final String description;
  private final ImmutableSet<PropertyDocModel> properties;
  private final ImmutableSet<FunctionDocModel> functions;

  public LuaDocModel(String name, String title, String subtitle, String type,
      @Nullable String superClass, String description, Iterable<PropertyDocModel> properties,
      Iterable<FunctionDocModel> functions) {
    this.name = requireNonNull(name, "name == null!");
    this.title = requireNonNull(title, "title == null!");
    this.subtitle = requireNonNull(subtitle, "subtitle == null!");
    this.type = requireNonNull(type, "type == null!");
    this.superClass = superClass;
    this.description = requireNonNull(description, "description == null!");
    this.properties = ImmutableSet.copyOf(properties);
    this.functions = ImmutableSet.copyOf(functions);
  }

  public String getName() {
    return name;
  }

  public String getTitle() {
    if (title.isEmpty()) {
      return getName();
    }
    return title;
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
