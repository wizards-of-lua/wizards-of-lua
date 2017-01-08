package net.karneim.luamod.gist;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GitHubTool {

  public static @Nullable GistFileRef parseGistRef(String gistRefStr) {
    return GistFileRef.parseGistRef(gistRefStr);
  }

  public static @Nullable String parseIdOld(String gistUrl) {
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

  public static String getGist(GistFileRef ref) throws IOException {
    return getGist(ref, null, null);
  }

  public static String getGist(GistFileRef ref, @Nullable String username,
      @Nullable String password) throws IOException {
    GitHubTool tool = new GitHubTool();
    tool.setAuthMethod(new Basic(username, password));
    GistFile gistFile = tool.loadFile(ref);
    return gistFile.getContent();
  }

  public static String getGist(GistFileRef ref, @Nullable String authkey) throws IOException {
    GitHubTool tool = new GitHubTool();
    tool.setAuthMethod(new Token(authkey));
    GistFile gistFile = tool.loadFile(ref);
    if (gistFile != null) {
      return gistFile.getContent();
    } else {
      throw new IllegalArgumentException("Unknown gist file: " + ref.toString());
    }
  }

  public static void checklogin(String username, String password) throws IOException {
    GitHubTool tool = new GitHubTool();
    tool.setAuthMethod(new Basic(username, password));
    tool.checkLogin();
  }

  public static void checkToken(String authkey) throws IOException {
    GitHubTool tool = new GitHubTool();
    tool.setAuthMethod(new Token(authkey));
    tool.checkLogin();
  }



  private @Nullable AuthenticationMethod authMethod;

  public GitHubTool() {}

  public void setAuthMethod(AuthenticationMethod authMethod) {
    this.authMethod = authMethod;
  }

  public void checkLogin() throws IOException {
    open(new URL("https://api.github.com/gists"));
  }

  public Gist loadGist(String gistId) throws IOException {
    URL url = new URL(String.format("https://api.github.com/gists/%s", gistId));
    return read(open(url));
  }

  public @Nullable GistFile loadFile(GistFileRef ref) throws IOException {
    URL url = new URL(String.format("https://api.github.com/gists/%s", ref.getGistId()));
    Gist gist = read(open(url));
    return gist.getFile(ref.getNameWithExtension());
  }

  private InputStreamReader open(URL url) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    if (authMethod != null) {
      connection.setRequestProperty("Authorization", authMethod.getValue());
    }
    connection.setRequestMethod("GET");
    connection.setDoOutput(true);
    return new InputStreamReader((InputStream) connection.getInputStream());
  }

  private Gist read(InputStreamReader reader) throws IOException {
    JsonParser parser = new JsonParser();
    JsonObject root = parser.parse(reader).getAsJsonObject();
    Gist result = new Gist();
    JsonObject files = root.get("files").getAsJsonObject();
    Set<Entry<String, JsonElement>> entries = files.entrySet();
    for (Entry<String, JsonElement> entry : entries) {
      JsonObject fileObj = entry.getValue().getAsJsonObject();
      String filename = fileObj.get("filename").getAsString();
      String content = fileObj.get("content").getAsString();
      String raw_url = fileObj.get("raw_url").getAsString();
      GistFile gistFile = new GistFile(filename, content);
      result.add(gistFile);
    }
    return result;
  }

}
