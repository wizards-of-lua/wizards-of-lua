package net.wizardsoflua.annotation.processor.luaclass.model;

import static java.util.Objects.requireNonNull;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getTypeParameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Types;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.processor.ProcessorUtils;
import net.wizardsoflua.annotation.processor.model.FunctionModel;
import net.wizardsoflua.annotation.processor.model.PropertyModel;

public class LuaClassModel {
  public static LuaClassModel of(TypeElement annotatedElement, ProcessingEnvironment env) {
    TypeName delegateTypeName = getDelegateTypeName(annotatedElement, env);

    String moduleName = annotatedElement.getAnnotation(GenerateLuaClass.class).name();

    AnnotationMirror mirror =
        ProcessorUtils.getAnnotationMirror(annotatedElement, GenerateLuaClass.class);
    DeclaredType superType = ProcessorUtils.getClassValue(mirror, "superClass", env);
    ClassName superClassName = ClassName.get((TypeElement) superType.asElement());

    ClassName superProxyClassName = getSuperProxyClassName(superType, env);

    return new LuaClassModel(annotatedElement, delegateTypeName, moduleName, superClassName,
        superProxyClassName);
  }

  private static TypeName getDelegateTypeName(TypeElement moduleElement,
      ProcessingEnvironment env) {
    DeclaredType subType = (DeclaredType) moduleElement.asType();
    String superType = "net.wizardsoflua.scribble.LuaApiBase";
    int typeParameterIndex = 0;
    TypeMirror delegateType = getTypeParameter(subType, superType, typeParameterIndex, env);
    if (delegateType.getKind() == TypeKind.TYPEVAR) {
      delegateType = ((TypeVariable) delegateType).getUpperBound();
    }
    return TypeName.get(delegateType);
  }

  private static ClassName getSuperProxyClassName(DeclaredType superType,
      ProcessingEnvironment env) {
    String superSuperType = "net.wizardsoflua.lua.classes.ProxyingLuaClass";
    int typeParameterIndex = 1;
    TypeMirror superProxyType =
        getTypeParameter(superType, superSuperType, typeParameterIndex, env);
    if (superProxyType == null) {
      return ClassName.get("net.wizardsoflua.scribble", "LuaApiProxy");
    }
    if (superProxyType.getKind() == TypeKind.TYPEVAR) {
      superProxyType = ((TypeVariable) superProxyType).getUpperBound();
    }
    Types types = env.getTypeUtils();
    TypeElement superProxyElement = (TypeElement) types.asElement(superProxyType);
    return ClassName.get(superProxyElement);
  }

  private final TypeElement annotatedElement;
  private final ClassName apiClassName;
  private final TypeName delegateTypeName;
  private final String name;
  private final ClassName superClassName;
  private final ClassName superProxyClassName;
  private final SortedMap<String, PropertyModel> properties = new TreeMap<>();
  private final SortedMap<String, FunctionModel> functions = new TreeMap<>();

  private final Collection<ExecutableElement> onCreateLuaProxy = new ArrayList<>();
  private final Collection<ExecutableElement> onLoadLuaClass = new ArrayList<>();

  public LuaClassModel(TypeElement annotatedElement, TypeName delegateTypeName, String name,
      ClassName superClassName, ClassName superProxyClassName) {
    this.annotatedElement = requireNonNull(annotatedElement, "annotatedElement == null!");
    apiClassName = ClassName.get(annotatedElement);
    this.delegateTypeName = requireNonNull(delegateTypeName, "delegateTypeName == null!");
    this.name = requireNonNull(name, "name == null!");
    this.superClassName = requireNonNull(superClassName, "superClassName == null!");
    this.superProxyClassName = requireNonNull(superProxyClassName, "superProxyClassName == null!");
  }

  public TypeElement getAnnotatedElement() {
    return annotatedElement;
  }

  public String getPackageName() {
    return apiClassName.packageName();
  }

  public ClassName getApiClassName() {
    return apiClassName;
  }

  public ParameterizedTypeName getParameterizedApiTypeName() {
    ClassName raw = getApiClassName();
    TypeName delegate = getDelegateTypeName();
    return ParameterizedTypeName.get(raw, delegate);
  }

  public TypeName getDelegateTypeName() {
    return delegateTypeName;
  }

  public ClassName getClassClassName() {
    String packageName = getPackageName();
    String simpleName = name + "Class";
    return ClassName.get(packageName, simpleName);
  }

  public ClassName getProxyClassName() {
    String packageName = getPackageName();
    String simpleName = name + "Proxy";
    return ClassName.get(packageName, simpleName);
  }

  public ParameterizedTypeName getParameterizedProxyTypeName() {
    ClassName raw = getProxyClassName();
    TypeName delegate = getDelegateTypeName();
    TypeName api = getParameterizedApiTypeName();
    return ParameterizedTypeName.get(raw, api, delegate);
  }

  public String getName() {
    return name;
  }

  public ClassName getSuperClassName() {
    return superClassName;
  }

  public ClassName getSuperProxyClassName() {
    return superProxyClassName;
  }

  public void addProperty(PropertyModel property) {
    PropertyModel existing = properties.get(property.getName());
    if (existing != null) {
      existing.merge(property);
    } else {
      properties.put(property.getName(), property);
    }
  }

  public Collection<PropertyModel> getProperties() {
    return Collections.unmodifiableCollection(properties.values());
  }

  public void addFunction(FunctionModel function) {
    functions.put(function.getName(), function);
  }

  public Collection<FunctionModel> getFunctions() {
    return Collections.unmodifiableCollection(functions.values());
  }

  public void addOnCreateLuaProxy(ExecutableElement method) {
    onCreateLuaProxy.add(method);
  }

  public Collection<ExecutableElement> getOnCreateLuaProxy() {
    return Collections.unmodifiableCollection(onCreateLuaProxy);
  }

  public void addOnLoadLuaClass(ExecutableElement method) {
    onLoadLuaClass.add(method);
  }

  public Collection<ExecutableElement> getOnLoadLuaClass() {
    return Collections.unmodifiableCollection(onLoadLuaClass);
  }
}
