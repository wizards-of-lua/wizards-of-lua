package net.wizardsoflua.annotation.processor.model;

import static com.google.common.base.Preconditions.checkNotNull;
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
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.google.common.base.Strings;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import net.wizardsoflua.annotation.LuaModule;
import net.wizardsoflua.annotation.processor.ProcessorUtils;

public class ModuleModel {
  public static ModuleModel of(TypeElement moduleElement, ProcessingEnvironment env) {
    ClassName apiClassName = ClassName.get(moduleElement);

    TypeName delegateTypeName = getDelegateTypeName(moduleElement, env);

    String moduleName = moduleElement.getAnnotation(LuaModule.class).name();

    AnnotationMirror mirror = ProcessorUtils.getAnnoationMirror(moduleElement, LuaModule.class);
    DeclaredType superType = ProcessorUtils.getClassValue(mirror, "superClass", env);
    TypeName superTypeName = TypeName.get(superType);

    ClassName superProxyClassName = getSuperProxyClassName(superType, env);

    Elements elements = env.getElementUtils();
    String docComment = elements.getDocComment(moduleElement);
    String description = Strings.nullToEmpty(docComment).trim();
    return new ModuleModel(apiClassName, delegateTypeName, moduleName, superTypeName,
        superProxyClassName, description);
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

  private final ClassName apiClassName;
  private final TypeName delegateTypeName;
  private final String name;
  private final TypeName superTypeName;
  private final ClassName superProxyClassName;
  private final SortedMap<String, PropertyModel> properties = new TreeMap<>();
  private final SortedMap<String, FunctionModel> functions = new TreeMap<>();
  private String description;

  private final Collection<ExecutableElement> onCreateLuaProxy = new ArrayList<>();
  private final Collection<ExecutableElement> onLoadLuaClass = new ArrayList<>();

  public ModuleModel(ClassName apiClassName, TypeName delegateTypeName, String name,
      TypeName superTypeName, ClassName superProxyClassName, String description) {
    this.apiClassName = checkNotNull(apiClassName, "apiClassName == null!");
    this.delegateTypeName = checkNotNull(delegateTypeName, "delegateTypeName == null!");
    this.name = checkNotNull(name, "name == null!");
    this.superTypeName = checkNotNull(superTypeName, "superTypeName == null!");
    this.superProxyClassName = checkNotNull(superProxyClassName, "superProxyClassName == null!");
    this.description = requireNonNull(description, "description == null!");
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

  public TypeName getSuperTypeName() {
    return superTypeName;
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

  public String getDescription() {
    return description;
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
