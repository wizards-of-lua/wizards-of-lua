package net.wizardsoflua.lua;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;
import net.sandius.rembulan.runtime.SchedulingContext;
import net.sandius.rembulan.runtime.SchedulingContextFactory;
import net.wizardsoflua.lua.runtime.Runtime;

public class SpellProgramFactory {

  private int luaTicksLimit = 10000;

  public SpellProgramFactory() {}

  public int getLuaTicksLimit() {
    return luaTicksLimit;
  }

  public void setLuaTicksLimit(int luaTicksLimit) {
    this.luaTicksLimit = luaTicksLimit;
  }

  public SpellProgram create(World world, ICommandSender owner, String code) {
    SpellProgramContext spellProgramContext = createSpellProgramContext(world);
    return new SpellProgram(owner, code, spellProgramContext);
  }

  private SpellProgramContext createSpellProgramContext(World world) {
    Runtime runtime = new Runtime(world, luaTicksLimit);
    return new SpellProgramContext() {

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
