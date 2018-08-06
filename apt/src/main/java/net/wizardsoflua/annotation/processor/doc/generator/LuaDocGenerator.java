package net.wizardsoflua.annotation.processor.doc.generator;

import static java.util.Objects.requireNonNull;
import static net.wizardsoflua.annotation.processor.ProcessorUtils.getAllSuperTypes;

import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import net.sandius.rembulan.Table;
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
      Map<String, String> luaClassNames, ProcessingEnvironment env)
      throws ProcessingException, IllegalArgumentException {
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
        DeclaredType declaredType = (DeclaredType) typeMirror;
        Types types = env.getTypeUtils();
        try {
          PrimitiveType primitiveType = types.unboxedType(typeMirror);
          return renderType(primitiveType, annotatedElement, luaClassNames, env);
        } catch (IllegalArgumentException ignore) {
        }
        TypeElement typeElement = (TypeElement) declaredType.asElement();
        if (typeElement.getQualifiedName().contentEquals(String.class.getName())) {
          return "string";
        }
        // Using getAllSuperTypes, because Types.isSubType() does not work across processing rounds
        for (TypeMirror superType : getAllSuperTypes(typeMirror, env)) {
          if (superType.getKind() == TypeKind.DECLARED) {
            TypeElement superElement = (TypeElement) ((DeclaredType) superType).asElement();

            Name qualifiedSuperName = superElement.getQualifiedName();
            String luaName = luaClassNames.get(qualifiedSuperName.toString());
            if (luaName != null) {
              return toReference(luaName);
            }
            if (qualifiedSuperName.contentEquals(Table.class.getName())) {
              return "table";
            }
            if (qualifiedSuperName.contentEquals(Enum.class.getName())) {
              return "string";
            }
            if (qualifiedSuperName.contentEquals(Iterable.class.getName())) {
              return "table";
            }
          }
        }
        Name javaName = typeElement.getQualifiedName();
        CharSequence msg = "Could not determine the lua name of " + javaName + " at "
            + annotatedElement + " of " + annotatedElement.getEnclosingElement();
        throw new ProcessingException(msg, annotatedElement);
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
