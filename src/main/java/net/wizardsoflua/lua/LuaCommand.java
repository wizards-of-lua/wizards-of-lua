package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.lua.module.print.PrintRedirector.PrintReceiver;

public class LuaCommand implements Command<CommandSource> {

  public static void register(CommandDispatcher<CommandSource> dispatcher, WizardsOfLua wol) {
    new LuaCommand(wol).register(dispatcher);
  }

  private final WizardsOfLua wol;

  public LuaCommand(WizardsOfLua wol) {
    this.wol = checkNotNull(wol, "wol == null!");
  }

  private void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(//
        literal("lua")//
            .then(argument("code", greedyString())//
                .executes(this)));

  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    CommandSource source = context.getSource();
    try {
      String luaCode = StringArgumentType.getString(context, "code");
      PrintReceiver printReceiver = new PrintReceiver() {
        @Override
        public void send(String message) {
          TextComponentString txt = new TextComponentString(message);
          source.sendFeedback(txt, true);
        }
      };
      wol.getSpellEntityFactory().create(source, printReceiver, luaCode);
      return Command.SINGLE_SUCCESS;
    } catch (Throwable t) {
      // FIXME check if we need to check for exceptions here or at some other place
      handleException(t, source);
      return 0;
    }
  }


  private void handleException(Throwable t, CommandSource source) {
    String message = String.format("An unexpected error occured during lua command execution: %s",
        t.getMessage());
    wol.logger.error(message, t);
    String stackTrace = getStackTrace(t);
    WolAnnouncementMessage txt = new WolAnnouncementMessage(message);
    TextComponentString details = new TextComponentString(stackTrace);
    txt.setStyle(new Style().setColor(TextFormatting.RED).setBold(Boolean.valueOf(true)));
    txt.appendSibling(details);
    source.sendFeedback(txt, true);
  }

  private String getStackTrace(Throwable throwable) {
    StringWriter writer = new StringWriter();
    throwable.printStackTrace(new PrintWriter(writer));
    String result = writer.toString();
    if (result.length() > 200) {
      result = result.substring(0, 200) + "...";
    }
    return result;
  }

}
