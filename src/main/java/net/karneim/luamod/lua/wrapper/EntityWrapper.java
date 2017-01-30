package net.karneim.luamod.lua.wrapper;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.karneim.luamod.lua.util.PreconditionsUtils.checkType;
import static net.karneim.luamod.lua.wrapper.WrapperFactory.wrap;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import net.karneim.luamod.lua.util.PreconditionsUtils;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Team;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction3;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class EntityWrapper<E extends Entity> extends StructuredLuaWrapper<E> {
  public EntityWrapper(@Nullable E delegate) {
    super(delegate);
  }

  private void setPosition(Object object) {
    Table vector = checkType(object, Table.class);
    Number x = checkType(vector.rawget("x"), Number.class);
    Number y = checkType(vector.rawget("y"), Number.class);
    Number z = checkType(vector.rawget("z"), Number.class);
    delegate.setPositionAndUpdate(x.doubleValue(), y.doubleValue(), z.doubleValue());
  }

  private ByteString getTeam() {
    Team team = delegate.getTeam();
    return team != null ? ByteString.of(team.getRegisteredName()) : null;
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b) {
    super.addProperties(b);
    b.add("id", delegate::getCachedUniqueIdString, null);
    b.add("name", delegate::getName,
        (Object name) -> delegate.setCustomNameTag(checkType(name, ByteString.class).toString()));
    b.add("dimension", () -> delegate.dimension,
        (Object dimension) -> delegate.dimension = checkType(dimension, Number.class).intValue());
    b.add("pos", () -> wrap(delegate.getPositionVector()), this::setPosition);
    b.add("blockPos", () -> wrap(delegate.getPosition()), null);
    b.add("orientation", () -> wrap(delegate.getHorizontalFacing()), null);
    b.add("rotationYaw", () -> delegate.rotationYaw,
        (Object yaw) -> delegate.rotationYaw = checkType(yaw, Number.class).floatValue());
    b.add("rotationPitch", () -> delegate.rotationPitch,
        (Object pitch) -> delegate.rotationPitch = checkType(pitch, Number.class).floatValue());
    b.add("lookVec", () -> wrap(delegate.getLookVec()), null);
    b.add("team", this::getTeam, null);
    b.add("tags", () -> wrap(delegate.getTags()), null);

    b.add("addTag", new AddTagFunction());
    b.add("removeTag", new RemoveTagFunction());
    b.add("setTags", new SetTagsFunction());
  }

  private class AddTagFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("string expected but got nil!"));
      }
      String tag = String.valueOf(arg1);
      if (delegate.getTags().contains(tag)) {
        context.getReturnBuffer().setTo(false);
      } else {
        delegate.addTag(tag);
        context.getReturnBuffer().setTo(true);
      }
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class RemoveTagFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("string expected but got nil!"));
      }
      String tag = String.valueOf(arg1);
      boolean changed = delegate.removeTag(tag);
      context.getReturnBuffer().setTo(changed);
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class SetTagsFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("table expected but got nil!"));
      }
      if (!(arg1 instanceof Table)) {
        throw new IllegalArgumentException(
            String.format("table expected but got %s", arg1.getClass().getSimpleName()));
      }
      delegate.getTags().clear();
      Table table = (Table) arg1;
      Object key = table.initialKey();
      while (key != null) {
        String tag = String.valueOf(table.rawget(key));
        delegate.addTag(tag);
        key = table.successorKeyOf(key);
      }
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

}
