package net.wizardsoflua.wol.luatickslimit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static net.wizardsoflua.config.GeneralConfig.MAX_LUA_TICKS_LIMIT;
import static net.wizardsoflua.config.GeneralConfig.MIN_LUA_TICKS_LIMIT;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.WolServer;

public class SetLuaTicksLimitCommand implements Command<CommandSource> {
  private static final String LIMIT_ARGUMENT = "limit";

  private final WolServer wol;

  public SetLuaTicksLimitCommand(WolServer wol) {
    this.wol = checkNotNull(wol, "wol==null!");
  }

  public void register(CommandDispatcher<CommandSource> dispatcher) {
    int min = MIN_LUA_TICKS_LIMIT;
    int max = MAX_LUA_TICKS_LIMIT;
    dispatcher.register(//
        literal("wol")//
            .then(literal("luaTicksLimit")//
                .then(literal("set")//
                    .then(argument(LIMIT_ARGUMENT, integer(min, max))//
                        .executes(this)))));
  }

  @Override
  public int run(CommandContext<CommandSource> context) {
    int limit = IntegerArgumentType.getInteger(context, LIMIT_ARGUMENT);
    limit = wol.getConfig().getGeneralConfig().setLuaTicksLimit(limit);
    // TODO I18n
    WolAnnouncementMessage message =
        new WolAnnouncementMessage("luaTicksLimit has been updated to " + limit);
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
