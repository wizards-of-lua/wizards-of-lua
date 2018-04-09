package net.wizardsoflua.lua.classes.entity;

import static java.lang.String.format;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameType;
import net.sandius.rembulan.LuaRuntimeException;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClass;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;

@GenerateLuaClass(name = PlayerApi.NAME)
@GenerateLuaDoc(subtitle = "Controlling the Player")
public class PlayerApi<D extends EntityPlayerMP> extends EntityLivingBaseApi<D> {
  public static final String NAME = "Player";

  public PlayerApi(DelegatorLuaClass<?, ?> luaClass, D delegate) {
    super(luaClass, delegate);
  }

  @Override
  public void putNbt(Table nbt) {
    throw new LuaRuntimeException(format("%s not supported for class %s", "putNbt", NAME));
  }

  @LuaProperty
  public GameType getGamemode() {
    return delegate.interactionManager.getGameType();
  }

  @LuaProperty
  public void setGamemode(GameType gamemode) {
    delegate.setGameType(gamemode);
  }

  @LuaProperty
  public @Nullable String getTeam() {
    Team team = delegate.getTeam();
    if (team == null) {
      return null;
    }
    return team.getName();
  }

  @LuaProperty
  public void setTeam(@Nullable String team) {
    if (team == null) {
      delegate.getWorldScoreboard().removePlayerFromTeams(delegate.getName());
    } else {
      boolean success = delegate.getWorldScoreboard().addPlayerToTeam(delegate.getName(), team);
      if (!success) {
        throw new IllegalArgumentException(
            String.format("Couldn't add player %s to unknown team %s!", delegate.getName(), team));
      }
    }
  }

  @Override
  public void setRotationYawAndPitch(float yaw, float pitch) {
    super.setRotationYawAndPitch(yaw, pitch);
    if (delegate instanceof EntityPlayerMP) {
      ((EntityPlayerMP) delegate).connection.setPlayerLocation(delegate.posX, delegate.posY,
          delegate.posZ, delegate.rotationYaw, delegate.rotationPitch);
    }
  }

  @Override
  public float getRotationYaw() {
    return MathHelper.wrapDegrees(delegate.rotationYaw);
  }

  @Override
  public void setRotationYaw(float rotationYaw) {
    super.setRotationYaw(rotationYaw);
    if (delegate instanceof EntityPlayerMP) {
      ((EntityPlayerMP) delegate).connection.setPlayerLocation(delegate.posX, delegate.posY,
          delegate.posZ, delegate.rotationYaw, delegate.rotationPitch);
    }
  }

  @Override
  public void setRotationPitch(float rotationPitch) {
    super.setRotationPitch(rotationPitch);
    if (delegate instanceof EntityPlayerMP) {
      ((EntityPlayerMP) delegate).connection.setPlayerLocation(delegate.posX, delegate.posY,
          delegate.posZ, delegate.rotationYaw, delegate.rotationPitch);
    }
  }
}
