package net.wizardsoflua.lua;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.WolAnnouncementMessage;
import net.wizardsoflua.spell.SpellEntity;

public class LuaCommand extends CommandBase {
  private static final String CMD_NAME = "lua";
  private final WizardsOfLua wol = WizardsOfLua.instance;
  private final List<String> aliases = new ArrayList<String>();

  public LuaCommand() {
    aliases.add(CMD_NAME);
  }

  @Override
  public String getName() {
    return CMD_NAME;
  }

  @Override
  public String getUsage(ICommandSender sender) {
    // TODO return usage
    return "";
  }

  /**
   * Return the required permission level for this command.
   */
  public int getRequiredPermissionLevel() {
    // TODO add real permission checking somewhere
    return 2;
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    try {
      World world = sender.getEntityWorld();
      SpellEntity spell = wol.getSpellEntityFactory().create(world, sender, concat(args));
      world.spawnEntity(spell);
    } catch (Exception ex) {
      handleException(ex, sender);
    }
  }

  private String concat(String[] args) {
    return Joiner.on(" ").join(args);
  }

  private void handleException(Exception ex, ICommandSender sender) {
    String message = String.format("An unexpected error occured during lua command execution: %s",
        ex.getMessage());
    wol.logger.error(message, ex);
    String stackTrace = getStackTrace(ex);
    WolAnnouncementMessage txt = new WolAnnouncementMessage(message);
    TextComponentString details = new TextComponentString(stackTrace);
    txt.setStyle((new Style()).setColor(TextFormatting.RED).setBold(Boolean.valueOf(true)));
    txt.appendSibling(details);
    sender.sendMessage(txt);
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
