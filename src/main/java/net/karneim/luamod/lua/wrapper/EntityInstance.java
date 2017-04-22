package net.karneim.luamod.lua.wrapper;

import static net.karneim.luamod.lua.util.PreconditionsUtils.checkType;
import static net.karneim.luamod.lua.wrapper.WrapperFactory.wrap;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;

public class EntityInstance<E extends Entity> extends DelegatingTableWrapper<E> {

  public EntityInstance(LuaTypesRepo repo, @Nullable E delegate, Table metatable) {
    super(repo, delegate, metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b) {
    b.add("id", delegate::getCachedUniqueIdString, null);
    b.add("name", delegate::getName,
        (Object name) -> delegate.setCustomNameTag(String.valueOf(name)));
    b.add("dimension", () -> delegate.dimension,
        (Object dimension) -> delegate.dimension = checkType(dimension, Number.class).intValue());
    b.add("pos", () -> wrap(repo, delegate.getPositionVector()), this::setPosition);
    b.add("blockPos", () -> wrap(repo, delegate.getPosition()), null);
    b.add("eyeHeight", () -> delegate.getEyeHeight(), null);
    b.add("orientation", () -> wrap(repo, delegate.getHorizontalFacing()), null);
    b.add("rotationYaw", () -> MathHelper.wrapDegrees(delegate.rotationYaw), this::setRotationYaw);
    b.add("rotationPitch", () -> delegate.rotationPitch, this::setRotationPitch);
    b.add("lookVec", () -> wrap(repo, delegate.getLookVec()), null);
    b.add("team", this::getTeam, null);
    b.add("tags", () -> wrap(repo, delegate.getTags()), null);
    b.add("facing", () -> wrap(repo, delegate.getAdjustedHorizontalFacing()), null);

    b.add("motion",
        () -> wrap(repo, new Vec3d(delegate.motionX, delegate.motionY, delegate.motionZ)),
        this::setMotion);
  }

  private void setRotationYaw(Object object) {
    float yaw = checkType(object, Number.class).floatValue();
    delegate.setPositionAndRotation(delegate.posX, delegate.posY, delegate.posZ, yaw,
        delegate.rotationPitch);
    if (delegate instanceof EntityPlayerMP) {
      ((EntityPlayerMP) delegate).connection.setPlayerLocation(delegate.posX, delegate.posY,
          delegate.posZ, delegate.rotationYaw, delegate.rotationPitch);
    }
  }

  private void setRotationPitch(Object object) {
    float pitch = checkType(object, Number.class).floatValue();
    delegate.setPositionAndRotation(delegate.posX, delegate.posY, delegate.posZ,
        delegate.rotationPitch, pitch);
    if (delegate instanceof EntityPlayerMP) {
      ((EntityPlayerMP) delegate).connection.setPlayerLocation(delegate.posX, delegate.posY,
          delegate.posZ, delegate.rotationYaw, delegate.rotationPitch);
    }
  }

  private void setMotion(Object object) {
    Table vector = checkType(object, Table.class);
    double x = checkType(vector.rawget("x"), Number.class).doubleValue();
    double y = checkType(vector.rawget("y"), Number.class).doubleValue();
    double z = checkType(vector.rawget("z"), Number.class).doubleValue();

    // see SPacketEntityVelocity
    double maxLen = 3.9;
    double lenSqr = x * x + y * y + z * z;
    if (lenSqr > maxLen * maxLen) {
      double f = maxLen / Math.sqrt(lenSqr);
      x = x * f;
      y = y * f;
      z = z * f;
    }

    delegate.motionX = x;
    delegate.motionY = y;
    delegate.motionZ = z;
    delegate.velocityChanged = true;
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
