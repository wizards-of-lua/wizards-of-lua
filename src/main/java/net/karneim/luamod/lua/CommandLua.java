package net.karneim.luamod.lua;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.karneim.luamod.LuaMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.sandius.rembulan.load.LoaderException;

public class CommandLua extends CommandBase {

  private static final String GIT_HUB = "GitHub";
  private static final String CMD_NAME = "lua";
  private static final String MSG_USAGE = "commands.lua.usage";

  private final LuaMod mod;
  private final List<String> aliases = new ArrayList<String>();

  private SpellEntityFactory spellEntityFactory;

  public CommandLua() {
    aliases.add(CMD_NAME);
    mod = LuaMod.instance;
  }

  @Override
  public String getCommandName() {
    return CMD_NAME;
  }

  @Override
  public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
    Permissions permissions = mod.getPermissions();
    boolean result = permissions.isWizard(sender);
    if (result == false) {
      result = super.checkPermission(server, sender);
    }
    return result;
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 2;
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
    // TODO ?
    return Collections.<String>emptyList();
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    try {
      ICommandSender owner = getOwner(sender);
      SpellEntity spell =
          mod.getSpellEntityFactory().create(sender.getEntityWorld(), sender, owner);

      server.getEntityWorld().spawnEntityInWorld(spell);
      addDefaultProfile(spell);
      addUserProfile(owner, spell);
      spell.setCommand(getArgString(args));

      if (sender.sendCommandFeedback()) {
        // this is true if "gamerule commandBlockOutput" is true
        notifyCommandListener(sender, this, "%s created", spell.getName());
      }
    } catch (IOException e) {
      throw new CommandException("Can't execute %s. Caught %s", getCommandName(), e.getMessage());
    } catch (LoaderException e) {
      throw new CommandException("Can't execute %s. Caught %s", getCommandName(), e.getMessage());
    }
  }

  private void addUserProfile(ICommandSender owner, SpellEntity spell) throws IOException {
    String userProfile = mod.getProfiles().getUserProfile(owner.getCommandSenderEntity());
    if (userProfile != null) {
      spell.addProfile(userProfile);
    }
  }

  private void addDefaultProfile(SpellEntity spell) throws IOException {
    String defaultProfile = mod.getProfiles().getDefaultProfile();
    if (defaultProfile != null) {
      spell.addProfile(defaultProfile);
    }
  }

  private ICommandSender getOwner(ICommandSender sender) {
    Entity entity = sender.getCommandSenderEntity();
    if (entity instanceof SpellEntity) {
      SpellEntity luaProcessEntity = (SpellEntity) entity;
      return luaProcessEntity.getOwner();
    } else {
      return sender;
    }
  }

  private String getArgString(String[] args) throws IOException {
    if (args.length > 0) {
      String argString = "";
      for (String arg : args) {
        argString = argString + " " + arg;
      }
      return argString;
    }
    return "";
  }

}
