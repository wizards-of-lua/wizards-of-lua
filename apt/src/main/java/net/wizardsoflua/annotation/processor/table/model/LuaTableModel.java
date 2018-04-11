package net.wizardsoflua.annotation.processor.table.model;

import static com.squareup.javapoet.WildcardTypeName.subtypeOf;
import static java.util.Objects.requireNonNull;
import static javax.lang.model.util.ElementFilter.methodsIn;
import static javax.lang.model.util.ElementFilter.typesIn;
import static net.wizardsoflua.annotation.processor.Constants.TABLE_SUFFIX;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.checkAnnotated;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAnnotationMirror;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getClassValue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import net.wizardsoflua.annotation.GenerateLuaTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.annotation.processor.ProcessingException;
import net.wizardsoflua.annotation.processor.model.FunctionModel;
import net.wizardsoflua.annotation.processor.model.ManualFunctionModel;
import net.wizardsoflua.annotation.processor.model.PropertyModel;

public class LuaTableModel {
  public static LuaTableModel of(TypeElement annotatedElement, ProcessingEnvironment env)
      throws ProcessingException {
    GenerateLuaTable annotation = checkAnnotated(annotatedElement, GenerateLuaTable.class);

    boolean modifiable = annotation.modifiable();

    AnnotationMirror mirror = getAnnotationMirror(annotatedElement, GenerateLuaTable.class);
    DeclaredType additionalType = getClassValue(mirror, GenerateLuaTable.ADDITIONAL_FUNCTIONS, env);

    boolean includeFunctions = annotation.includeFunctions();

    Map<String, PropertyModel> properties = new HashMap<>();
    Map<String, FunctionModel> functions = new HashMap<>();
    Map<String, FunctionModel> additionalFunctions = new HashMap<>();
    Map<String, ManualFunctionModel> manualFunctions = new HashMap<>();

    List<? extends Element> elements = annotatedElement.getEnclosedElements();
    List<ExecutableElement> methods = methodsIn(elements);
    for (ExecutableElement method : methods) {
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
    }
    if (includeFunctions) {
      for (ExecutableElement method : methods) {
        if (method.getAnnotation(LuaFunction.class) != null) {
          FunctionModel function = FunctionModel.of(method);
          functions.put(function.getName(), function);
        }
      }
      TypeElement additionalElement = (TypeElement) additionalType.asElement();
      List<? extends Element> additionalElements = additionalElement.getEnclosedElements();
      List<ExecutableElement> additionalMethods = methodsIn(additionalElements);
      for (ExecutableElement method : additionalMethods) {
        if (method.getAnnotation(LuaFunction.class) != null) {
          FunctionModel function = FunctionModel.of(method);
          additionalFunctions.put(function.getName(), function);
        }
      }
      List<TypeElement> types = typesIn(Iterables.concat(elements, additionalElements));
      for (TypeElement typeElement : types) {
        if (typeElement.getAnnotation(LuaFunction.class) != null) {
          ManualFunctionModel function = ManualFunctionModel.of(typeElement);
          manualFunctions.put(function.getName(), function);
        }
      }
    }

    return new LuaTableModel(annotatedElement, modifiable, additionalType, properties, functions,
        manualFunctions, additionalFunctions);
  }

  private final TypeElement sourceElement;
  private final boolean modifiable;
  private final TypeMirror additionalFunctionsType;
  private final SortedMap<String, PropertyModel> properties = new TreeMap<>();
  private final SortedMap<String, FunctionModel> functions = new TreeMap<>();
  private final SortedMap<String, ManualFunctionModel> manualFunctions = new TreeMap<>();
  private final SortedMap<String, FunctionModel> additionalFunctions = new TreeMap<>();

  public LuaTableModel(//
      TypeElement sourceElement, //
      boolean modifiable, //
      TypeMirror additionalFunctionsType, //
      Map<? extends String, ? extends PropertyModel> properties, //
      Map<? extends String, ? extends FunctionModel> functions, //
      Map<? extends String, ? extends ManualFunctionModel> manualFunctions, //
      Map<? extends String, ? extends FunctionModel> additionalFunctions //
  ) {
    this.sourceElement = requireNonNull(sourceElement, "sourceElement == null!");
    this.modifiable = modifiable;
    this.additionalFunctionsType =
        requireNonNull(additionalFunctionsType, "additionalFunctionsType == null!");
    this.properties.putAll(properties);
    this.functions.putAll(functions);
    this.manualFunctions.putAll(manualFunctions);
    this.additionalFunctions.putAll(additionalFunctions);
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

  public Collection<FunctionModel> getAdditionalFunctions() {
    return Collections.unmodifiableCollection(additionalFunctions.values());
  }

  public TypeMirror getAdditionalFunctionsType() {
    return additionalFunctionsType;
  }
}
