package net.wizardsoflua.wol;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public interface WolCommandAction {

  void execute(ICommandSender sender) throws CommandException;
}
