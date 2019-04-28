package net.wizardsoflua;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.inject.Scope;
import net.minecraft.server.MinecraftServer;
import net.wizardsoflua.extension.api.inject.Resource;

/**
 * A {@link Scope} annotation identifiying a type that is instantiated at most once per
 * {@link MinecraftServer}. A {@link ServerScoped} type may declare dependencies on the
 * {@link Resource} {@link MinecraftServer}.
 * 
 * @author Adrodoc
 */
@Documented
@Retention(RUNTIME)
@Scope
@Target(TYPE)
public @interface ServerScoped {

}
