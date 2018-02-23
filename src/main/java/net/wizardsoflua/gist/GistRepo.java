package net.wizardsoflua.gist;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GistRepo {

  /**
   * Loads all Gist files from the given url.
   * 
   * @param gistUrl e.g. "https://gist.github.com/mkarneim/629797671d41de3b674424ee66b0459b" or just
   *        plainly "629797671d41de3b674424ee66b0459b", optionally followed by a slash and the
   *        revision ID.
   * @param accessToken the GitHub access token to use. When no access token is provided, the number
   *        of calls from a specific IP is limited to 60 per hour.
   * @throws RequestRateLimitExceededException
   * @throws IOException
   */
  public List<GistFile> getGistFiles(String gistUrl, @Nullable String accessToken)
      throws IOException, RequestRateLimitExceededException {
    checkNotNull(gistUrl, "gistUrl==null!");
    String gistId = parseGistId(gistUrl);
    URL url = new URL(String.format("https://api.github.com/gists/%s", gistId));
    String auth = accessToken == null ? null : "token " + accessToken;
    InputStreamReader in = openInputStream(url, auth);
    Gist gist = read(in);
    return gist.getFiles();
  }

  /**
   * Returns the current {@link RateLimit} for REST calls to GitHub originating from this IP before
   * being denied.
   * 
   * @param accessToken the GitHub access token to use
   * @return the {@link RateLimit}
   * @throws IOException
   * @see https://developer.github.com/v3/rate_limit/
   */
  public RateLimit getRateLimitRemaining(@Nullable String accessToken) throws IOException {
    URL url = new URL("https://api.github.com/rate_limit");
    String auth = accessToken == null ? null : "token " + accessToken;
    HttpURLConnection connection = _openConnection(url, auth);
    connection.setRequestMethod("GET");
    connection.setDoOutput(true);
    return getRateLimit(connection);
  }

  private String parseGistId(String gistUrl) {
    Pattern p;
    if (gistUrl.startsWith("https")) {
      p = Pattern.compile("^https://gist.github.com/[^/ ]+/([a-f0-9/]+)$");
    } else {
      p = Pattern.compile("^([a-f0-9/]+)$");
    }
    Matcher m = p.matcher(gistUrl);
    if (m.find()) {
      String gistId = m.group(1);
      return gistId;
    } else {
      throw new IllegalArgumentException("No Gist ID found in " + gistUrl);
    }
  }

  private HttpURLConnection openConnection(URL url, @Nullable String authorization)
      throws IOException, RequestRateLimitExceededException {
    HttpURLConnection connection = _openConnection(url, authorization);
    connection.setRequestMethod("GET");
    connection.setDoOutput(true);
    int respCode = connection.getResponseCode();
    if (respCode == 403) {
      // see https://developer.github.com/v3/#rate-limiting
      RateLimit rateLimit = getRateLimit(connection);
      if (rateLimit.remaining <= 0) {
        throw new RequestRateLimitExceededException(url, rateLimit, authorization != null);
      }
    }
    return connection;
  }

  private HttpURLConnection _openConnection(URL url, String authorization) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    if (authorization != null) {
      connection.setRequestProperty("Authorization", authorization);
    }
    return connection;
  }

  private InputStreamReader openInputStream(URL url, @Nullable String authorization)
      throws IOException, RequestRateLimitExceededException {
    HttpURLConnection connection = openConnection(url, authorization);
    try {
      return new InputStreamReader((InputStream) connection.getInputStream());
    } catch (FileNotFoundException e) {
      throw new IOException("Can't load Gist from " + url, e);
    }
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
      // String raw_url = fileObj.get("raw_url").getAsString();
      GistFile gistFile = new GistFile(filename, content);
      result.add(gistFile);
    }
    return result;
  }

  private RateLimit getRateLimit(HttpURLConnection connection) {
    String limitStr = connection.getHeaderField("X-RateLimit-Limit");
    String remainingStr = connection.getHeaderField("X-RateLimit-Remaining");
    String resetStr = connection.getHeaderField("X-RateLimit-Reset");
    int limit = Integer.parseInt(limitStr);
    int remaining = Integer.parseInt(remainingStr);
    long reset = Long.parseLong(resetStr);
    return new RateLimit(limit, remaining, reset);
  }

}
