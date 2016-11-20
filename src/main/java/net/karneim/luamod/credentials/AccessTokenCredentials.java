package net.karneim.luamod.credentials;

public class AccessTokenCredentials implements Credentials {
  public final String token;

  public AccessTokenCredentials(String token) {
    this.token = token;
  }
  
}
