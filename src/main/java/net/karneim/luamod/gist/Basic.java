package net.karneim.luamod.gist;

public class Basic implements AuthenticationMethod {
  private String username;
  private String password;

  public Basic(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public String getValue() {
    sun.misc.BASE64Encoder x = new sun.misc.BASE64Encoder();
    String encoding = x.encode((username + ":" + password).getBytes());
    return "Basic " + encoding;
  }

}
