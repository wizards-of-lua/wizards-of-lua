package net.wizardsoflua.lua;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.wizardsoflua.extension.InjectionScope;
import net.wizardsoflua.extension.api.inject.PostConstruct;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.server.api.ServerScoped;
import net.wizardsoflua.lua.module.print.PrintRedirector.PrintReceiver;

@ServerScoped
public class SpellProgramFactory {
  @Resource
  private InjectionScope scope;

  public SpellProgram create(World world, @Nullable Entity owner, PrintReceiver printReceiver,
      String code, String... arguments) {
    SpellProgram result = new SpellProgram(world, owner, printReceiver, code, arguments);
    scope.injectMembers(result);
    scope.callLifecycleMethods(result, PostConstruct.class);
    return result;
  }
}
