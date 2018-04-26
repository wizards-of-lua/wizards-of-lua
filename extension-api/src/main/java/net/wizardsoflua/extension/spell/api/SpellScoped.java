package net.wizardsoflua.extension.spell.api;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Scope;
import javax.inject.Singleton;

import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.spi.SpellExtension;

/**
 * A {@link Scope} annotation identifiying a type that is instantiated at most once per spell. A
 * {@link SpellScoped} type may declare dependencies on {@link Resource}s in the package
 * {@link net.wizardsoflua.extension.spell.api.resource}.
 * <p>
 * Sub classes of {@link SpellExtension} are always considered to be {@link SpellScoped} unless they
 * are annotated with a different {@link Scope} annotation like @{@link Singleton}.
 *
 * @author Adrodoc
 */
@Documented
@Retention(RUNTIME)
@Scope
@Target(TYPE)
public @interface SpellScoped {

}
