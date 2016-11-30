package net.karneim.luamod.lua;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.karneim.luamod.LuaMod;
import net.karneim.luamod.credentials.Credentials;
import net.karneim.luamod.cursor.Clipboard;
import net.karneim.luamod.cursor.CursorUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.sandius.rembulan.load.LoaderException;

public class CommandLua extends CommandBase {

  private static final String GIT_HUB = "GitHub";
  private static final String CMD_NAME = "lua";
  private static final String MSG_USAGE = "commands.lua.usage";

  private final LuaMod mod;
  private final List<String> aliases = new ArrayList<String>();

  public CommandLua() {
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
    // TODO ?
    return Collections.<String>emptyList();
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {
    try {
      ICommandSender owner = getOwner(sender);
      Clipboard clipboard = getClipboard(owner);
      BlockPos pos = getBlockPos(sender);
      Rotation rot = getRotation(sender);
      EnumFacing surface = getSurface(sender);
      @Nullable
      String prog = getProgram(owner, args);

      SpellEntity spell = new SpellEntity(sender.getEntityWorld(), LuaMod.instance, owner,
          clipboard, pos, rot, surface);
      spell.setProgram(prog);
      sender.getEntityWorld().spawnEntityInWorld(spell);
      if (sender.sendCommandFeedback()) {
        // this is true if "gamerule commandBlockOutput" is true
        notifyCommandListener(sender, this, "%s created", spell.getName());
      }
      spell.onUpdate();
      // sender.setCommandStat(CommandResultStats.Type.SUCCESS_COUNT, 1);
    } catch (IOException e) {
      throw new CommandException("Can't execute %s. Caught %s", getCommandName(), e.getMessage());
    } catch (LoaderException e) {
      throw new CommandException("Can't execute %s. Caught %s", getCommandName(), e.getMessage());
    }
  }

  private Clipboard getClipboard(ICommandSender owner) {
    if (owner.getCommandSenderEntity() instanceof SpellEntity) {
      SpellEntity lpe = (SpellEntity) owner;
      return lpe.getClipboard();
    } else if (owner.getCommandSenderEntity() instanceof EntityPlayer) {
      return mod.getClipboards().get((EntityPlayer) owner.getCommandSenderEntity());
    }
    return new Clipboard();
  }

  private String getProgram(ICommandSender owner, String[] args) throws IOException {
    Entity entity = owner.getCommandSenderEntity();
    @Nullable
    String prog = loadProfile(entity);
    if (args.length > 0) {
      String argString = "";
      for (String arg : args) {
        argString = argString + " " + arg;
      }
      if (prog == null) {
        prog = "";
      } else {
        prog = prog + "\n";
      }
      prog = prog + argString;
    }
    return prog;
  }

  private EnumFacing getSurface(ICommandSender sender) {
    Entity entity = sender.getCommandSenderEntity();
    if (entity == null) {
      return null;
    } else if (entity instanceof SpellEntity) {
      return null;
    } else {
      EnumFacing side = CursorUtil.getSideLookingAt(entity);
      return side == null ? null : side;
    }
  }

  private Rotation getRotation(ICommandSender sender) {
    Entity entity = sender.getCommandSenderEntity();
    if (entity == null) {
      return Rotation.NONE;
    } else if (entity instanceof SpellEntity) {
      SpellEntity luaProcessEntity = (SpellEntity) entity;
      return luaProcessEntity.getCursor().getRotation();
    } else {
      return CursorUtil.getRotation(entity.getHorizontalFacing());
    }
  }

  private BlockPos getBlockPos(ICommandSender sender) {
    Entity entity = sender.getCommandSenderEntity();
    if (entity == null) {
      return sender.getPosition();
    } else if (entity instanceof SpellEntity) {
      SpellEntity luaProcessEntity = (SpellEntity) entity;
      return luaProcessEntity.getCursor().getWorldPosition();
    } else {
      return CursorUtil.getPositionLookingAt(entity);
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

  private String loadProfile(Entity player) throws IOException {
    @Nullable
    String url = mod.getProfileUrls().getProfileUrl(player);
    if (url == null) {
      url = mod.getProfileUrls().getDefaultProfileUrl();
    }
    return loadGist(player, url);
  }

  private String loadGist(Entity player, String gistUrl) throws IOException {
    @Nullable
    String prog = null;
    if (gistUrl != null) {
      @Nullable
      Credentials credentials = (player == null)
          ? mod.getCredentialsStore().retrieveCredentials(GIT_HUB, "default")
          : mod.getCredentialsStore().retrieveCredentials(GIT_HUB, player.getUniqueID().toString());
      prog = mod.getGistRepo().load(credentials, gistUrl);
    }
    return prog;
  }


}
