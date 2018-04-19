package net.wizardsoflua.lua.extension.api.inject;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Deprecated
@Retention(RUNTIME)
@Target(METHOD)
public @interface AfterInjection {

}
