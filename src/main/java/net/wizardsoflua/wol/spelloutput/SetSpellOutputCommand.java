package net.wizardsoflua.wol.spelloutput;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

import javax.inject.Inject;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandSource;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.config.WolConfig;
import net.wizardsoflua.extension.server.spi.CommandRegisterer;

@AutoService(CommandRegisterer.class)
public class SetSpellOutputCommand implements CommandRegisterer, Command<CommandSource> {
  private static final String VALUE_ARGUMENT = "value";
  @Inject
  private WolConfig config;

  @Override
  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("wol")//
            .then(literal("spellOutput")//
                .then(literal("set")//
                    .then(argument(VALUE_ARGUMENT, bool())//
                        .executes(this)))));
  }

  @Override
  public int run(CommandContext<CommandSource> context) {
    boolean value = BoolArgumentType.getBool(context, VALUE_ARGUMENT);
    config.getGeneralConfig().setSpellOutput(value);
    // TODO I18n
    WolAnnouncementMessage message =
        new WolAnnouncementMessage("spellOutput has been updated to " + value);
    context.getSource().sendFeedback(message, true);
    return Command.SINGLE_SUCCESS;
  }

}
