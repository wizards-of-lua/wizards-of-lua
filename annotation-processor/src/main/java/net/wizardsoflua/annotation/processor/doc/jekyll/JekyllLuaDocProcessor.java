package net.wizardsoflua.annotation.processor.doc.jekyll;

import static javax.lang.model.type.TypeKind.VOID;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileManager.Location;
import javax.tools.StandardLocation;

import com.google.common.base.Strings;

import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.annotation.processor.Property;

public class JekyllLuaDocProcessor extends AbstractProcessor {
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    HashSet<String> result = new HashSet<>();
    result.add(LuaProperty.class.getName());
    return result;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.RELEASE_8;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Elements utils = processingEnv.getElementUtils();
    for (TypeElement annotation : annotations) {
      Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

      Map<String, Property> properties = new TreeMap<>();

      for (Element method : annotatedElements) {
        String methodName = method.getSimpleName().toString();
        ExecutableType methodType = (ExecutableType) method.asType();
        TypeMirror returnType = methodType.getReturnType();
        List<? extends TypeMirror> parameterTypes = methodType.getParameterTypes();
        TypeKind returnTypeKind = returnType.getKind();
        String description = Strings.nullToEmpty(utils.getDocComment(method)).trim();
        if (returnTypeKind != VOID && parameterTypes.isEmpty()) { // Getter
          String propertyName = extractPropertyNameFromGetter(methodName);
          Property property = properties.computeIfAbsent(propertyName,
              name -> new Property(name, typeToString(returnType), description));
          property.setReadable(true);
        } else if (returnTypeKind == VOID && parameterTypes.size() == 1) { // Setter
          String propertyName = extractPropertyNameFromSetter(methodName);
          Property property = properties.computeIfAbsent(propertyName, name -> {
            return new Property(name, typeToString(parameterTypes.get(0)), description);
          });
          property.setWriteable(true);
        } else {
          // TODO error?
        }
      }



      Filer filer = processingEnv.getFiler();
      Location location = StandardLocation.SOURCE_OUTPUT;
      CharSequence pkg = "net.wizardsoflua.test";
      CharSequence relativeName = "bla.txt";
      try (Writer writer =
          new BufferedWriter(filer.createResource(location, pkg, relativeName).openWriter())) {
        writer.write("properties:\n");
        for (Property property : properties.values()) {
          writer.write(property + "\n");
        }
      } catch (IOException ex) {
        throw new UndeclaredThrowableException(ex);
      }
    }
    return false;
  }

  private String typeToString(TypeMirror typeMirror) {
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
        Name simpleName = ((DeclaredType) typeMirror).asElement().getSimpleName();
        return "[" + simpleName + "](!SITE_URL!/modules/" + simpleName + "/)";
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

  private static final String GET = "get";
  private static final int GET_LENGTH = GET.length();
  private static final String IS = "is";
  private static final int IS_LENGTH = IS.length();

  private String extractPropertyNameFromGetter(String methodName) {
    if (methodName.startsWith(GET) && methodName.length() > GET_LENGTH) {
      char firstChar = methodName.charAt(GET_LENGTH);
      if (Character.isUpperCase(firstChar)) {
        return Character.toLowerCase(firstChar) + methodName.substring(GET_LENGTH + 1);
      }
    } else {
      if (methodName.startsWith(IS) && methodName.length() > IS_LENGTH) {
        char firstChar = methodName.charAt(IS_LENGTH);
        if (Character.isUpperCase(firstChar)) {
          return Character.toLowerCase(firstChar) + methodName.substring(IS_LENGTH + 1);
        }
      }
    }
    throw new IllegalArgumentException("'" + methodName + "' is not a name of a getter method");
  }

  private static final String SET = "set";
  private static final int SET_LENGTH = SET.length();

  private String extractPropertyNameFromSetter(String methodName) {
    if (methodName.startsWith(SET) && methodName.length() > SET_LENGTH) {
      char firstChar = methodName.charAt(SET_LENGTH);
      if (Character.isUpperCase(firstChar)) {
        return Character.toLowerCase(firstChar) + methodName.substring(SET_LENGTH + 1);
      }
    }
    throw new IllegalArgumentException("'" + methodName + "' is not a name of a setter method");
  }

}
