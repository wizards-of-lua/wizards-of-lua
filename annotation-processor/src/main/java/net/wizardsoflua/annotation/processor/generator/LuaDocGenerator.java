package net.wizardsoflua.annotation.processor.generator;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import net.wizardsoflua.annotation.processor.model.ArgumentModel;
import net.wizardsoflua.annotation.processor.model.FunctionModel;
import net.wizardsoflua.annotation.processor.model.ModuleModel;
import net.wizardsoflua.annotation.processor.model.PropertyModel;

public class LuaDocGenerator {
  private final ModuleModel module;
  private final ProcessingEnvironment env;

  public LuaDocGenerator(ModuleModel module, ProcessingEnvironment env) {
    this.module = checkNotNull(module, "module == null!");
    this.env = checkNotNull(env, "env == null!");
  }

  public String generate() {
    StringBuilder doc = new StringBuilder();
    doc.append("---\n");
    doc.append("name: ").append(module.getName()).append('\n');
    doc.append("subtitle: ").append("TODO").append('\n'); // TODO: subtitle
    doc.append("type: class\n");
    doc.append("layout: module\n");
    doc.append("properties:\n");
    for (PropertyModel property : module.getProperties()) {
      doc.append("  - name: ").append(property.getName()).append('\n');
      doc.append("    type: ").append(property.getType()).append('\n');
      doc.append("    access: ").append(property.renderAccess()).append('\n');
      doc.append("    description: ").append(renderDescription(property.getDescription()))
          .append('\n');
    }
    doc.append("functions:\n");
    for (FunctionModel function : module.getFunctions()) {
      doc.append("  - name: ").append(function.getName()).append('\n');
      String args =
          Joiner.on(", ").join(Iterables.transform(function.getArgs(), ArgumentModel::getName));
      doc.append("    parameters: ").append(args).append('\n');
      doc.append("    results: ").append(renderType(function.getReturnType())).append('\n');
      doc.append("    description: ").append(renderDescription(function.getDescription()))
          .append('\n');
    }
    doc.append("---\n");
    doc.append("\n");
    doc.append(module.getDescription()).append('\n');
    return doc.toString();
  }

  private String renderDescription(String description) {
    if (description.contains("\n")) {
      return '"' + description + '"';
    } else {
      return description;
    }
  }

  public String renderType(TypeMirror typeMirror) {
    return renderType(typeMirror, env);
  }

  public static String renderType(TypeMirror typeMirror, ProcessingEnvironment env) {
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
        TypeMirror enumType = elements.getTypeElement(Enum.class.getName()).asType();
        if (types.isSameType(typeMirror, stringType) || types.isSubtype(typeMirror, enumType)) {
          return "string";
        }
        try {
          PrimitiveType primitiveType = types.unboxedType(typeMirror);
          return renderType(primitiveType, env);
        } catch (IllegalArgumentException ignore) {
        }
        return toReference(((DeclaredType) typeMirror).asElement().getSimpleName());
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

  public static String toReference(CharSequence simpleName) {
    return simpleName.length() == 0 ? ""
        : "[" + simpleName + "](!SITE_URL!/modules/" + simpleName + "/)";
  }
}
