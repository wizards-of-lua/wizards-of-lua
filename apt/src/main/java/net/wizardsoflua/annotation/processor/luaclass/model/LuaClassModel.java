package net.wizardsoflua.annotation.processor.luaclass.model;

import static java.util.Objects.requireNonNull;
import static javax.lang.model.type.TypeKind.DECLARED;
import static net.wizardsoflua.annotation.processor.Constants.CLASS_SUFFIX;
import static net.wizardsoflua.annotation.processor.Constants.LUA_API_BASE;
import static net.wizardsoflua.annotation.processor.Constants.LUA_API_PROXY_CLASS_NAME;
import static net.wizardsoflua.annotation.processor.Constants.OBJECT_CLASS_CLASS_NAME;
import static net.wizardsoflua.annotation.processor.Constants.PROXY_SUFFIX;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getTypeParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;

import com.google.common.collect.Maps;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.HasLuaClass;
import net.wizardsoflua.annotation.processor.MultipleProcessingExceptions;
import net.wizardsoflua.annotation.processor.ProcessingException;
import net.wizardsoflua.annotation.processor.ProcessorUtils;
import net.wizardsoflua.annotation.processor.model.FunctionModel;
import net.wizardsoflua.annotation.processor.model.ManualFunctionModel;
import net.wizardsoflua.annotation.processor.model.PropertyModel;

public class LuaClassModel {
  public static LuaClassModel of(TypeElement annotatedElement, ProcessingEnvironment env)
      throws ProcessingException, MultipleProcessingExceptions {
    if (annotatedElement.getAnnotation(HasLuaClass.class) != null) {
      CharSequence msg = "Either use @" + HasLuaClass.class.getSimpleName() + " or @"
          + GenerateLuaClass.class.getSimpleName() + ", but not both";
      AnnotationMirror a1 = ProcessorUtils.getAnnotationMirror(annotatedElement, HasLuaClass.class);
      AnnotationMirror a2 =
          ProcessorUtils.getAnnotationMirror(annotatedElement, GenerateLuaClass.class);
      throw new MultipleProcessingExceptions(Arrays.asList(//
          new ProcessingException(msg, annotatedElement, a1), //
          new ProcessingException(msg, annotatedElement, a2)//
      ));
    }

    TypeName delegateTypeName = getDelegateTypeName(annotatedElement, env);

    String moduleName = annotatedElement.getAnnotation(GenerateLuaClass.class).name();

    Entry<ClassName, ClassName> superClassAndProxy = getSuperClassAndProxy(annotatedElement, env);
    ClassName superClassName = superClassAndProxy.getKey();
    ClassName superProxyClassName = superClassAndProxy.getValue();

    return new LuaClassModel(annotatedElement, delegateTypeName, moduleName, superClassName,
        superProxyClassName);
  }

  private static TypeName getDelegateTypeName(TypeElement moduleElement,
      ProcessingEnvironment env) {
    DeclaredType subType = (DeclaredType) moduleElement.asType();
    String superType = LUA_API_BASE;
    int typeParameterIndex = 0;
    TypeMirror delegateType = getTypeParameter(subType, superType, typeParameterIndex, env);
    if (delegateType.getKind() == TypeKind.TYPEVAR) {
      delegateType = ((TypeVariable) delegateType).getUpperBound();
    }
    return TypeName.get(delegateType);
  }

  public static Entry<ClassName, ClassName> getSuperClassAndProxy(TypeElement annotatedElement,
      ProcessingEnvironment env) throws ProcessingException {
    TypeElement superApi = getSuperApi(annotatedElement);
    return getLuaClassAndProxyName(superApi, env);
  }

  private static @Nullable TypeElement getSuperApi(TypeElement element) {
    while (true) {
      TypeMirror superType = element.getSuperclass();
      if (superType.getKind() != DECLARED) {
        return null;
      }
      element = (TypeElement) ((DeclaredType) superType).asElement();
      if (element.getAnnotation(GenerateLuaClass.class) != null
          || element.getAnnotation(HasLuaClass.class) != null) {
        return element;
      }
    }
  }

  private static Entry<ClassName, ClassName> getLuaClassAndProxyName(@Nullable TypeElement api,
      ProcessingEnvironment env) throws ProcessingException {
    if (api == null) {
      ClassName luaClassName = OBJECT_CLASS_CLASS_NAME;
      ClassName luaProxyName = LUA_API_PROXY_CLASS_NAME;
      return Maps.immutableEntry(luaClassName, luaProxyName);
    }
    HasLuaClass hasLuaClass = api.getAnnotation(HasLuaClass.class);
    GenerateLuaClass generateLuaClass = api.getAnnotation(GenerateLuaClass.class);
    if (hasLuaClass != null && generateLuaClass != null) {
      CharSequence msg = "@" + HasLuaClass.class.getSimpleName() + " and @"
          + GenerateLuaClass.class.getSimpleName() + " are exclusive";
      throw new ProcessingException(msg, api);
    }
    if (hasLuaClass != null) {
      AnnotationMirror mirror = ProcessorUtils.getAnnotationMirror(api, HasLuaClass.class);

      DeclaredType luaClassType = ProcessorUtils.getClassValue(mirror, HasLuaClass.LUA_CLASS, env);
      TypeElement luaClassElement = (TypeElement) luaClassType.asElement();
      ClassName luaClassName = ClassName.get(luaClassElement);

      DeclaredType luaProxyType = ProcessorUtils.getClassValue(mirror, HasLuaClass.LUA_PROXY, env);
      TypeElement luaProxyElement = (TypeElement) luaProxyType.asElement();
      ClassName luaProxyName = ClassName.get(luaProxyElement);

      return Maps.immutableEntry(luaClassName, luaProxyName);
    }
    if (generateLuaClass != null) {
      Elements elements = env.getElementUtils();
      PackageElement pkg = elements.getPackageOf(api);
      String packageName = pkg.getQualifiedName().toString();
      String apiName = generateLuaClass.name();
      ClassName luaClassName = ClassName.get(packageName, apiName + CLASS_SUFFIX);
      ClassName luaProxyName = ClassName.get(packageName, apiName + PROXY_SUFFIX);
      return Maps.immutableEntry(luaClassName, luaProxyName);
    }
    throw new IllegalStateException("Api must be annotated with @"
        + GenerateLuaClass.class.getSimpleName() + " or @" + HasLuaClass.class.getSimpleName());
  }

  private final TypeElement annotatedElement;
  private final ClassName apiClassName;
  private final TypeName delegateTypeName;
  private final String name;
  private final ClassName superClassName;
  private final ClassName superProxyClassName;
  private final SortedMap<String, PropertyModel> properties = new TreeMap<>();
  private final SortedMap<String, FunctionModel> functions = new TreeMap<>();
  private final SortedMap<String, ManualFunctionModel> manualFunctions = new TreeMap<>();

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
    String simpleName = name + CLASS_SUFFIX;
    return ClassName.get(packageName, simpleName);
  }

  public ClassName getProxyClassName() {
    String packageName = getPackageName();
    String simpleName = name + PROXY_SUFFIX;
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

  public void addManualFunction(ManualFunctionModel function) {
    manualFunctions.put(function.getName(), function);
  }

  public Collection<ManualFunctionModel> getManualFunctions() {
    return Collections.unmodifiableCollection(manualFunctions.values());
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
