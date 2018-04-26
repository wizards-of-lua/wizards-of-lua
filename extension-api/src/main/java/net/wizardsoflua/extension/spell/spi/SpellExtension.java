package net.wizardsoflua.extension.spell.spi;

import java.lang.annotation.Inherited;
import java.util.ServiceLoader;

import javax.inject.Scope;
import javax.inject.Singleton;

import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.SpellScoped;

/**
 * A {@link ServiceLoader service provider interface (SPI)} for extensions that are
 * {@link SpellScoped}. Each subclass that is discovered via this SPI is instantiated for each new
 * spell. Even though @{@link SpellScoped} is not {@link Inherited} all sub classes are considered
 * to be {@link SpellScoped} unless they are annotated with a different {@link Scope} annotation
 * like @{@link Singleton}.
 * <p>
 * Subclasses are not required to have a zero-argument constructor, but they need an injectable
 * constructor as defined in {@link javax.inject.Inject}. Additionally sub classes may declare
 * injectable resources with @{@link Resource}.
 *
 * @author Adrodoc
 */
@SpellScoped
public interface SpellExtension {

}
