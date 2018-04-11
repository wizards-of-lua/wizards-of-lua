package net.wizardsoflua.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(SOURCE)
@Target(TYPE)
public @interface GenerateLuaTable {
  boolean modifiable();

  String ADDITIONAL_FUNCTIONS = "additionalFunctions";

  Class<?> additionalFunctions() default Void.class;

  boolean includeFunctions() default true;
}
