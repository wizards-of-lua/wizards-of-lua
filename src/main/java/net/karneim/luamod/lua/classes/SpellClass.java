package net.karneim.luamod.lua.classes;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.karneim.luamod.lua.util.LuaPreconditions.checkType;
import static net.karneim.luamod.lua.util.LuaPreconditions.checkTypeString;

import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.output.WriterOutputStream;

import com.google.common.collect.Lists;

import net.karneim.luamod.TabEncoder;
import net.karneim.luamod.cursor.EnumDirection;
import net.karneim.luamod.cursor.Selection;
import net.karneim.luamod.cursor.Spell;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.lib.BasicLib;
import net.sandius.rembulan.lib.StringLib;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.AbstractFunction3;
import net.sandius.rembulan.runtime.AbstractFunction4;
import net.sandius.rembulan.runtime.AbstractFunctionAnyArg;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

@LuaModule("Spell")
public class SpellClass extends DelegatingLuaClass<Spell> {
  public SpellClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder<? extends Spell> b, Spell delegate) {
    SpellWrapper d = new SpellWrapper(delegate);
    b.add("block", () -> repo.wrap(delegate.getBlockState()), d::setBlock);
    b.add("orientation", () -> repo.wrap(delegate.getOrientation()), d::setOrientation);
    b.addReadOnly("origin", () -> repo.wrap(delegate.getOrigin()));
    b.addReadOnly("owner", () -> repo.wrap(delegate.getOwner()));
    // b.add("rotation", () -> repo.wrap(getRepo(), delegate.getRotation()), d::setRotation);
    b.add("rotation", () -> delegate.getRotation(), d::setRotation);
    b.addReadOnly("surface", () -> repo.wrap(delegate.getSurface()));
    b.add("pos", () -> repo.wrap(delegate.getPosition()), d::setPosition);
  }

  private static class SpellWrapper {
    private final Spell delegate;

    public SpellWrapper(Spell delegate) {
      this.delegate = checkNotNull(delegate, "delegate == null!");
    }

    private void setPosition(Object arg) {
      Table vector = checkType(arg, Table.class);
      Number x = checkType(vector.rawget("x"), Number.class);
      Number y = checkType(vector.rawget("y"), Number.class);
      Number z = checkType(vector.rawget("z"), Number.class);
      Vec3d v = new Vec3d(x.doubleValue(), y.doubleValue(), z.doubleValue());
      delegate.setPosition(v);
    }

    private void setRotation(Object arg) {
      if (arg instanceof Number) {
        float angle = ((Number) arg).floatValue();
        delegate.setRotation(angle);
      } else {
        Rotation rot = Rotation.valueOf(String.valueOf(arg));
        if (rot != null) {
          delegate.setRotation(rot);
        } else {
          throw new IllegalArgumentException(
              String.format("Rotation value expected but got %s!", arg));
        }
      }
    }

    private void setOrientation(Object arg) {
      EnumFacing f = EnumFacing.byName(String.valueOf(arg));
      if (f != null) {
        delegate.setOrientation(f);
      } else {
        throw new IllegalArgumentException(String.format("Facing value expected but got %s!", arg));
      }
    }

    private void setBlock(Object arg) {
      if (arg == null) {
        throw new IllegalArgumentException(String.format("Block value expected but got nil!"));
      }
      try {
        Block block = null;
        if (arg instanceof DelegatingTable) {
          DelegatingTable<IBlockState> table = (DelegatingTable<IBlockState>) arg;
          String typename = String.valueOf(table.rawget("type"));
          if ("Block".equals(typename)) {
            IBlockState blockState = table.getDelegate();
            delegate.setBlockState(blockState);
            return;
          } else {
            throw new IllegalArgumentException(
                String.format("Block value expected but got %s!", arg));
          }
        } else if (arg instanceof Table) {
          Table table = (Table) arg;
          String typename = String.valueOf(table.rawget("type"));
          String name = String.valueOf(table.rawget("name"));
          if ("Block".equals(typename)) {
            block = delegate.getBlockByName(name);
          }
        } else {
          block = delegate.getBlockByName(String.valueOf(arg));
        }
        if (block != null) {
          delegate.setBlock(block);
        } else {
          throw new IllegalArgumentException(
              String.format("Block value expected but got %s!", arg));
        }
      } catch (NumberInvalidException e) {
        throw new IllegalArgumentException(
            String.format("Block value expected but received exception: %s!", e.getMessage()));
      }
    }
  }

  @Override
  protected void addFunctions(Table luaClass) {
    luaClass.rawset("move", new MoveFunction());
    luaClass.rawset("moveBy", new MoveByFunction());
    luaClass.rawset("rotate", new RotateFunction());
    luaClass.rawset("say", new SayFunction(getEnv()));
    luaClass.rawset("whisper", new WhisperFunction(getEnv()));
    luaClass.rawset("execute", new ExecuteFunction());
    luaClass.rawset("reset", new ResetFunction());
    luaClass.rawset("resetRotation", new ResetRotationFunction());
    luaClass.rawset("resetPosition", new ResetPositionFunction());
    luaClass.rawset("pushLocation", new PushLocationFunction());
    luaClass.rawset("popLocation", new PopLocationFunction());
    luaClass.rawset("cut", new CutFunction());
    luaClass.rawset("copy", new CopyFunction());
    luaClass.rawset("paste", new PasteFunction());
  }

  private class MoveFunction extends AbstractFunction3 {
    @Override
    public void invoke(ExecutionContext context, Object arg0, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      DelegatingTable<?> self = checkType(0, arg0, DelegatingTable.class);
      Spell delegate = checkType(0, self.getDelegate(), Spell.class);
      String direction = checkTypeString(1, arg1);
      int length = checkType(2, arg2, Number.class).intValue();

      EnumFacing x = EnumFacing.byName(direction);
      if (x != null) {
        delegate.move(x, length);
      } else {
        EnumDirection y = EnumDirection.byName(direction);
        if (y != null) {
          delegate.move(y, length);
        } else if ("SURFACE".equals(direction)) {
          delegate.move(delegate.getSurface(), length);
        } else {
          throw new IllegalArgumentException(
              String.format("Direction value expected but got %s!", arg1));
        }
      }
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class MoveByFunction extends AbstractFunction4 {
    @Override
    public void invoke(ExecutionContext context, Object arg0, Object arg1, Object arg2, Object arg3)
        throws ResolvedControlThrowable {
      DelegatingTable<?> self = checkType(0, arg0, DelegatingTable.class);
      Spell delegate = checkType(0, self.getDelegate(), Spell.class);
      int dx = checkType(1, arg1, Number.class).intValue();
      int dy = checkType(2, arg2, Number.class).intValue();
      int dz = checkType(3, arg3, Number.class).intValue();

      delegate.moveBy(dx, dy, dz);
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class RotateFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg0, Object arg1)
        throws ResolvedControlThrowable {
      DelegatingTable<?> self = checkType(0, arg0, DelegatingTable.class);
      Spell delegate = checkType(0, self.getDelegate(), Spell.class);

      if (arg1 instanceof Number) {
        float angle = ((Number) arg1).floatValue();
        delegate.rotate(angle);
      } else {
        Rotation rot = rotationByName(String.valueOf(arg1));
        if (rot != null) {
          delegate.rotate(rot);
        } else {
          throw new IllegalArgumentException(
              String.format("Rotation value expected but got %s!", arg1));
        }
      }
      context.getReturnBuffer().setTo();
    }

    private Rotation rotationByName(String name) {
      return Rotation.valueOf(name);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class SayFunction extends AbstractFunctionAnyArg {
    private final Table env;

    public SayFunction(Table env) {
      this.env = env;
    }

    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      Object arg0 = args[0];
      DelegatingTable<?> self = checkType(0, arg0, DelegatingTable.class);
      Spell delegate = checkType(0, self.getDelegate(), Spell.class);

      List<Object> values = Lists.newArrayList(args);
      values.remove(0);

      String text = format(context, env, values);
      delegate.say(text);
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class WhisperFunction extends AbstractFunctionAnyArg {
    private final Table env;

    public WhisperFunction(Table env) {
      this.env = env;
    }

    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      Object arg0 = args[0];
      Object arg1 = args[1];
      List<Object> values = Lists.newArrayList(args);
      values.remove(0);
      values.remove(0);

      DelegatingTable<?> self = checkType(0, arg0, DelegatingTable.class);
      Spell delegate = checkType(0, self.getDelegate(), Spell.class);

      String username = checkTypeString(1, arg1);

      String text = format(context, env, values);
      delegate.whisper(username, text);
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class ExecuteFunction extends AbstractFunctionAnyArg {
    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      Object arg0 = args[0];
      DelegatingTable<?> self = checkType(0, arg0, DelegatingTable.class);
      Spell delegate = checkType(0, self.getDelegate(), Spell.class);

      LuaFunction formatFunc = StringLib.format();
      Object[] argArray = new Object[args.length - 1];
      System.arraycopy(args, 1, argArray, 0, args.length - 1);
      formatFunc.invoke(context, argArray);
      String command = String.valueOf(context.getReturnBuffer().get(0));

      int result = delegate.execute(command);
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class ResetFunction extends AbstractFunction1 {
    @Override
    public void invoke(ExecutionContext context, Object arg0) throws ResolvedControlThrowable {
      DelegatingTable<?> self = checkType(0, arg0, DelegatingTable.class);
      Spell delegate = checkType(0, self.getDelegate(), Spell.class);

      delegate.reset();
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class ResetRotationFunction extends AbstractFunction1 {
    @Override
    public void invoke(ExecutionContext context, Object arg0) throws ResolvedControlThrowable {
      DelegatingTable<?> self = checkType(0, arg0, DelegatingTable.class);
      Spell delegate = checkType(0, self.getDelegate(), Spell.class);

      delegate.resetRotation();
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class ResetPositionFunction extends AbstractFunction1 {
    @Override
    public void invoke(ExecutionContext context, Object arg0) throws ResolvedControlThrowable {
      DelegatingTable<?> self = checkType(0, arg0, DelegatingTable.class);
      Spell delegate = checkType(0, self.getDelegate(), Spell.class);

      delegate.resetPosition();
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class PushLocationFunction extends AbstractFunction1 {
    @Override
    public void invoke(ExecutionContext context, Object arg0) throws ResolvedControlThrowable {
      DelegatingTable<?> self = checkType(0, arg0, DelegatingTable.class);
      Spell delegate = checkType(0, self.getDelegate(), Spell.class);

      delegate.pushLocation();
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class PopLocationFunction extends AbstractFunction1 {
    @Override
    public void invoke(ExecutionContext context, Object arg0) throws ResolvedControlThrowable {
      DelegatingTable<?> self = checkType(0, arg0, DelegatingTable.class);
      Spell delegate = checkType(0, self.getDelegate(), Spell.class);

      boolean result = delegate.popLocation();
      context.getReturnBuffer().setTo(result);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class CutFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg0, Object arg1)
        throws ResolvedControlThrowable {
      DelegatingTable<?> self = checkType(0, arg0, DelegatingTable.class);
      Spell delegate = checkType(0, self.getDelegate(), Spell.class);

      Table table = checkType(1, arg1, Table.class);

      Selection selection = new Selection();
      Object k = table.initialKey();
      while (k != null) {
        // process the key k
        Object v = table.rawget(k);
        BlockPos pos = toBlockPos(v);
        selection.add(pos);
        k = table.successorKeyOf(k);
      }
      String id = delegate.cut(selection);

      context.getReturnBuffer().setTo(id);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class CopyFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg0, Object arg1)
        throws ResolvedControlThrowable {
      DelegatingTable<?> self = checkType(0, arg0, DelegatingTable.class);
      Spell delegate = checkType(0, self.getDelegate(), Spell.class);

      Table table = checkType(1, arg1, Table.class);

      Selection selection = new Selection();
      Object k = table.initialKey();
      while (k != null) {
        // process the key k
        Object v = table.rawget(k);
        BlockPos pos = toBlockPos(v);
        selection.add(pos);
        k = table.successorKeyOf(k);
      }
      String id = delegate.copy(selection);

      context.getReturnBuffer().setTo(id);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class PasteFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg0, Object arg1)
        throws ResolvedControlThrowable {
      DelegatingTable<?> self = checkType(0, arg0, DelegatingTable.class);
      Spell delegate = checkType(0, self.getDelegate(), Spell.class);

      String id = checkTypeString(1, arg1);

      Selection resultSelection = delegate.paste(id);

      Table result = DefaultTable.factory().newTable();
      long idx = 0;
      for (BlockPos pos : resultSelection.getPositions()) {
        idx++;
        Table vec3d = toVec3dTable(pos);
        result.rawset(idx, vec3d);
      }
      context.getReturnBuffer().setTo(result);
    }

    private Table toVec3dTable(BlockPos pos) {
      Table result = DefaultTable.factory().newTable();
      result.rawset("x", pos.getX());
      result.rawset("y", pos.getY());
      result.rawset("z", pos.getZ());
      return result;
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private BlockPos toBlockPos(Object v) {
    if (!(v instanceof Table)) {
      throw new IllegalArgumentException(String.format("Vec3 expected, but got %s!", v));
    }
    Table table = (Table) v;
    Object ox = table.rawget("x");
    Object oy = table.rawget("y");
    Object oz = table.rawget("z");
    int x = ((Number) ox).intValue();
    int y = ((Number) oy).intValue();
    int z = ((Number) oz).intValue();

    return new BlockPos(x, y, z);
  }

  private String format(ExecutionContext context, Table env, List<Object> args)
      throws ResolvedControlThrowable {
    StringWriter writer = new StringWriter();
    WriterOutputStream out = new WriterOutputStream(writer);
    LuaFunction printFunc = BasicLib.print(out, env);
    Object[] argArray = args.toArray();
    printFunc.invoke(context, argArray);
    return TabEncoder.encode(writer.toString());
  }
}
