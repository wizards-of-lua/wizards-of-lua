package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.time.Clock;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.wizardsoflua.lua.classes.LuaClasses;
import net.wizardsoflua.lua.dependency.ModuleDependencies;
import net.wizardsoflua.lua.dependency.ModuleDependency;
import net.wizardsoflua.lua.module.searcher.LuaFunctionBinaryCache;
import net.wizardsoflua.lua.module.time.Time;
import net.wizardsoflua.profiles.Profiles;

public class SpellProgramFactory {

  public interface Context {
    Clock getClock();

    int getLuaTicksLimit();

    Profiles getProfiles();

    String getSharedLuaPath();

    String getLuaPathElementOfPlayer(String nameOrUuid);

    LuaClasses getLuaClasses();

    LuaFunctionBinaryCache getLuaFunctionBinaryCache();

  }

  private final Context context;

  public SpellProgramFactory(Context context) {
    this.context = checkNotNull(context, "context==null!");
  }

  public SpellProgram create(World world, ICommandSender owner, String code) {
    ModuleDependencies dependencies = createDependencies(owner);
    String defaultLuaPath = getDefaultLuaPath(owner);
    Time time = createTime(world);
    SpellProgram.Context context = createSpellProgramContext(world, owner);
    return new SpellProgram(owner, code, dependencies, defaultLuaPath, time, context);
  }

  private String getDefaultLuaPath(ICommandSender owner) {
    Entity entity = owner.getCommandSenderEntity();
    if (entity instanceof EntityPlayer) {
      return context.getSharedLuaPath() + ";"
          + context.getLuaPathElementOfPlayer(entity.getCachedUniqueIdString());
    }
    return context.getSharedLuaPath();
  }

  private Time createTime(World world) {
    Time.Context timeContext = new Time.Context() {
      @Override
      public Clock getClock() {
        return context.getClock();
      }
    };
    int luaTicksLimit = context.getLuaTicksLimit();
    return new Time(world, luaTicksLimit, timeContext);
  }

  private SpellProgram.Context createSpellProgramContext(World world, ICommandSender owner) {
    checkNotNull(world, "world==null!");

    return new SpellProgram.Context() {
      @Override
      public String getLuaPathElementOfPlayer(String nameOrUuid) {
        return context.getLuaPathElementOfPlayer(nameOrUuid);
      }

      @Override
      public LuaClasses getLuaClasses() {
        return context.getLuaClasses();
      }

      @Override
      public LuaFunctionBinaryCache getLuaFunctionBinaryCache() {
        return context.getLuaFunctionBinaryCache();
      }
    };
  }

  private ModuleDependencies createDependencies(ICommandSender owner) {
    ModuleDependencies result = new ModuleDependencies();
    result.add(new ModuleDependency("wol.Globals"));
    result.add(new ModuleDependency("wol.inspect"));
    result.add(new ModuleDependency("wol.Check"));
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
    Entity entity = owner.getCommandSenderEntity();
    if (entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) entity;
      String playerProfile = context.getProfiles().getProfile(player);
      if (playerProfile != null) {
        result.add(new ModuleDependency(playerProfile));
      }
    }
    return result;
  }
}
