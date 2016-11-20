package net.karneim.luamod.gist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
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
    URL url = new URL(String.format("https://api.github.com/gists/%s", id));
    return loadGist(url, username, password);
  }

  public static String getGist(String id, @Nullable String authkey) throws IOException {
    URL url = new URL(String.format("https://api.github.com/gists/%s", id));
    return loadGist(url, authkey);
  }

  public static void checklogin(String username, String password) throws IOException {
    URL url = new URL("https://api.github.com/gists");
    load(url, username, password);
  }

  public static void checkToken(String token) throws IOException {
    URL url = new URL("https://api.github.com/gists");
    load(url, token);
  }

  private static @Nullable String loadGist(URL gistUrl, @Nullable String username,
      @Nullable String password) throws IOException, ProtocolException, MalformedURLException {
    String gistContent = null;
    String stringContent = load(gistUrl, username, password);
    URL rawUrl = parseRawUrl(stringContent);
    if (rawUrl != null) {
      gistContent = load(rawUrl, username, password);
    }
    return gistContent;
  }

  private static @Nullable String loadGist(URL gistUrl, @Nullable String authkey)
      throws IOException, ProtocolException, MalformedURLException {
    String gistContent = null;
    String stringContent = load(gistUrl, authkey);
    URL rawUrl = parseRawUrl(stringContent);
    if (rawUrl != null) {
      gistContent = load(rawUrl, authkey);
    }
    return gistContent;
  }

  private static String load(URL url, @Nullable String authkey)
      throws IOException, ProtocolException {
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    if (authkey != null) {
      connection.setRequestProperty("Authorization", "token " + authkey);
    }
    connection.setRequestMethod("GET");
    connection.setDoOutput(true);

    StringBuilder contentBuilder = new StringBuilder();
    InputStream content = (InputStream) connection.getInputStream();
    BufferedReader in = new BufferedReader(new InputStreamReader(content));
    String line;
    while ((line = in.readLine()) != null) {
      contentBuilder.append(line).append("\n");
    }
    return contentBuilder.toString();
  }

  private static String load(URL url, @Nullable String username, @Nullable String password)
      throws IOException, ProtocolException {
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    if (username != null && password != null) {
      sun.misc.BASE64Encoder x = new sun.misc.BASE64Encoder();
      String encoding = x.encode((username + ":" + password).getBytes());
      connection.setRequestProperty("Authorization", "Basic " + encoding);
    }
    connection.setRequestMethod("GET");
    connection.setDoOutput(true);

    StringBuilder contentBuilder = new StringBuilder();
    InputStream content = (InputStream) connection.getInputStream();
    BufferedReader in = new BufferedReader(new InputStreamReader(content));
    String line;
    while ((line = in.readLine()) != null) {
      contentBuilder.append(line).append("\n");
    }
    return contentBuilder.toString();
  }

  private static @Nullable URL parseRawUrl(String gistContent) throws MalformedURLException {
    Pattern p = Pattern.compile("raw_url\":\"(https://[^\"]+)\"");
    Matcher m = p.matcher(gistContent);
    if (m.find()) {
      String result = m.group(1);
      return new URL(result);
    }
    return null;
  }

}
