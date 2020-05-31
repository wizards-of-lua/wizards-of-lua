package net.wizardsoflua.wol.spelloutput;

import static net.minecraft.command.Commands.literal;

import javax.inject.Inject;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandSource;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.config.WolConfig;
import net.wizardsoflua.extension.server.spi.CommandRegisterer;

@AutoService(CommandRegisterer.class)
public class PrintSpellOutputCommand implements CommandRegisterer, Command<CommandSource> {
  @Inject
  private WolConfig config;

  @Override
  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("wol")//
            .then(literal("spellOutput")//
                .executes(this)));
  }

  @Override
  public int run(CommandContext<CommandSource> context) {
    boolean spellOutput = config.getGeneralConfig().getSpellOutput();
    WolAnnouncementMessage message = new WolAnnouncementMessage("spellOutput = " + spellOutput);
    CommandSource source = context.getSource();
    source.sendFeedback(message, true);
    return Command.SINGLE_SUCCESS;
  }
}
