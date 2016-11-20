package net.karneim.luamod.lua;

import javax.annotation.Nullable;

import net.karneim.luamod.LuaMod;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.server.CommandMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class CommandMessagePatched extends CommandMessage {
  private final LuaMod mod;

  public CommandMessagePatched(LuaMod mod) {
    this.mod = mod;
  }

  /**
   * Callback for when the command is executed
   */
  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    if (args.length < 2) {
      // this will throw an exception
      super.execute(server, sender, args);
    } else {
      LuaProcessEntity processEntity = getLuaProcessEntity(server, sender, args[0]);
      if (processEntity != null) {
        processEntity.onWhisperMessage(sender, concat(args,1,args.length));
      } else { 
        // do default handling
        super.execute(server, sender, args);
      }
    }
  }

  private String concat(String[] text, int from, int to) {
    StringBuilder result = new StringBuilder();
    for( int i=from; i<to; ++i) {
      if ( result.length()>0) {
        result.append(" ");
      }
      result.append(text[i]);
    }
    return result.toString();
  }

  private @Nullable LuaProcessEntity getLuaProcessEntity(MinecraftServer server, ICommandSender sender, String name) {
    return mod.getProcessRegistry().get(name);
  }
}
