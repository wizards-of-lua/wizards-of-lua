package net.wizardsoflua.annotation.processor;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.lang.model.type.DeclaredType;

public class Module {
  private final String name;
  private final DeclaredType superClass;
  private final SortedMap<String, Property> properties = new TreeMap<>();

  public Module(String name, DeclaredType superClass) {
    this.name = checkNotNull(name, "name == null!");
    this.superClass = checkNotNull(superClass, "superClass == null!");
  }

  public String getName() {
    return name;
  }

  public DeclaredType getSuperClass() {
    return superClass;
  }

  public void addProperty(Property property) {
    Property existing = properties.get(property.getName());
    if (existing != null) {
      existing.merge(property);
    } else {
      properties.put(property.getName(), property);
    }
  }

  public Collection<Property> getProperties() {
    return Collections.unmodifiableCollection(properties.values());
  }
}
