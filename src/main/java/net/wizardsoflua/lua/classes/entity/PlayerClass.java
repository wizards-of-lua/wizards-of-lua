package net.wizardsoflua.lua.classes.entity;

import static java.lang.String.format;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Inject;
import com.google.auto.service.AutoService;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketPlayerPosLook.EnumFlags;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
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
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.common.Delegator;
import net.wizardsoflua.permissions.Permissions;

@AutoService(LuaConverter.class)
@LuaClassAttributes(name = PlayerClass.NAME, superClass = EntityClass.class)
@GenerateLuaClassTable(instance = PlayerClass.Instance.class)
@GenerateLuaDoc(subtitle = "Controlling the Player")
public final class PlayerClass
    extends BasicLuaClass<EntityPlayer, PlayerClass.Instance<EntityPlayerMP>> {
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
  protected Delegator<Instance<EntityPlayerMP>> toLuaInstance(EntityPlayer javaInstance) {
    return new PlayerClassInstanceTable<>(new Instance<>((EntityPlayerMP) javaInstance, injector),
        getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends EntityPlayerMP> extends EntityLivingBaseClass.Instance<D> {
    @Inject
    private Permissions permissions;

    public Instance(D delegate, Injector injector) {
      super(delegate, injector);
    }

    @Override
    public String getEntityType() {
      return "player";
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
    public boolean isOperator() {
      return permissions.hasOperatorPrivileges(delegate.getUniqueID());
    }

    // TODO: isn't team supported for entities?

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
      Scoreboard scoreboard = delegate.getWorldScoreboard();
      String name = delegate.getScoreboardName();
      if (team == null) {
        scoreboard.removePlayerFromTeams(name);
      } else {
        ScorePlayerTeam teamObj = scoreboard.getTeam(team);
        if (teamObj == null) {
          throw new IllegalArgumentException(
              String.format("Couldn't add player %s to unknown team %s!", name, team));
        }
        scoreboard.addPlayerToTeam(name, teamObj);
      }
    }

    @Override
    protected void setPos(double x, double y, double z) {
      Set<EnumFlags> relativeFlags = EnumSet.allOf(EnumFlags.class);
      relativeFlags.remove(EnumFlags.X);
      relativeFlags.remove(EnumFlags.Y);
      relativeFlags.remove(EnumFlags.Z);
      delegate.connection.setPlayerLocation(x, y, z, 0, 0, relativeFlags);
    }

    @Override
    public void setRotationPitch(float rotationPitch) {
      Set<EnumFlags> relativeFlags = EnumSet.allOf(EnumFlags.class);
      relativeFlags.remove(EnumFlags.X_ROT);
      delegate.connection.setPlayerLocation(0, 0, 0, 0, rotationPitch, relativeFlags);
    }

    @Override
    public void setRotationYaw(float rotationYaw) {
      Set<EnumFlags> relativeFlags = EnumSet.allOf(EnumFlags.class);
      relativeFlags.remove(EnumFlags.Y_ROT);
      delegate.connection.setPlayerLocation(0, 0, 0, rotationYaw, 0, relativeFlags);
    }

    @Override
    protected void setRotation(float rotationYaw, float rotationPitch) {
      Set<EnumFlags> relativeFlags = EnumSet.allOf(EnumFlags.class);
      relativeFlags.remove(EnumFlags.X_ROT);
      relativeFlags.remove(EnumFlags.Y_ROT);
      delegate.connection.setPlayerLocation(0, 0, 0, rotationYaw, rotationPitch, relativeFlags);
    }
  }
}
