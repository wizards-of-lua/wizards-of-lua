package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Logger;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.wizardsoflua.lua.dependency.ModuleDependencies;
import net.wizardsoflua.lua.dependency.ModuleDependency;
import net.wizardsoflua.lua.module.print.PrintRedirector.PrintReceiver;
import net.wizardsoflua.profiles.Profiles;

public class SpellProgramFactory {

  public interface Context extends SpellProgram.Context {
    Profiles getProfiles();

    String getSharedLuaPath();
  }

  private final Logger logger;
  private final Context context;

  public SpellProgramFactory(Logger logger, Context context) {
    this.logger = checkNotNull(logger, "logger==null!");
    this.context = checkNotNull(context, "context==null!");
  }

  public SpellProgram create(World world, CommandSource source, Entity owner,
      PrintReceiver spellLogger, String code, @Nullable String[] arguments) {
    ModuleDependencies dependencies = createDependencies(owner);
    String defaultLuaPath = getDefaultLuaPath(owner);
    return new SpellProgram(owner, code, arguments, dependencies, defaultLuaPath, world,
        spellLogger, context, logger);
  }

  private String getDefaultLuaPath(Entity owner) {
    if (owner instanceof EntityPlayer) {
      return context.getSharedLuaPath() + ";"
          + context.getLuaPathElementOfPlayer(owner.getCachedUniqueIdString());
    }
    return context.getSharedLuaPath();
  }

  private ModuleDependencies createDependencies(Entity owner) {
    ModuleDependencies result = new ModuleDependencies();
    result.add(new ModuleDependency("wol.Globals"));
    result.add(new ModuleDependency("wol.inspect"));
    result.add(new ModuleDependency("wol.Check"));
    result.add(new ModuleDependency("wol.Object"));
    result.add(new ModuleDependency("wol.Vec3"));
    result.add(new ModuleDependency("wol.Material"));
    result.add(new ModuleDependency("wol.Block"));
    result.add(new ModuleDependency("wol.Entity"));
    result.add(new ModuleDependency("wol.Spell"));
    result.add(new ModuleDependency("wol.Player"));

    String sharedProfile = context.getProfiles().getSharedProfile();
    if (sharedProfile != null) {
      result.add(new ModuleDependency(sharedProfile));
    }
    if (owner instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) owner;
      String playerProfile = context.getProfiles().getProfile(player);
      if (playerProfile != null) {
        result.add(new ModuleDependency(playerProfile));
      }
    }
    return result;
  }
}
