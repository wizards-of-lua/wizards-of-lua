package net.karneim.luamod.gist;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class GistFileRef {

  public static @Nullable GistFileRef parseGistRef(@Nullable String gistRefStr) {
    if (gistRefStr == null) {
      return null;
    }
    Pattern p = Pattern.compile("^gist\\.([a-f0-9/]+)\\.([^ ]+)$");
    Matcher m = p.matcher(gistRefStr);
    if (m.find()) {
      String gistId = m.group(1);
      String name = m.group(2);;
      if (name.endsWith(".lua")) {
        throw new IllegalArgumentException(
            "Gist reference must not end with '.lua', but got: '" + gistRefStr + "'");
      }
      GistFileRef result = new GistFileRef(gistId, name);
      return result;
    }
    return null;
  }

  private final String gistId;
  private final String name;

  public GistFileRef(String gistId, String name) {
    this.gistId = gistId;
    this.name = name;
  }

  public String getGistId() {
    return gistId;
  }

  public String getName() {
    return name;
  }
  
  public String getNameWithExtension() {
    return name + getExtension();
  }

  public String getExtension() {
    return ".lua";
  }

  @Override
  public String toString() {
    return "gist." + gistId + "." + name;
  }

  public String asFilename() {
    return "gist" + File.separator + gistId + File.separator + name + getExtension();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((gistId == null) ? 0 : gistId.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GistFileRef other = (GistFileRef) obj;
    if (gistId == null) {
      if (other.gistId != null)
        return false;
    } else if (!gistId.equals(other.gistId))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

}
