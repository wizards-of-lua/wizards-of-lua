package net.wizardsoflua.extension.server.spi;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.server.api.ServerScoped;

/**
 * Subclasses are not required to have a zero-argument constructor, but they need an injectable
 * constructor as defined in {@link javax.inject.Inject}. Additionally sub classes may declare
 * injectable resources with @{@link Resource}.
 *
 * @author Adrodoc
 */
@ServerScoped
public interface CommandRegisterer {
  void register(CommandDispatcher<CommandSource> dispatcher);
}
