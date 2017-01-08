package net.karneim.luamod.credentials;


import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import net.karneim.luamod.config.ModConfiguration;

public class CredentialsStore {
  private ModConfiguration configuration;
  private final Map<Key, Credentials> map = new HashMap<CredentialsStore.Key, Credentials>();

  public CredentialsStore(ModConfiguration configuration) {
    this.configuration = configuration;
  }

  public void storeCredentials(Realm realm, Credentials credentials) {
    storeCredentials(realm, null, credentials);
  }

  public void storeCredentials(Realm realm, @Nullable String userId, Credentials credentials) {
    checkNotNull(realm, "realm==null!");
    checkNotNull(credentials, "credentials==null!");
    if (userId == null) {
      userId = "default";
    }
    map.put(new Key(realm, userId), credentials);
    // TODO store encrypted pw only!
    if (credentials instanceof UsernamePasswordCredentials) {
      UsernamePasswordCredentials upw = (UsernamePasswordCredentials) credentials;
      configuration.setString("credentials", realm + ".username." + userId, upw.username);
      configuration.setString("credentials", realm + ".password." + userId, upw.password);
      configuration.setString("credentials", realm + ".token." + userId, null);
    }
    if (credentials instanceof AccessTokenCredentials) {
      AccessTokenCredentials atok = (AccessTokenCredentials) credentials;
      configuration.setString("credentials", realm + ".username." + userId, null);
      configuration.setString("credentials", realm + ".password." + userId, null);
      configuration.setString("credentials", realm + ".token." + userId, atok.token);
    }
    configuration.save();
  }

  public @Nullable Credentials retrieveCredentials(Realm realm) {
    return retrieveCredentials(realm, null);
  }

  public @Nullable Credentials retrieveCredentials(Realm realm, @Nullable String userId) {
    checkNotNull(realm, "realm==null!");
    if (userId == null) {
      userId = "default";
    }
    Credentials result = map.get(new Key(realm, userId));
    if (result == null) {
      String token = configuration.getStringOrNull("credentials", realm + ".token." + userId);
      if (token != null) {
        result = new AccessTokenCredentials(token);
      } else {
        String username =
            configuration.getStringOrNull("credentials", realm + ".username." + userId);
        String password =
            configuration.getStringOrNull("credentials", realm + ".password." + userId);
        // TODO decrypt pw
        if (username != null && password != null) {
          result = new UsernamePasswordCredentials(username, password);
        }
      }
    }
    if (result != null) {
      storeCredentials(realm, userId, result);
    }
    if (result == null && !"default".equals(userId)) {
      return retrieveCredentials(realm);
    }
    return result;
  }

  private static class Key {
    Realm realm;
    String username;

    public Key(Realm realm, String username) {
      this.realm = realm;
      this.username = username;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((realm == null) ? 0 : realm.hashCode());
      result = prime * result + ((username == null) ? 0 : username.hashCode());
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
      Key other = (Key) obj;
      if (realm != other.realm)
        return false;
      if (username == null) {
        if (other.username != null)
          return false;
      } else if (!username.equals(other.username))
        return false;
      return true;
    }

  }
}
