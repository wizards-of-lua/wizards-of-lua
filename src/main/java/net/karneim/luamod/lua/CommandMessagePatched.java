package net.karneim.luamod.lua;

import net.karneim.luamod.lua.event.ModEventHandler;
import net.karneim.luamod.lua.event.WhisperEvent;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandMessagePatched extends CommandMessage {
  private final ModEventHandler modEventHandler;

  public CommandMessagePatched(ModEventHandler modEventHandler) {
    this.modEventHandler = modEventHandler;
  }

  /**
   * Callback for when the command is executed
   */
  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    super.execute(server, sender, args);

    EntityPlayerMP player = getPlayer(sender);
    modEventHandler
        .onEvent(new WhisperEvent(sender.getName(), player, concat(args, 1, args.length)));
  }

  private EntityPlayerMP getPlayer(ICommandSender sender) {
    Entity entity = sender.getCommandSenderEntity();
    if (entity instanceof EntityPlayerMP) {
      return (EntityPlayerMP) entity;
    }
    return null;
  }

  private String concat(String[] text, int from, int to) {
    StringBuilder result = new StringBuilder();
    for (int i = from; i < to; ++i) {
      if (result.length() > 0) {
        result.append(" ");
      }
      result.append(text[i]);
    }
    return result.toString();
  }
}
