package net.wizardsoflua.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;

public class LoginCookie {

  private final UUID playerUuid;
  private final String token;

  public LoginCookie(UUID uuid, String token) {
    this.playerUuid = checkNotNull(uuid, "uuid==null!");
    this.token = checkNotNull(token, "token==null!");
  }

  public UUID getPlayerUuid() {
    return playerUuid;
  }

  public String getToken() {
    return token;
  }

}
