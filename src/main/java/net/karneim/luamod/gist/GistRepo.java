package net.karneim.luamod.gist;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URL;

import net.karneim.luamod.cache.FileCache;
import net.karneim.luamod.credentials.AccessTokenCredentials;
import net.karneim.luamod.credentials.Credentials;
import net.karneim.luamod.credentials.UsernamePasswordCredentials;

public class GistRepo {

  private final FileCache fileCache;

  public GistRepo(FileCache fileCache) {
    this.fileCache = checkNotNull(fileCache);
  }

  public String load(Credentials credentials, URL gistUrl) throws IOException {
    String id = parseId(gistUrl.toString());
    if (id == null) {
      throw new IllegalArgumentException(String.format("Can't parse Gist ID in %s", gistUrl));
    }
    String content = fileCache.load(id);
    if (content == null) {
      System.out.println("Loading " + gistUrl);
      if (credentials == null) {
        content = GitHubTool.getGist(id);
      } else if (credentials instanceof UsernamePasswordCredentials) {
        UsernamePasswordCredentials upw = (UsernamePasswordCredentials) credentials;
        content = GitHubTool.getGist(id, upw.username, upw.password);
      } else if (credentials instanceof AccessTokenCredentials) {
        AccessTokenCredentials atk = (AccessTokenCredentials) credentials;
        content = GitHubTool.getGist(id, atk.token);
      } else {
        throw new IllegalStateException(
            String.format("Unknown credentials type %s", credentials.getClass().getSimpleName()));
      }
      fileCache.save(id, content);
    }
    return content;
  }

  public String parseId(String gistUrlStr) {
    return GitHubTool.parseId(gistUrlStr);
  }

}
