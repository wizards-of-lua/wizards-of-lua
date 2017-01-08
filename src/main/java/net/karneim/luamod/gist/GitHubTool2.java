package net.karneim.luamod.gist;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GitHubTool2 {

  private @Nullable AuthenticationMethod authMethod;

  public GitHubTool2() {}

  public void setAuthMethod(AuthenticationMethod authMethod) {
    this.authMethod = authMethod;
  }

  public void checkLogin() throws IOException {
    open(new URL("https://api.github.com/gists"));
  }

  public Gist load(String id) throws IOException {
    URL url = new URL(String.format("https://api.github.com/gists/%s", id));
    return read(open(url));
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
      GistFile gistFile = new GistFile(filename, content);
      result.add(gistFile);
    }
    return result;
  }

}
