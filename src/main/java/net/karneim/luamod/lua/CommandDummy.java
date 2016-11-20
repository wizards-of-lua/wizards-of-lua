package net.karneim.luamod.lua;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.karneim.luamod.LuaMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class CommandDummy extends CommandBase {

  private static final String CMD_NAME = "dummy";
  private static final String MSG_USAGE = "commands.dummy.usage";

  private final LuaMod mod;
  private final List<String> aliases = new ArrayList<String>();

  public CommandDummy() {
    aliases.add(CMD_NAME);
    mod = LuaMod.instance;
  }

  @Override
  public String getCommandName() {
    return CMD_NAME;
  }

  @Override
  public int getRequiredPermissionLevel() {
    // return 2;
    return 0;
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return MSG_USAGE;
  }

  @Override
  public List getCommandAliases() {
    return aliases;
  }

  @Override
  public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender,
      String[] args, @Nullable BlockPos pos) {
    // TODO
    return Collections.<String>emptyList();
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    System.out.println("here we are!");
    if (!sender.getEntityWorld().isRemote) {
      if (args != null && args.length > 0) {
        String txt = args[0];
        if (txt == null) {
          txt = "hello";
        }
        sender.addChatMessage(new TextComponentString(txt));
      }
    }
  }

}
