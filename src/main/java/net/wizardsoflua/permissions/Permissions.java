package net.wizardsoflua.permissions;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandExecuteAt;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.world.GameType;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.spell.SpellEntity;

public class Permissions {

  private Set<String> restricted;
  private EnumSet<GameType> wizardGameModes;

  public Permissions(Iterable<String> restricted, EnumSet<GameType> wizardGameModes) {
    this.restricted = Sets.newHashSet(restricted);
    this.wizardGameModes = Preconditions.checkNotNull(wizardGameModes);
  }

  public boolean checkPermissionForLuaCommand(ICommandSender sender) {
    GameType gamemode = getGameMode(sender);
    return hasOperatorPrivileges(sender) || gamemode == GameType.NOT_SET
        || wizardGameModes.contains(gamemode);
  }

  public boolean checkPermissionForWolCommand(ICommandSender sender) {
    GameType gamemode = getGameMode(sender);
    return hasOperatorPrivileges(sender) || gamemode == GameType.NOT_SET
        || wizardGameModes.contains(gamemode);
  }

  /**
   * This method intercepts all server-side commands and cancels those, that are not allowed. This
   * should onyl affect commands that are sent by spells.
   * 
   * @param event
   */
  @SubscribeEvent
  public void onEvent(CommandEvent event) {
    if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
      return;
    }
    SpellEntity spell = getAsSpell(event.getSender());
    if (spell != null) {
      boolean denied = false;
      if (!hasOperatorPrivileges(spell)) {
        // we will restrict certain commands from no-op senders
        boolean isCmdDenied = isDenied(event.getCommand().getName());
        boolean isAliasDenied = isDenied(event.getCommand().getAliases());
        if (isCmdDenied || isAliasDenied) {
          String message = String.format("Command %s is denied for sender %s.",
              event.getCommand().getName(), event.getSender().getName());
          event.setCanceled(true);
          // event.setException(new RuntimeException(message));
          WizardsOfLua.instance.logger.warn(message);
          denied = true;
        } else if (isExecuteCommandAndTargetHasOperatorPrivileges(event)) {
          String message = String.format(
              "Command %s is denied for sender %s because target has operator privileges.",
              event.getCommand().getName(), event.getSender().getName());
          event.setCanceled(true);
          // event.setException(new RuntimeException(message));
          WizardsOfLua.instance.logger.warn(message);
          denied = true;
        }
      }
      spell.setLastCommandWasDenied(denied);
    }
  }

  /**
   * Returns the {@link SpellEntity} assigned to the sender if any, <code>null</code> otherwise.
   * 
   * @param sender
   * @return the {@link SpellEntity} assigned to the sender if any, <code>null</code> otherwise
   */
  private @Nullable SpellEntity getAsSpell(ICommandSender sender) {
    Entity entity = sender.getCommandSenderEntity();
    if (entity instanceof SpellEntity) {
      return (SpellEntity) entity;
    }
    return null;
  }


  /**
   * Returns <code>true</code> if this event is about a {@link CommandExecuteAt} and if the
   * execution target is an entity with operator privileges.
   * 
   * @param event
   * @return <code>true</code> if the given event is about a {@link CommandExecuteAt} and if the
   *         execution target is an entity with operator privileges
   */
  private boolean isExecuteCommandAndTargetHasOperatorPrivileges(CommandEvent event) {
    if (!(event.getCommand() instanceof CommandExecuteAt)) {
      return false;
    }
    ICommandSender sender = event.getSender();
    MinecraftServer server = event.getSender().getEntityWorld().getMinecraftServer();
    String[] args = event.getParameters();
    try {
      Entity target = CommandBase.getEntity(server, sender, args[0], Entity.class);
      return hasOperatorPrivileges(target);
    } catch (CommandException e) {
      return false;
    }
  }

  /**
   * Returns <code>true</code> if the given sender represents the server console (or a spell that
   * was sent from the server console directly or indirectly).
   * 
   * @param sender
   * @return <code>true</code> if the given sender represents the server console (or a spell that
   *         was sent from the server console directly or indirectly)
   */
  private boolean isServer(ICommandSender sender) {
    Entity entity = sender.getCommandSenderEntity();
    if (entity instanceof SpellEntity) {
      SpellEntity spell = (SpellEntity) entity;
      return isServer(spell.getOwner());
    }
    return sender instanceof MinecraftServer;
  }

  /**
   * Returns <code>true</code> if one of the given command aliases is denied from execution.
   * 
   * @param aliases
   * @return <code>true</code> if one of the given command aliases is denied from execution
   */
  private boolean isDenied(List<String> aliases) {
    for (String alias : aliases) {
      if (isDenied(alias)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns <code>true</code> if the given command is denied from execution.
   * 
   * @param name
   * @return <code>true</code> if the given command is denied from execution
   */
  private boolean isDenied(String name) {
    return restricted.contains(name);
  }

  /**
   * Returns the game mode of the given command sender. That can be {@link GameType#NOT_SET}, if the
   * sender is not a player or spell.
   * 
   * @param sender
   * @return the game mode of the given command sender
   */
  private GameType getGameMode(ICommandSender sender) {
    Entity e = sender.getCommandSenderEntity();
    if (e instanceof EntityPlayerMP) {
      EntityPlayerMP player = (EntityPlayerMP) e;
      return player.interactionManager.getGameType();
    }
    if (e instanceof SpellEntity) {
      SpellEntity spell = (SpellEntity) e;
      return getGameMode(spell.getOwner());
    }
    return GameType.NOT_SET;
  }

  /**
   * Returns <code>true</code> if the given sender has operator privileges.
   * 
   * @param sender
   * @return <code>true</code> if the given sender has operator privileges
   */
  private boolean hasOperatorPrivileges(ICommandSender sender) {
    if (sender instanceof Entity) {
      return hasOperatorPrivileges((Entity) sender);
    }
    if (isServer(sender)) {
      return true;
    }
    return hasOperatorPrivileges(sender.getCommandSenderEntity());
  }

  /**
   * Returns <code>true</code> if the given entity has operator privileges.
   * 
   * @param e
   * @return <code>true</code> if the given entity has operator privileges
   */
  private boolean hasOperatorPrivileges(Entity e) {
    if (e instanceof EntityPlayerMP) {
      EntityPlayerMP player = (EntityPlayerMP) e;
      UserListOpsEntry entry = (UserListOpsEntry) player.mcServer.getPlayerList().getOppedPlayers()
          .getEntry(player.getGameProfile());
      return entry != null;
    }
    if (e instanceof SpellEntity) {
      SpellEntity spell = (SpellEntity) e;
      return hasOperatorPrivileges(spell.getOwner());
    }
    return false;
  }
}
