package net.karneim.luamod.lua.wrapper;

import static net.karneim.luamod.lua.util.PreconditionsUtils.checkType;
import static net.karneim.luamod.lua.wrapper.WrapperFactory.wrap;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.MathHelper;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;

public class EntityInstance<E extends Entity> extends DelegatingTableWrapper<E> {

  public EntityInstance(Table env, @Nullable E delegate, Table metatable) {
    super(env, delegate, metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b) {
    b.add("id", delegate::getCachedUniqueIdString, null);
    b.add("name", delegate::getName,
        (Object name) -> delegate.setCustomNameTag(String.valueOf(name)));
    b.add("dimension", () -> delegate.dimension,
        (Object dimension) -> delegate.dimension = checkType(dimension, Number.class).intValue());
    b.add("pos", () -> wrap(env, delegate.getPositionVector()), this::setPosition);
    b.add("blockPos", () -> wrap(env, delegate.getPosition()), null);
    b.add("eyeHeight", () -> delegate.getEyeHeight(), null);
    b.add("orientation", () -> wrap(env, delegate.getHorizontalFacing()), null);
    b.add("rotationYaw", () -> MathHelper.wrapDegrees(delegate.rotationYaw),
        (Object yaw) -> delegate.rotationYaw = checkType(yaw, Number.class).floatValue());
    b.add("rotationPitch", () -> delegate.rotationPitch,
        (Object pitch) -> delegate.rotationPitch = checkType(pitch, Number.class).floatValue());
    b.add("lookVec", () -> wrap(env, delegate.getLookVec()), null);
    b.add("team", this::getTeam, null);
    b.add("tags", () -> wrap(env, delegate.getTags()), null);
    b.add("facing", () -> wrap(env, delegate.getAdjustedHorizontalFacing()), null);
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
}
