package net.wizardsoflua.annotation.processor.doc.model;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAnnotationMirror;
import static net.wizardsoflua.annotation.processor.doc.model.PropertyAccess.READWRITE;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.annotation.processor.LuaPropertyUtils;
import net.wizardsoflua.annotation.processor.MultipleProcessingExceptions;
import net.wizardsoflua.annotation.processor.ProcessingException;
import net.wizardsoflua.annotation.processor.doc.generator.LuaDocGenerator;

public class PropertyDocModel {
  public static PropertyDocModel of(ExecutableElement method, ProcessingEnvironment env)
      throws ProcessingException {
    String name = LuaPropertyUtils.getPropertyName(method);
    String type = LuaPropertyUtils.getPropertyType(method, env);
    PropertyAccess access = LuaPropertyUtils.getPropertyAccess(method);
    String description = LuaDocGenerator.getDescription(method, env);
    List<Element> elements = Arrays.asList(method);
    return new PropertyDocModel(name, type, access, description, elements);
  }

  private final String name;
  private final String type;
  private final PropertyAccess access;
  private final String description;
  private final Set<Element> elements;

  public PropertyDocModel(String name, String type, PropertyAccess access, String description,
      Iterable<? extends Element> elements) {
    this.name = requireNonNull(name, "name == null!");
    this.type = requireNonNull(type, "type == null!");
    this.access = requireNonNull(access, "access == null!");
    this.description = requireNonNull(description, "description == null!");
    this.elements = ImmutableSet.copyOf(elements);
  }

  /**
   * @return the value of {@link #name}
   */
  public String getName() {
    return name;
  }

  /**
   * @return the value of {@link #type}
   */
  public String getType() {
    return type;
  }

  /**
   * @return the value of {@link #access}
   */
  public PropertyAccess getAccess() {
    return access;
  }

  /**
   * @return the value of {@link #description}
   */
  public String getDescription() {
    return description;
  }

  public PropertyDocModel merge(PropertyDocModel other) throws MultipleProcessingExceptions {
    checkArgument(name.equals(other.name), "Cannot merge properties with different names");
    if (access == other.access || access == READWRITE || other.access == READWRITE) {
      throw newMultipleProcessingExceptions("Duplicate getter/setter " + name);
    }
    if (!type.equals(other.type)) {
      throw newMultipleProcessingExceptions("Getter type does not equal setter type");
    }
    if (!description.isEmpty() && !other.description.isEmpty()
        && !description.equals(other.description)) {
      throw newMultipleProcessingExceptions(
          "The description on the getter differs from the description on the setter");
    }
    Set<Element> elements = Sets.union(this.elements, other.elements);
    return new PropertyDocModel(name, type, access, description, elements);
  }

  private MultipleProcessingExceptions newMultipleProcessingExceptions(CharSequence msg) {
    return new MultipleProcessingExceptions(Iterables.transform(elements, e -> {
      AnnotationMirror a = getAnnotationMirror(e, LuaProperty.class);
      return new ProcessingException(msg, e, a);
    }));
  }
}
