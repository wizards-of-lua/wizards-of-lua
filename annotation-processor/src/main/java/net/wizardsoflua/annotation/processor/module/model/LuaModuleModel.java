package net.wizardsoflua.annotation.processor.module.model;

import static java.util.Objects.requireNonNull;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.checkAnnotated;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import com.squareup.javapoet.ClassName;

import net.wizardsoflua.annotation.GenerateLuaModule;
import net.wizardsoflua.annotation.processor.model.FunctionModel;
import net.wizardsoflua.annotation.processor.model.PropertyModel;

public class LuaModuleModel {
  public static LuaModuleModel of(TypeElement moduleElement, ProcessingEnvironment env) {
    ClassName moduleClassName = ClassName.get(moduleElement);

    GenerateLuaModule annotation = checkAnnotated(moduleElement, GenerateLuaModule.class);
    String name = annotation.name();

    return new LuaModuleModel(moduleClassName, name);
  }

  private final ClassName moduleClassName;
  private final String name;
  private final SortedMap<String, PropertyModel> properties = new TreeMap<>();
  private final SortedMap<String, FunctionModel> functions = new TreeMap<>();

  public LuaModuleModel(ClassName moduleClassName, String name) {
    this.moduleClassName = requireNonNull(moduleClassName, "moduleClassName == null!");
    this.name = requireNonNull(name, "name == null!");
  }

  public String getPackageName() {
    return moduleClassName.packageName();
  }

  public ClassName getClassName() {
    return moduleClassName;
  }

  public ClassName getModuleClassName() {
    String packageName = getPackageName();
    String simpleName = name + "Module";
    return ClassName.get(packageName, simpleName);
  }

  public String getName() {
    return name;
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
