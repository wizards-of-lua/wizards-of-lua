package net.wizardsoflua.wol.luatickslimit;

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
public class PrintEventListenerLuaTicksLimitCommand
    implements CommandRegisterer, Command<CommandSource> {
  @Inject
  private WolConfig config;

  @Override
  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("wol")//
            .then(literal("eventListenerLuaTicksLimit")//
                .executes(this)));
  }

  @Override
  public int run(CommandContext<CommandSource> context) {
    long eventListenerLuaTicksLimit = config.getGeneralConfig().getEventListenerLuaTicksLimit();
    WolAnnouncementMessage message =
        new WolAnnouncementMessage("eventListenerLuaTicksLimit = " + eventListenerLuaTicksLimit);
    CommandSource source = context.getSource();
    source.sendFeedback(message, true);
    return Command.SINGLE_SUCCESS;
  }
}
