package net.wizardsoflua.lua.converters.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction3;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.converters.Converters;
import net.wizardsoflua.lua.converters.common.DelegatingProxy;
import net.wizardsoflua.lua.module.types.Terms;

public class EntityConverter {
  public static final String METATABLE_NAME = "Entity";

  private final Converters converters;
  private final Table metatable;

  public EntityConverter(Converters converters) {
    this.converters = converters;
    // TODO do declaration outside this class
    this.metatable = converters.getTypes().declare(METATABLE_NAME);
    metatable.rawset("move", new MoveFunction());
  }

  public Table toLua(Entity delegate) {
    return new Proxy(converters, metatable, delegate);
  }

  public static class Proxy extends DelegatingProxy {

    private final Entity delegate;

    public Proxy(Converters converters, Table metatable, Entity delegate) {
      super(converters, metatable, delegate);
      this.delegate = delegate;
      addReadOnly("dimension", () -> delegate.dimension);
      addReadOnly("uuid", this::getUuid);
      add("name", this::getName, this::setName);
      add("pos", this::getPos, this::setPos);
    }

    public Table getPos() {
      return getConverters().vec3ToLua(delegate.getPositionVector());
    }

    public void setPos(Object luaObj) {
      Vec3d pos = getConverters().vec3ToJava(luaObj);
      delegate.setPositionAndUpdate(pos.xCoord, pos.yCoord, pos.zCoord);
    }

    public ByteString getUuid() {
      return getConverters().stringToLua(delegate.getUniqueID().toString());
    }

    public ByteString getName() {
      return getConverters().stringToLua(delegate.getName());
    }

    public void setName(Object luaObj) {
      String name = getConverters().stringToJava(luaObj);
      delegate.setCustomNameTag(name);
    }

    public void move(String direction, @Nullable Number distance) {
      EnumFacing facing = EnumFacing.byName(direction);
      checkNotNull(facing != null, "expected direction but got %s", direction);
      if (distance == null) {
        distance = 1L;
      }
      Vec3i vec = facing.getDirectionVec();
      double x = delegate.posX + vec.getX() * distance.doubleValue();
      double y = delegate.posY + vec.getY() * distance.doubleValue();
      double z = delegate.posZ + vec.getZ() * distance.doubleValue();
      delegate.setPositionAndUpdate(x, y, z);
    }

  }

  private class MoveFunction extends AbstractFunction3 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2, Object arg3)
        throws ResolvedControlThrowable {
      converters.getTypes().checkAssignable(METATABLE_NAME, arg1);
      Proxy proxy = (Proxy) arg1;
      String direction = converters.getTypes().castString(arg2, Terms.MANDATORY);
      Number distance = converters.getTypes().castNumber(arg3, Terms.OPTIONAL);
      proxy.move(direction, distance);
      context.getReturnBuffer().setTo();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

}
