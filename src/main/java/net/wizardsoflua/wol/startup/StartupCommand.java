package net.wizardsoflua.wol.startup;

import static net.minecraft.command.Commands.literal;
import javax.inject.Inject;
import com.google.auto.service.AutoService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.wizardsoflua.extension.server.spi.CommandRegisterer;
import net.wizardsoflua.startup.Startup;

@AutoService(CommandRegisterer.class)
public class StartupCommand implements CommandRegisterer, Command<CommandSource> {
  @Inject
  private Startup startup;

  @Override
  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("wol")//
            .then(literal("startup")//
                .executes(this)));
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    startup.runStartupSequence(context.getSource());
    return Command.SINGLE_SUCCESS;
  }
}
