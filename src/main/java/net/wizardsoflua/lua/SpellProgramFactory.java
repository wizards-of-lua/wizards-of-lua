package net.wizardsoflua.lua;

import static java.lang.String.format;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.time.Clock;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.inject.Inject;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.wizardsoflua.GameProfiles;
import net.wizardsoflua.ServerScoped;
import net.wizardsoflua.config.GeneralConfig;
import net.wizardsoflua.config.ScriptGatewayConfig;
import net.wizardsoflua.config.WolConfig;
import net.wizardsoflua.extension.api.inject.PostConstruct;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.api.resource.RealTime;
import net.wizardsoflua.filesystem.WolServerFileSystem;
import net.wizardsoflua.lua.extension.InjectionScope;
import net.wizardsoflua.lua.module.print.PrintRedirector.PrintReceiver;
import net.wizardsoflua.lua.module.searcher.LuaFunctionBinaryCache;
import net.wizardsoflua.profiles.Profiles;
import net.wizardsoflua.spell.SpellRegistry;

@ServerScoped
public class SpellProgramFactory {
  @Resource
  private InjectionScope scope;
  @Resource
  private RealTime time;
  @Inject
  private SpellRegistry spellRegistry;
  @Inject
  private WolConfig config;
  @Inject
  private GameProfiles gameProfiles;
  @Inject
  private Profiles profiles;
  @Inject
  private WolServerFileSystem fileSystem;
  @Inject
  private LuaFunctionBinaryCache luaFunctionCache;

  private SpellProgram.Context context;

  @PostConstruct
  private void postConstruct() {
    GeneralConfig generalConfig = config.getGeneralConfig();
    ScriptGatewayConfig scriptGatewayConfig = config.getScriptGatewayConfig();
    context = new SpellProgram.Context() {
      @Override
      public Clock getClock() {
        return time.getClock();
      }

      @Override
      public long getLuaTicksLimit() {
        return generalConfig.getLuaTicksLimit();
      }

      @Override
      public long getEventListenerLuaTicksLimit() {
        return generalConfig.getEventListenerLuaTicksLimit();
      }

      @Override
      public @Nullable String getLuaPathElementOfPlayer(String nameOrUuid) {
        UUID uuid = getUUID(nameOrUuid);
        return config.getOrCreateWizardConfig(uuid).getLibDirPathElement();
      }

      private UUID getUUID(String nameOrUuid) {
        try {
          return UUID.fromString(nameOrUuid);
        } catch (IllegalArgumentException e) {
          GameProfile profile = gameProfiles.getGameProfileByName(nameOrUuid);
          if (profile != null) {
            return profile.getId();
          } else {
            throw new IllegalArgumentException(
                format("Player not found with name '%s'", nameOrUuid));
          }
        }
      }

      @Override
      public LuaFunctionBinaryCache getLuaFunctionBinaryCache() {
        return luaFunctionCache;
      }

      @Override
      public boolean isScriptGatewayEnabled() {
        return scriptGatewayConfig.isEnabled();
      }

      @Override
      public Path getScriptDir() {
        return scriptGatewayConfig.getDir();
      }

      @Override
      public long getScriptTimeoutMillis() {
        return scriptGatewayConfig.getTimeoutMillis();
      }

      @Override
      public Profiles getProfiles() {
        return profiles;
      }

      @Override
      public SpellRegistry getSpellRegistry() {
        return spellRegistry;
      }

      @Override
      public InjectionScope getParentScope() {
        return scope;
      }

      @Override
      public FileSystem getWorldFileSystem() {
        return fileSystem;
      }
    };
  }

  public SpellProgram create(World world, @Nullable Entity owner, PrintReceiver printReceiver,
      String code, @Nullable String[] arguments) {
    String defaultLuaPath = getDefaultLuaPath(owner);
    return new SpellProgram(owner, code, arguments, defaultLuaPath, world, printReceiver, context);
  }

  private String getDefaultLuaPath(Entity owner) {
    if (owner instanceof EntityPlayer) {
      return config.getSharedLuaPath() + ";"
          + getLuaPathElementOfPlayer(owner.getCachedUniqueIdString());
    }
    return config.getSharedLuaPath();
  }

  public @Nullable String getLuaPathElementOfPlayer(String nameOrUuid) {
    UUID uuid = getUUID(nameOrUuid);
    return config.getOrCreateWizardConfig(uuid).getLibDirPathElement();
  }

  private UUID getUUID(String nameOrUuid) {
    try {
      return UUID.fromString(nameOrUuid);
    } catch (IllegalArgumentException e) {
      GameProfile profile = gameProfiles.getGameProfileByName(nameOrUuid);
      if (profile != null) {
        return profile.getId();
      } else {
        throw new IllegalArgumentException(format("Player not found with name '%s'", nameOrUuid));
      }
    }
  }
}
