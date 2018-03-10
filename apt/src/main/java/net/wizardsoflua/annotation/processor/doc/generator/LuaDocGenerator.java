package net.wizardsoflua.annotation.processor.doc.generator;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import net.wizardsoflua.annotation.processor.ProcessingException;
import net.wizardsoflua.annotation.processor.doc.model.FunctionDocModel;
import net.wizardsoflua.annotation.processor.doc.model.LuaDocModel;
import net.wizardsoflua.annotation.processor.doc.model.PropertyDocModel;

public class LuaDocGenerator {
  private final LuaDocModel model;

  public LuaDocGenerator(LuaDocModel model) {
    this.model = requireNonNull(model, "model == null!");
  }

  public String generate() {
    StringBuilder doc = new StringBuilder();
    doc.append("---\n");
    doc.append("name: ").append(model.getName()).append('\n');
    doc.append("subtitle: ").append(model.getSubtitle()).append('\n');
    doc.append("type: ").append(model.getType()).append('\n');
    String superClass = model.getSuperClass();
    if (superClass != null) {
      doc.append("extends: ").append(superClass).append('\n');
    }
    doc.append("layout: module\n");
    doc.append("properties:\n");
    for (PropertyDocModel property : model.getProperties()) {
      doc.append("  - name: ").append(property.getName()).append('\n');
      doc.append("    type: '").append(property.getType()).append("'\n");
      doc.append("    access: ").append(property.getAccess()).append('\n');
      doc.append("    description: |\n").append(renderDescription(property.getDescription()))
          .append('\n');
    }
    doc.append("functions:\n");
    for (FunctionDocModel function : model.getFunctions()) {
      doc.append("  - name: ").append(function.getName()).append('\n');
      String args = Joiner.on(", ").join(function.getArgs());
      doc.append("    parameters: ").append(args).append('\n');
      doc.append("    results: '").append(function.getReturnType()).append("'\n");
      doc.append("    description: |\n").append(renderDescription(function.getDescription()))
          .append('\n');
    }
    doc.append("---\n");
    doc.append("\n");
    doc.append(model.getDescription()).append('\n');
    return doc.toString();
  }

  public static String getDescription(Element element, ProcessingEnvironment env) {
    Elements elements = env.getElementUtils();
    String docComment = elements.getDocComment(element);
    return Strings.nullToEmpty(docComment).trim();
  }

  private static String renderDescription(String description) {
    String indent = "       ";
    return indent + ' ' + description.replace("\n", "\n" + indent).replace("<code>", "```lua")
        .replace("</code>", "```");
  }

  public static String renderType(TypeMirror typeMirror, Element annotatedElement,
      Map<String, String> luaClassNames, ProcessingEnvironment env) throws ProcessingException {
    TypeKind typeKind = typeMirror.getKind();
    switch (typeKind) {
      case ARRAY:
        return "table";
      case BOOLEAN:
        return "boolean";
      case BYTE:
      case DOUBLE:
      case FLOAT:
      case INT:
      case LONG:
      case SHORT:
        return "number (" + typeKind.toString().toLowerCase() + ")";
      case CHAR:
        return "string";
      case DECLARED:
        Elements elements = env.getElementUtils();
        Types types = env.getTypeUtils();
        TypeMirror stringType = elements.getTypeElement(String.class.getName()).asType();
        TypeMirror enumType = types.erasure(elements.getTypeElement(Enum.class.getName()).asType());
        if (types.isSameType(typeMirror, stringType) || types.isSubtype(typeMirror, enumType)) {
          return "string";
        }
        TypeMirror iterableType =
            types.erasure(elements.getTypeElement(Iterable.class.getName()).asType());
        if (types.isSubtype(typeMirror, iterableType)) {
          return "table";
        }
        try {
          PrimitiveType primitiveType = types.unboxedType(typeMirror);
          return renderType(primitiveType, annotatedElement, luaClassNames, env);
        } catch (IllegalArgumentException ignore) {
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        TypeElement typeElement = (TypeElement) declaredType.asElement();
        String javaName = typeElement.getQualifiedName().toString();
        String luaName = luaClassNames.get(javaName);
        if (luaName == null) {
          CharSequence msg = "Could not determine the lua name of " + javaName;
          throw new ProcessingException(msg, annotatedElement);
        }
        return toReference(luaName);
      case VOID:
        return "nil";
      case EXECUTABLE:
      case ERROR:
      case INTERSECTION:
      case NONE:
      case NULL:
      case OTHER:
      case PACKAGE:
      case TYPEVAR:
      case UNION:
      case WILDCARD:
      default:
        throw new IllegalArgumentException("Unknown type: " + typeMirror);
    }
  }

  public static String renderType(String type) {
    switch (type) {
      case "boolean":
      case "function":
      case "number":
      case "string":
      case "table":
        return type;
    }
    if (type.startsWith("number (")) {
      return type;
    }
    return toReference(type);
  }

  private static String toReference(CharSequence simpleName) {
    return simpleName.length() == 0 ? "" : "[" + simpleName + "](/modules/" + simpleName + ")";
  }
}
