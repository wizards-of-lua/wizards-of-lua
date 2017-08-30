package net.wizardsoflua.wol.menu;

import java.util.Deque;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public interface CommandAction {

  void execute(ICommandSender sender, Deque<String> argList) throws CommandException;
}
