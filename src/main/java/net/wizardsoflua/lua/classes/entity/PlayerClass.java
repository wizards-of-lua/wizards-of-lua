package net.wizardsoflua.lua.classes.entity;

import static java.lang.String.format;
import javax.annotation.Nullable;
import com.google.auto.service.AutoService;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameType;
import net.sandius.rembulan.LuaRuntimeException;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.Injector;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.lua.extension.util.BasicLuaClass;
import net.wizardsoflua.lua.extension.util.LuaClassAttributes;

@AutoService(LuaConverter.class)
@LuaClassAttributes(name = PlayerClass.NAME, superClass = EntityClass.class)
@GenerateLuaClassTable(instance = PlayerClass.Instance.class)
@GenerateLuaDoc(subtitle = "Controlling the Player")
public class PlayerClass extends BasicLuaClass<EntityPlayerMP, PlayerClass.Instance<?>> {
  public static final String NAME = "Player";
  @Resource
  private LuaConverters converters;
  @Resource
  private Injector injector;

  @Override
  protected Table createRawTable() {
    return new PlayerClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<?>> toLuaInstance(EntityPlayerMP javaInstance) {
    return new PlayerClassInstanceTable<>(new Instance<>(javaInstance, injector), getTable(),
        converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends EntityPlayerMP> extends EntityLivingBaseClass.Instance<D> {
    public Instance(D delegate, Injector injector) {
      super(delegate, injector);
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
          throw new IllegalArgumentException(String
              .format("Couldn't add player %s to unknown team %s!", delegate.getName(), team));
        }
      }
    }

    @Override
    public void setRotationYawAndPitch(float yaw, float pitch) {
      super.setRotationYawAndPitch(yaw, pitch);
      delegate.connection.setPlayerLocation(delegate.posX, delegate.posY, delegate.posZ,
          delegate.rotationYaw, delegate.rotationPitch);
    }

    @Override
    public float getRotationYaw() {
      return MathHelper.wrapDegrees(delegate.rotationYaw);
    }

    @Override
    public void setRotationYaw(float rotationYaw) {
      super.setRotationYaw(rotationYaw);
      delegate.connection.setPlayerLocation(delegate.posX, delegate.posY, delegate.posZ,
          delegate.rotationYaw, delegate.rotationPitch);
    }

    @Override
    public void setRotationPitch(float rotationPitch) {
      super.setRotationPitch(rotationPitch);
      delegate.connection.setPlayerLocation(delegate.posX, delegate.posY, delegate.posZ,
          delegate.rotationYaw, delegate.rotationPitch);
    }
  }
}
