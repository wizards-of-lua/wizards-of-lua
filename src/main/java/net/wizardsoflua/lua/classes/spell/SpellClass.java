package net.wizardsoflua.lua.classes.spell;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.auto.service.AutoService;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.lib.StringLib;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaFunctionDoc;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.block.LiveWolBlock;
import net.wizardsoflua.block.WolBlock;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.api.resource.LuaTypes;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.function.NamedFunctionAnyArg;
import net.wizardsoflua.lua.view.ViewFactory;
import net.wizardsoflua.spell.SpellEntity;

@AutoService(LuaConverter.class)
@LuaClassAttributes(name = SpellClass.NAME, superClass = VirtualEntityClass.class)
@GenerateLuaClassTable(instance = SpellClass.Instance.class)
@GenerateLuaDoc(subtitle = "Aspects of an Active Spell")
public final class SpellClass extends BasicLuaClass<SpellEntity, SpellClass.Instance<SpellEntity>> {
  public static final String NAME = "Spell";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new SpellClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<SpellEntity>> toLuaInstance(SpellEntity javaInstance) {
    return new SpellClassInstanceTable<>(new Instance<>(javaInstance, injector), getTable(),
        converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends SpellEntity> extends VirtualEntityClass.Instance<D> {
    @Inject
    private ViewFactory viewFactory;

    public Instance(D delegate, Injector injector) {
      super(delegate, injector);
    }

    @LuaProperty
    public WolBlock getBlock() {
      BlockPos pos = new BlockPos(delegate.getPositionVector());
      World world = delegate.getEntityWorld();
      return new LiveWolBlock(pos, world);
    }

    @LuaProperty
    public void setBlock(WolBlock block) {
      World world = delegate.getEntityWorld();
      BlockPos pos = new BlockPos(delegate.getPositionVector());
      block.setBlock(world, pos);
    }

    @LuaProperty
    public Table getData() {
      return delegate.getData(viewFactory);
    }

    @LuaProperty
    public @Nullable Entity getOwner() {
      return delegate.getOwnerEntity();
    }

    @LuaProperty
    public long getSid() {
      return delegate.getSid();
    }

    @LuaProperty
    public void setVisible(boolean visible) {
      delegate.setVisible(visible);
    }

    @LuaProperty
    public boolean isVisible() {
      return delegate.isVisible();
    }

    @LuaFunction(name = ExecuteFunction.NAME)
    @LuaFunctionDoc(returnType = LuaTypes.NUMBER, args = {"command", "..."})
    static class ExecuteFunction extends NamedFunctionAnyArg {
      public static final String NAME = "execute";
      private final SpellClass luaClass;

      public ExecuteFunction(SpellClass luaClass) {
        this.luaClass = requireNonNull(luaClass, "luaClass == null!");
      }

      @Override
      public String getName() {
        return NAME;
      }

      @Override
      public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
        Object arg1 = getArg(1, args);
        Instance<?> self = luaClass.converters.toJava(Instance.class, arg1, 1, "self", NAME);
        String command;
        if (args.length > 2) { // format the command
          Object[] format = Arrays.copyOfRange(args, 1, args.length);
          StringLib.format().invoke(context, format);
          command = String.valueOf(context.getReturnBuffer().get0());
        } else {
          Object arg2 = getArg(2, args);
          command = luaClass.converters.toJava(String.class, arg2, 2, "command", getName());
        }
        int result = self.execute(command);
        context.getReturnBuffer().setTo(result);
      }

      private Object getArg(int i, Object[] args) {
        return args.length < i ? null : args[i - 1];
      }
    }

    public int execute(String command) {
      World world = delegate.getEntityWorld();
      return world.getMinecraftServer().getCommandManager().executeCommand(delegate, command);
    }
  }
}
