package net.wizardsoflua.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import com.google.common.reflect.TypeToken;

public class ReflectionUtils {
  /**
   * Returns whether {@code superMethod} is overridden by {@code subMethod}.
   *
   * @param superMethod
   * @param subMethod
   * @return whether {@code superMethod} is overridden by {@code subMethod}
   */
  public static boolean isOverridden(Method superMethod, Method subMethod) {
    Class<?> superClass = superMethod.getDeclaringClass();
    Class<?> subClass = subMethod.getDeclaringClass();
    if (!superClass.isAssignableFrom(subClass)) {
      return false;
    }
    int superModifiers = superMethod.getModifiers();
    if (Modifier.isPrivate(superModifiers)) {
      return false;
    }
    if (!Modifier.isProtected(superModifiers) && !Modifier.isPublic(superModifiers)
        && superClass.getPackage() != subClass.getPackage()) {
      return false;
    }
    if (!superMethod.getName().equals(subMethod.getName())) {
      return false;
    }
    if (superMethod.getParameterCount() != subMethod.getParameterCount()) {
      return false;
    }
    TypeToken<?> subClassToken = TypeToken.of(subClass);
    Type[] superParamTypes = superMethod.getGenericParameterTypes();
    Type[] subParamTypes = subMethod.getGenericParameterTypes();
    for (int i = 0; i < superParamTypes.length; i++) {
      Type superParamType = superParamTypes[i];
      Type subParamType = subParamTypes[i];
      TypeToken<?> resolvedSuperParamTypeToken = subClassToken.resolveType(superParamType);
      TypeToken<?> subParamTypeToken = TypeToken.of(subParamType);
      if (!resolvedSuperParamTypeToken.getRawType().equals(subParamTypeToken.getRawType())) {
        return false;
      }
    }
    return true;
  }
}
