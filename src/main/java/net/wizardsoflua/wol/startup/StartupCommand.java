package net.wizardsoflua.wol.startup;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.minecraft.command.Commands.literal;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.wizardsoflua.WolServer;

public class StartupCommand implements Command<CommandSource> {

  private final WolServer wol;

  public StartupCommand(WolServer wol) {
    this.wol = checkNotNull(wol, "wol == null!");
  }

  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("wol")//
            .then(literal("startup")//
                .executes(this)));
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    wol.runStartupSequence(context.getSource());
    return Command.SINGLE_SUCCESS;
  }
}
