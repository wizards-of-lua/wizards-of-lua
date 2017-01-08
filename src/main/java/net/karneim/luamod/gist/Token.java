package net.karneim.luamod.gist;

public class Token implements AuthenticationMethod {
  private String authkey;

  public Token(String authkey) {
    super();
    this.authkey = authkey;
  }

  @Override
  public String getValue() {
    return "token " + authkey;
  }

}
