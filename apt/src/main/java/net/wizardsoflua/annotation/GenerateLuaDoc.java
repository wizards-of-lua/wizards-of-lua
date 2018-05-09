package net.wizardsoflua.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(SOURCE)
@Target(TYPE)
public @interface GenerateLuaDoc {
  /**
   * The name of the module. Required unless @{@link LuaClassAttributes} is present, in which case
   * the name defaults to {@link LuaClassAttributes#name()}.
   */
  String name() default "";

  /**
   * The subtitle of the module.
   */
  String subtitle() default "";

  /**
   * The document type ({@code "module"}, {@code "class"} or {@code "event"}). Defaults to
   * {@code "class"} when the type is also annotated with @{@link GenerateLuaClassTable}. Defaults
   * to {@code "module"} when the type is also annotated with @{@link GenerateLuaModuleTable}.
   */
  String type() default "";
}
