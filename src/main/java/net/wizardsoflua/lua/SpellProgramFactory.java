package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.time.Clock;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.sandius.rembulan.runtime.SchedulingContext;
import net.sandius.rembulan.runtime.SchedulingContextFactory;
import net.wizardsoflua.lua.dependency.ModuleDependencies;
import net.wizardsoflua.lua.dependency.ModuleDependency;
import net.wizardsoflua.lua.module.time.Time;
import net.wizardsoflua.profiles.Profiles;

public class SpellProgramFactory {

  public interface Context {
    Clock getClock();

    int getLuaTicksLimit();

    Profiles getProfiles();

    String getSharedLuaPath();

    String getLuaPathElementOfPlayer(String nameOrUuid);

  }

  private final Context context;

  public SpellProgramFactory(Context context) {
    this.context = checkNotNull(context, "context==null!");
  }

  public SpellProgram create(World world, ICommandSender owner, String code) {
    ModuleDependencies dependencies = createDependencies(owner);
    String defaultLuaPath = getDefaultLuaPath(owner);
    SpellProgram.Context context = createSpellProgramContext(world);
    return new SpellProgram(code, dependencies, owner, defaultLuaPath, context);
  }

  private String getDefaultLuaPath(ICommandSender owner) {
    Entity entity = owner.getCommandSenderEntity();
    if (entity instanceof EntityPlayer) {
      return context.getSharedLuaPath() + ";"
          + context.getLuaPathElementOfPlayer(entity.getCachedUniqueIdString());
    }
    return context.getSharedLuaPath();
  }

  private SpellProgram.Context createSpellProgramContext(World world) {
    checkNotNull(world, "world==null!");
    Time.Context timeContext = new Time.Context() {
      @Override
      public Clock getClock() {
        return context.getClock();
      }
    };
    int luaTicksLimit = context.getLuaTicksLimit();
    Time time = new Time(world, luaTicksLimit, timeContext);

    return new SpellProgram.Context() {

      @Override
      public SchedulingContextFactory getSchedulingContextFactory() {
        return new SchedulingContextFactory() {

          @Override
          public SchedulingContext newInstance() {
            time.resetAllowance();
            return new SchedulingContext() {

              @Override
              public boolean shouldPause() {
                return time.shouldPause();
              }

              @Override
              public void registerTicks(int ticks) {
                time.consumeLuaTicks(ticks);
              }
            };
          }
        };
      }

      @Override
      public Time getTime() {
        return time;
      }

      @Override
      public String getLuaPathElementOfPlayer(String nameOrUuid) {
        return context.getLuaPathElementOfPlayer(nameOrUuid);
      }
    };
  }

  private ModuleDependencies createDependencies(ICommandSender owner) {
    ModuleDependencies result = new ModuleDependencies();
    result.add(new ModuleDependency("net.wizardsoflua.lua.modules.Globals"));
    result.add(new ModuleDependency("net.wizardsoflua.lua.modules.inspect"));
    result.add(new ModuleDependency("net.wizardsoflua.lua.modules.Check"));
    result.add(new ModuleDependency("net.wizardsoflua.lua.modules.Vec3"));
    result.add(new ModuleDependency("net.wizardsoflua.lua.modules.Material"));
    result.add(new ModuleDependency("net.wizardsoflua.lua.modules.Block"));
    result.add(new ModuleDependency("net.wizardsoflua.lua.modules.Entity"));
    result.add(new ModuleDependency("net.wizardsoflua.lua.modules.Spell"));
    result.add(new ModuleDependency("net.wizardsoflua.lua.modules.Player"));

    Entity entity = owner.getCommandSenderEntity();
    if (entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) entity;
      String module = context.getProfiles().getProfile(player);
      if (module != null) {
        result.add(new ModuleDependency(module));
      }
    }
    return result;
  }
}
