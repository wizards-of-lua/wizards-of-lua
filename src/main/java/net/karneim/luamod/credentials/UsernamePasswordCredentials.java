package net.karneim.luamod.credentials;

public class UsernamePasswordCredentials implements Credentials {
  public final String username;
  public final String password;

  public UsernamePasswordCredentials(String username, String password) {
    this.username = username;
    this.password = password;
  }

}
