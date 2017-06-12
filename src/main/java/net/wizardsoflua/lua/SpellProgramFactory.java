package net.wizardsoflua.lua;

import net.minecraft.command.ICommandSender;

public class SpellProgramFactory {

  public SpellProgram create(ICommandSender owner, String code) {
    return new SpellProgram(owner, code);
  }
}
