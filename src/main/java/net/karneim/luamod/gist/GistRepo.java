package net.karneim.luamod.gist;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import com.google.common.base.Preconditions;

import net.karneim.luamod.cache.FileCache;
import net.karneim.luamod.credentials.AccessTokenCredentials;
import net.karneim.luamod.credentials.Credentials;
import net.karneim.luamod.credentials.UsernamePasswordCredentials;

public class GistRepo {

  private final FileCache fileCache;

  public GistRepo(FileCache fileCache) {
    this.fileCache = checkNotNull(fileCache);
  }

  public String load(Credentials credentials, GistFileRef ref) throws IOException {
    Preconditions.checkNotNull(ref, "ref==null!");
    String content = fileCache.load(ref);
    if (content == null) {
      System.out.println("Loading " + ref);
      if (credentials == null) {
        content = GitHubTool.getGist(ref);
      } else if (credentials instanceof UsernamePasswordCredentials) {
        UsernamePasswordCredentials upw = (UsernamePasswordCredentials) credentials;
        content = GitHubTool.getGist(ref, upw.username, upw.password);
      } else if (credentials instanceof AccessTokenCredentials) {
        AccessTokenCredentials atk = (AccessTokenCredentials) credentials;
        content = GitHubTool.getGist(ref, atk.token);
      } else {
        throw new IllegalStateException(
            String.format("Unknown credentials type %s", credentials.getClass().getSimpleName()));
      }
      fileCache.save(ref, content);
    }
    return content;
  }


}
