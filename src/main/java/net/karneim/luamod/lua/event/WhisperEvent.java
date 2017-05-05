package net.karneim.luamod.lua.event;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

public class WhisperEvent extends Event {
  private final String sender;
  private final String message;
  private final @Nullable EntityPlayerMP player;

  public WhisperEvent(String sender, @Nullable EntityPlayerMP player, String message) {
    this.sender = checkNotNull(sender, "sender == null!");
    this.player = player;
    this.message = checkNotNull(message, "message == null!");
  }

  public String getSender() {
    return sender;
  }

  public String getMessage() {
    return message;
  }

  public @Nullable EntityPlayerMP getPlayer() {
    return player;
  }
}
