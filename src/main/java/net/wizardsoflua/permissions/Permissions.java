package net.wizardsoflua.permissions;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.world.GameType;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wizardsoflua.spell.SpellEntity;

public class Permissions {

  private Set<String> restricted;
  private EnumSet<GameType> wizardGameModes;

  public Permissions(Iterable<String> restricted, EnumSet<GameType> wizardGameModes) {
    this.restricted = Sets.newHashSet(restricted);
    this.wizardGameModes = Preconditions.checkNotNull(wizardGameModes);
  }

  @SubscribeEvent
  public void onEvent(CommandEvent event) {
    if (!isServer(event.getSender()) && !isOperator(event.getSender())) {
      if (isDenied(event.getCommand().getName()) || isDenied(event.getCommand().getAliases())) {
        String message = String.format("Command %s is denied for sender %s.",
            event.getCommand().getName(), event.getSender().getName());
        event.setCanceled(true);
        event.setException(new RuntimeException(message));
      }
    }
  }

  private boolean isServer(ICommandSender sender) {
    return sender instanceof MinecraftServer;
  }

  private boolean isDenied(List<String> aliases) {
    for (String alias : aliases) {
      if (isDenied(alias)) {
        return true;
      }
    }
    return false;
  }

  private boolean isDenied(String name) {
    return restricted.contains(name);
  }

  public boolean checkPermissionToCastASpell(ICommandSender sender) {
    GameType gamemode = getGameMode(sender);
    return isOperator(sender) || gamemode == GameType.NOT_SET || wizardGameModes.contains(gamemode);
  }

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

  private boolean isOperator(ICommandSender sender) {
    Entity e = sender.getCommandSenderEntity();
    if (e instanceof EntityPlayerMP) {
      EntityPlayerMP player = (EntityPlayerMP) e;
      UserListOpsEntry entry = (UserListOpsEntry) player.mcServer.getPlayerList().getOppedPlayers()
          .getEntry(player.getGameProfile());
      return entry != null;
    }
    if (e instanceof SpellEntity) {
      SpellEntity spell = (SpellEntity) e;
      return isOperator(spell.getOwner());
    }
    return false;
  }
}
