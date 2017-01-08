package net.karneim.luamod.gist;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class GitHubTool {

  public static @Nullable String parseId(String gistUrl) {
    Pattern p = Pattern.compile("(http|https)://gist\\.github\\.com/[^/]+/([a-f0-9/]+)$");
    Matcher m = p.matcher(gistUrl);
    if (m.find()) {
      String result = m.group(2);
      return result;
    }
    return null;
  }

  public static @Nullable String parseUsername(String gistUrl) {
    Pattern p = Pattern.compile("(http|https)://gist\\.github\\.com/([^/]+)/[a-f0-9/]+$");
    Matcher m = p.matcher(gistUrl);
    if (m.find()) {
      String result = m.group(2);
      return result;
    }
    return null;
  }

  public static String getGist(String id) throws IOException {
    return getGist(id, null, null);
  }

  public static String getGist(String id, @Nullable String username, @Nullable String password)
      throws IOException {
    GitHubTool2 tool = new GitHubTool2();
    tool.setAuthMethod(new Basic(username, password));
    Gist gist = tool.load(id);
    return gist.getFiles().get(0).getContent();
  }

  public static String getGist(String id, @Nullable String authkey) throws IOException {
    GitHubTool2 tool = new GitHubTool2();
    tool.setAuthMethod(new Token(authkey));
    Gist gist = tool.load(id);
    return gist.getFiles().get(0).getContent();
  }

  public static void checklogin(String username, String password) throws IOException {
    GitHubTool2 tool = new GitHubTool2();
    tool.setAuthMethod(new Basic(username, password));
    tool.checkLogin();
  }

  public static void checkToken(String authkey) throws IOException {
    GitHubTool2 tool = new GitHubTool2();
    tool.setAuthMethod(new Token(authkey));
    tool.checkLogin();
  }
}
