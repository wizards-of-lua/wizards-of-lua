package net.wizardsoflua.wol.luatickslimit;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.minecraft.command.Commands.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandSource;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;

public class PrintLuaTicksLimitCommand implements Command<CommandSource> {
  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("wol")//
            .then(literal("luaTicksLimit")//
                .executes(this)));
  }

  private final WizardsOfLua wol;

  public PrintLuaTicksLimitCommand(WizardsOfLua wol) {
    this.wol = checkNotNull(wol, "wol==null!");
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
