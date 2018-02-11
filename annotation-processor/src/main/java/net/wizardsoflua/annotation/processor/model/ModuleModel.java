package net.wizardsoflua.annotation.processor.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getTypeParameter;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import net.wizardsoflua.annotation.LuaModule;
import net.wizardsoflua.annotation.processor.ProcessorUtils;

public class ModuleModel {
  public static ModuleModel of(TypeElement moduleElement, ProcessingEnvironment env) {
    ClassName className = ClassName.get(moduleElement);

    DeclaredType subType = (DeclaredType) moduleElement.asType();
    String superType = "net.wizardsoflua.scribble.LuaApiBase";
    int typeParameterIndex = 0;
    TypeMirror delegateType =
        getTypeParameter(subType, superType, typeParameterIndex, env.getTypeUtils());
    checkNotNull(delegateType, "delegateType == null! " + className);
    TypeName delegateTypeName = TypeName.get(delegateType);

    String moduleName = moduleElement.getAnnotation(LuaModule.class).name();

    AnnotationMirror mirror = ProcessorUtils.getAnnoationMirror(moduleElement, LuaModule.class);
    DeclaredType superClass = ProcessorUtils.getClassValue(mirror, "superClass");

    return new ModuleModel(className, delegateTypeName, moduleName, superClass);
  }

  private final ClassName apiClassName;
  private final TypeName delegateTypeName;
  private final String name;
  private final DeclaredType superClass;
  private final SortedMap<String, PropertyModel> properties = new TreeMap<>();
  private final SortedMap<String, FunctionModel> functions = new TreeMap<>();

  public ModuleModel(ClassName apiClassName, TypeName delegateTypeName, String name,
      DeclaredType superClass) {
    this.apiClassName = checkNotNull(apiClassName, "apiClassName == null!");
    this.delegateTypeName = checkNotNull(delegateTypeName, "delegateTypeName == null!");
    this.name = checkNotNull(name, "name == null!");
    this.superClass = checkNotNull(superClass, "superClass == null!");
  }

  public String getPackageName() {
    return apiClassName.packageName();
  }

  public ClassName getApiClassName() {
    return apiClassName;
  }

  public TypeName getDelegateTypeName() {
    return delegateTypeName;
  }

  public ClassName getClassClassName() {
    String packageName = apiClassName.packageName();
    String simpleName = name + "Class";
    return ClassName.get(packageName, simpleName);
  }

  public ClassName getProxyClassName() {
    String packageName = apiClassName.packageName();
    String simpleName = name + "Proxy";
    return ClassName.get(packageName, simpleName);
  }

  public String getName() {
    return name;
  }

  public DeclaredType getSuperClass() {
    return superClass;
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
}
