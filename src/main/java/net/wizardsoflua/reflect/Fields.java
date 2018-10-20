package net.wizardsoflua.reflect;

import java.lang.reflect.Field;
import java.util.Set;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class Fields {
  private static final Field COMMAND_SET;

  static {
    // Alll field names can be fetched from mcpbot at https://webchat.esper.net.
    //
    // !findf commandSet 1.12
    COMMAND_SET = ReflectionHelper.findField(CommandHandler.class, "commandSet", "field_71561_b");
    COMMAND_SET.setAccessible(true);
  }

  public static Set<ICommand> getCommandSet(CommandHandler commandHandler) {
    return get(commandHandler, COMMAND_SET);
  }


  /////

  private static <T> T get(Object owner, Field field) {
    try {
      @SuppressWarnings("unchecked")
      T result = (T) field.get(owner);
      return result;
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
