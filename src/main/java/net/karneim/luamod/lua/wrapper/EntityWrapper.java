package net.karneim.luamod.lua.wrapper;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.DynamicTable;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Team;
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

  @Override
  protected void addProperties(DynamicTable.Builder builder) {
    super.addProperties(builder);
    builder.add("id", delegate.getCachedUniqueIdString());
    builder.add("name", delegate.getName());
    builder.add("dimension", delegate.dimension);
    builder.add("pos", new Vec3dWrapper(delegate.getPositionVector()).getLuaObject());
    builder.add("blockPos", new BlockPosWrapper(delegate.getPosition()).getLuaObject());
    builder.add("orientation", new EnumWrapper(delegate.getHorizontalFacing()).getLuaObject());
    builder.add("rotationYaw", delegate.rotationYaw);
    builder.add("rotationPitch", delegate.rotationPitch);
    Team team = delegate.getTeam();
    builder.add("team", team != null ? team.getRegisteredName() : null);
    builder.add("tags", new StringIterableWrapper(delegate.getTags()).getLuaObject());

    builder.add("setName", new SetNameFunction());
    builder.add("setPos", new SetPosFunction());
    builder.add("setRotationYaw", new SetRotationYawFunction());
    builder.add("setRotationPitch", new SetRotationPitchFunction());
    builder.add("addTag", new AddTagFunction());
    builder.add("removeTag", new RemoveTagFunction());
    builder.add("setTags", new SetTagsFunction());
  }

  private class SetNameFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("string expected but got nil!"));
      }
      String name = String.valueOf(arg1);
      delegate.setCustomNameTag(name);
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class SetPosFunction extends AbstractFunction3 {

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2, Object arg3)
        throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("Arg 1: number expected but got nil!"));
      }
      if (arg2 == null) {
        throw new IllegalArgumentException(String.format("Arg 2: number expected but got nil!"));
      }
      if (arg3 == null) {
        throw new IllegalArgumentException(String.format("Arg 3: number expected but got nil!"));
      }
      double x = ((Number) arg1).doubleValue();
      double y = ((Number) arg2).doubleValue();
      double z = ((Number) arg3).doubleValue();
      delegate.setPositionAndUpdate(x, y, z);
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class SetRotationYawFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("Number expected but got nil!"));
      }
      float yaw = ((Number) arg1).floatValue();
      float pitch = delegate.rotationPitch;
      double x = delegate.posX;
      double y = delegate.posY;
      double z = delegate.posZ;
      delegate.setPositionAndRotation(x, y, z, yaw, pitch);
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class SetRotationPitchFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("Number expected but got nil!"));
      }
      float yaw = delegate.rotationYaw;
      float pitch = ((Number) arg1).floatValue();
      double x = delegate.posX;
      double y = delegate.posY;
      double z = delegate.posZ;
      delegate.setPositionAndRotation(x, y, z, yaw, pitch);
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class AddTagFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("string expected but got nil!"));
      }
      String tag = String.valueOf(arg1);
      delegate.addTag(tag);
      context.getReturnBuffer().setTo();
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
      delegate.removeTag(tag);
      context.getReturnBuffer().setTo();
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
