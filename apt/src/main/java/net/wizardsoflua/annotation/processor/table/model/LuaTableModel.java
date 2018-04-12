package net.wizardsoflua.annotation.processor.table.model;

import static com.squareup.javapoet.WildcardTypeName.subtypeOf;
import static java.util.Objects.requireNonNull;
import static net.wizardsoflua.annotation.processor.Constants.TABLE_SUFFIX;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAnnotationMirror;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getClassValue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import com.google.common.base.Joiner;
import com.google.common.collect.Streams;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.GenerateLuaTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.annotation.processor.ProcessingException;
import net.wizardsoflua.annotation.processor.model.ManualFunctionModel;
import net.wizardsoflua.annotation.processor.model.PropertyModel;

public class LuaTableModel {
  public static LuaTableModel of(TypeElement annotatedElement, ProcessingEnvironment env)
      throws ProcessingException {
    boolean modifiable = isModifiable(annotatedElement);
    Map<String, PropertyModel> properties = new HashMap<>();
    Map<String, FunctionModel> functions = new HashMap<>();
    Map<String, ManualFunctionModel> manualFunctions = new HashMap<>();

    Iterator<? extends Element> it = relevantElements(annotatedElement, env).iterator();
    while (it.hasNext()) {
      Element element = it.next();
      ElementKind kind = element.getKind();
      if (kind == ElementKind.METHOD) {
        ExecutableElement method = (ExecutableElement) element;
        if (method.getAnnotation(LuaProperty.class) != null) {
          PropertyModel property = PropertyModel.of(method, env);
          String name = property.getName();
          PropertyModel existing = properties.get(name);
          if (existing != null) {
            existing.merge(property);
          } else {
            properties.put(name, property);
          }
        }
        if (method.getAnnotation(LuaFunction.class) != null) {
          FunctionModel function = FunctionModel.of(method);
          functions.put(function.getName(), function);
        }
      } else if (kind == ElementKind.CLASS) {
        TypeElement type = (TypeElement) element;
        if (type.getAnnotation(LuaFunction.class) != null) {
          ManualFunctionModel function = ManualFunctionModel.of(type);
          manualFunctions.put(function.getName(), function);
        }
      }
    }
    return new LuaTableModel(annotatedElement, modifiable, properties, functions, manualFunctions);
  }

  private static boolean isModifiable(TypeElement annotatedElement) {
    GenerateLuaTable generateLuaTable = annotatedElement.getAnnotation(GenerateLuaTable.class);
    if (generateLuaTable != null) {
      return generateLuaTable.modifiable();
    } else if (annotatedElement.getAnnotation(GenerateLuaClassTable.class) != null) {
      return true;
    } else if (annotatedElement.getAnnotation(GenerateLuaInstanceTable.class) != null) {
      return false;
    } else if (annotatedElement.getAnnotation(GenerateLuaModuleTable.class) != null) {
      return true;
    } else {
      throw new IllegalArgumentException(
          annotatedElement + " is not annotated with @" + GenerateLuaTable.class.getSimpleName()
              + ", @" + GenerateLuaClassTable.class.getSimpleName() + ", @"
              + GenerateLuaInstanceTable.class.getSimpleName() + " or @"
              + GenerateLuaModuleTable.class);
    }
  }

  public static Stream<? extends Element> relevantElements(TypeElement annotatedElement,
      ProcessingEnvironment env) {
    boolean includeFunctions;
    DeclaredType additionalFunctionsType;
    GenerateLuaTable generateLuaTable = annotatedElement.getAnnotation(GenerateLuaTable.class);
    if (generateLuaTable != null) {
      includeFunctions = generateLuaTable.includeFunctions();
      AnnotationMirror mirror = getAnnotationMirror(annotatedElement, GenerateLuaTable.class);
      additionalFunctionsType = getClassValue(mirror, GenerateLuaTable.ADDITIONAL_FUNCTIONS, env);
    } else if (annotatedElement.getAnnotation(GenerateLuaClassTable.class) != null) {
      includeFunctions = true;
      AnnotationMirror mirror = getAnnotationMirror(annotatedElement, GenerateLuaClassTable.class);
      additionalFunctionsType = getClassValue(mirror, GenerateLuaClassTable.INSTANCE, env);
    } else if (annotatedElement.getAnnotation(GenerateLuaInstanceTable.class) != null) {
      includeFunctions = false;
      additionalFunctionsType = null;
    } else if (annotatedElement.getAnnotation(GenerateLuaModuleTable.class) != null) {
      includeFunctions = true;
      additionalFunctionsType = null;
    } else {
      throw new IllegalArgumentException(
          annotatedElement + " is not annotated with @" + GenerateLuaTable.class.getSimpleName()
              + ", @" + GenerateLuaClassTable.class.getSimpleName() + ", @"
              + GenerateLuaInstanceTable.class.getSimpleName() + " or @"
              + GenerateLuaModuleTable.class);
    }
    TypeElement additionalFunctionsElement =
        additionalFunctionsType == null ? null : (TypeElement) additionalFunctionsType.asElement();
    return relevantElements(annotatedElement, includeFunctions, additionalFunctionsElement);
  }

  private static Stream<? extends Element> relevantElements(TypeElement annotatedElement,
      boolean includeFunctions, @Nullable TypeElement additionalFunctionsElement) {
    List<? extends Element> elements = annotatedElement.getEnclosedElements();
    Stream<? extends Element> stream = elements.stream();
    if (additionalFunctionsElement != null) {
      Stream<? extends Element> additionalFunctionsStream =
          additionalFunctionsElement.getEnclosedElements().stream()//
              .filter(e -> isLuaFunction(e));
      stream = Streams.concat(stream, additionalFunctionsStream);
    }
    if (!includeFunctions) {
      stream = stream.filter(e -> !isLuaFunction(e));
    }
    return stream;
  }

  private static boolean isLuaFunction(Element element) {
    return element.getAnnotation(LuaFunction.class) != null;
  }

  private final TypeElement sourceElement;
  private final boolean modifiable;
  private final SortedMap<String, PropertyModel> properties = new TreeMap<>();
  private final SortedMap<String, FunctionModel> functions = new TreeMap<>();
  private final SortedMap<String, ManualFunctionModel> manualFunctions = new TreeMap<>();

  public LuaTableModel(//
      TypeElement sourceElement, //
      boolean modifiable, //
      Map<? extends String, ? extends PropertyModel> properties, //
      Map<? extends String, ? extends FunctionModel> functions, //
      Map<? extends String, ? extends ManualFunctionModel> manualFunctions //
  ) {
    this.sourceElement = requireNonNull(sourceElement, "sourceElement == null!");
    this.modifiable = modifiable;
    this.properties.putAll(properties);
    this.functions.putAll(functions);
    this.manualFunctions.putAll(manualFunctions);
  }

  public TypeElement getSourceElement() {
    return sourceElement;
  }

  public ClassName getSourceClassName() {
    return ClassName.get(sourceElement);
  }

  public TypeName getParameterizedSourceClassName() {
    int typeParamCount = sourceElement.getTypeParameters().size();
    ClassName rawType = getSourceClassName();
    if (typeParamCount == 0) {
      return rawType;
    } else {
      TypeName[] typeArguments = new TypeName[typeParamCount];
      for (int i = 0; i < typeArguments.length; i++) {
        typeArguments[i] = subtypeOf(Object.class);
      }
      return ParameterizedTypeName.get(rawType, typeArguments);
    }
  }

  public String getGeneratedPackageName() {
    return getSourceClassName().packageName();
  }

  public String getGeneratedSimpleName() {
    List<String> sourceSimpleNames = getSourceClassName().simpleNames();
    return Joiner.on("").join(sourceSimpleNames) + TABLE_SUFFIX;
  }

  public boolean isModifiable() {
    return modifiable;
  }

  public Collection<PropertyModel> getProperties() {
    return Collections.unmodifiableCollection(properties.values());
  }

  public Collection<FunctionModel> getFunctions() {
    return Collections.unmodifiableCollection(functions.values());
  }

  public Collection<ManualFunctionModel> getManualFunctions() {
    return Collections.unmodifiableCollection(manualFunctions.values());
  }
}
