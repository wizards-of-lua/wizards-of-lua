package net.wizardsoflua.lua;

import static com.google.common.base.Preconditions.checkNotNull;

import java.time.Clock;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;
import net.sandius.rembulan.runtime.SchedulingContext;
import net.sandius.rembulan.runtime.SchedulingContextFactory;
import net.wizardsoflua.lua.module.runtime.Runtime;

public class SpellProgramFactory {
  public interface Context {
    Clock getClock();

    int getLuaTicksLimit();
  }

  private final Context context;

  public SpellProgramFactory(Context context) {
    this.context = checkNotNull(context, "context==null!");
  }

  public SpellProgram create(World world, ICommandSender owner, String code) {
    SpellProgram.Context spellProgramContext = createSpellProgramContext(world);
    return new SpellProgram(owner, code, spellProgramContext);
  }

  private SpellProgram.Context createSpellProgramContext(World world) {
    Runtime.Context runtimeContext = new Runtime.Context() {
      @Override
      public Clock getClock() {
        return context.getClock();
      }
    };
    int luaTicksLimit = context.getLuaTicksLimit();
    Runtime runtime = new Runtime(world, luaTicksLimit, runtimeContext);
    return new SpellProgram.Context() {

      @Override
      public SchedulingContextFactory getSchedulingContextFactory() {
        return new SchedulingContextFactory() {

          @Override
          public SchedulingContext newInstance() {
            runtime.resetAllowance();
            return new SchedulingContext() {

              @Override
              public boolean shouldPause() {
                return runtime.shouldPause();
              }

              @Override
              public void registerTicks(int ticks) {
                runtime.consumeLuaTicks(ticks);
              }
            };
          }
        };
      }

      @Override
      public Runtime getRuntime() {
        return runtime;
      }
    };
  }
}
