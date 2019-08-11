package net.wizardsoflua;

import static net.minecraft.util.text.TextFormatting.RED;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class CommandSourceUtils {
  private static ICommandSource getICommandSource(CommandSource source) {
    return ObfuscationReflectionHelper.getPrivateValue(CommandSource.class, source, "source");
  }

  public static void sendAndLogFeedback(CommandSource source, ITextComponent message) {
    source.sendFeedback(message, false);
    MinecraftServer server = source.getServer();
    if (server != getICommandSource(source)) {
      server.sendMessage(message);
    }
  }

  public static void sendAndLogErrorMessage(CommandSource source, ITextComponent message) {
    source.sendErrorMessage(message);
    MinecraftServer server = source.getServer();
    if (server != getICommandSource(source)) {
      server.sendMessage(new TextComponentString("").appendSibling(message).applyTextStyle(RED));
    }
  }
}
