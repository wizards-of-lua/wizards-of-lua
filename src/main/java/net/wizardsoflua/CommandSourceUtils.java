package net.wizardsoflua;

import static net.minecraft.util.text.TextFormatting.RED;
import java.lang.reflect.Field;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class CommandSourceUtils {
  private static @Nullable Field sourceField;

  private static Field getSourceField() {
    if (sourceField == null) {
      try {
        sourceField = CommandSource.class.getDeclaredField("source");
        sourceField.setAccessible(true);
      } catch (NoSuchFieldException e) {
        throw new RuntimeException(e);
      }
    }
    return sourceField;
  }

  private static ICommandSource getICommandSource(CommandSource source) {
    Field field = getSourceField();
    try {
      return (ICommandSource) field.get(source);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
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
