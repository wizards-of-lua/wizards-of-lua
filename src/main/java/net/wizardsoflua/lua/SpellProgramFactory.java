package net.wizardsoflua.lua;

import com.google.common.base.Preconditions;

import net.minecraft.command.ICommandSender;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.wizardsoflua.lua.scheduling.SpellSchedulingContextFactory;

public class SpellProgramFactory {

  private final SpellSchedulingContextFactory contextFactory;

  public SpellProgramFactory(SpellSchedulingContextFactory contextFactory) {
    this.contextFactory = Preconditions.checkNotNull(contextFactory, "contextFactory==null!");
  }

  public SpellProgram create(ICommandSender owner, String code) {
    DirectCallExecutor executor = DirectCallExecutor.newExecutor(contextFactory);
    return new SpellProgram(owner, code, executor);
  }
}
