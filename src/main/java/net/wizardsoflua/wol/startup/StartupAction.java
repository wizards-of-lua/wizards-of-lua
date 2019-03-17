package net.wizardsoflua.wol.startup;

import static com.google.common.base.Preconditions.checkNotNull;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.wizardsoflua.WizardsOfLua;

public class StartupAction implements Command<CommandSource> {
  private final WizardsOfLua wol;

  public StartupAction(WizardsOfLua wol) {
    this.wol = checkNotNull(wol, "wol == null!");
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    wol.runStartupSequence(context.getSource());
    return Command.SINGLE_SUCCESS;
  }

}
