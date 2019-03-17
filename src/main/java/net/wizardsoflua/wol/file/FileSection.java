package net.wizardsoflua.wol.file;

import static net.minecraft.command.Commands.literal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.CommandSource;

public enum FileSection {
  PERSONAL("file"), //
  SHARED("shared-file"), //
  ;
  private final LiteralArgumentBuilder<CommandSource> commandLiteral;

  private FileSection(String commandLiteral) {
    this.commandLiteral = literal(commandLiteral);
  }

  public LiteralArgumentBuilder<CommandSource> getCommandLiteral() {
    return commandLiteral;
  }
}
