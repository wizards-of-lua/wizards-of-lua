package net.wizardsoflua.wol.luatickslimit;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.minecraft.command.Commands.literal;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.WolServer;

public class PrintLuaTicksLimitCommand implements Command<CommandSource> {

  private final WolServer wol;

  public PrintLuaTicksLimitCommand(WolServer wol) {
    this.wol = checkNotNull(wol, "wol==null!");
  }

  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("wol")//
            .then(literal("luaTicksLimit")//
                .executes(this)));
  }

  @Override
  public int run(CommandContext<CommandSource> context) {
    long luaTicksLimit = wol.getConfig().getGeneralConfig().getLuaTicksLimit();
    WolAnnouncementMessage message = new WolAnnouncementMessage("luaTicksLimit = " + luaTicksLimit);
    CommandSource source = context.getSource();
    source.sendFeedback(message, true);
    return Command.SINGLE_SUCCESS;
  }
}
