package net.wizardsoflua.wol.luatickslimit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.WolServer;

public class SetEventListenerLuaTicksLimitCommand implements Command<CommandSource> {
  private static final String LIMIT_ARGUMENT = "limit";

  private final WolServer wol;

  public SetEventListenerLuaTicksLimitCommand(WolServer wol) {
    this.wol = checkNotNull(wol, "wol==null!");
  }

  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("wol")//
            .then(literal("eventListenerLuaTicksLimit")//
                .then(literal("set")//
                    .then(argument(LIMIT_ARGUMENT, integer())//
                        .executes(this)))));
  }

  @Override
  public int run(CommandContext<CommandSource> context) {
    int limit = IntegerArgumentType.getInteger(context, LIMIT_ARGUMENT);
    wol.getConfig().getGeneralConfig().setEventListenerLuaTicksLimit(limit);
    // TODO I18n
    WolAnnouncementMessage message =
        new WolAnnouncementMessage("eventListenerLuaTicksLimit has been updated to " + limit);
    context.getSource().sendFeedback(message, true);
    return Command.SINGLE_SUCCESS;
  }

  // @Override
  // public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
  // Deque<String> argList, BlockPos targetPos) {
  // if (argList.size() == 1) {
  // return getMatchingTokens(argList.poll(), "1000", "10000", "100000", "1000000", "10000000");
  // }
  // return Collections.emptyList();
  // }
}
