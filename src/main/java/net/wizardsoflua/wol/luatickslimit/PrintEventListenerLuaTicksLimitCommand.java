package net.wizardsoflua.wol.luatickslimit;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.minecraft.command.Commands.literal;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.WolServer;

public class PrintEventListenerLuaTicksLimitCommand implements Command<CommandSource> {

  private final WolServer wol;

  public PrintEventListenerLuaTicksLimitCommand(WolServer wol) {
    this.wol = checkNotNull(wol, "wol==null!");
  }

  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("wol")//
            .then(literal("eventListenerLuaTicksLimit")//
                .executes(this)));
  }

  @Override
  public int run(CommandContext<CommandSource> context) {
    int eventListenerLuaTicksLimit =
        wol.getConfig().getGeneralConfig().getEventListenerLuaTicksLimit();
    WolAnnouncementMessage message =
        new WolAnnouncementMessage("eventListenerLuaTicksLimit = " + eventListenerLuaTicksLimit);
    CommandSource source = context.getSource();
    source.sendFeedback(message, true);
    return Command.SINGLE_SUCCESS;
  }
}
