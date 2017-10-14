package net.wizardsoflua.lua.classes.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.InstanceCachingLuaClass;

@DeclareLuaClass(name = PlayerClass.METATABLE_NAME, superclassname = EntityClass.METATABLE_NAME)
public class PlayerClass extends InstanceCachingLuaClass<EntityPlayer> {
  public static final String METATABLE_NAME = "Player";

  public PlayerClass() {
    super(EntityPlayer.class);
    add("putNbt", new UnsupportedFunction("putNbt", METATABLE_NAME));
  }

  @Override
  public Table toLua(EntityPlayer delegate) {
    return new Proxy(getConverters(), getMetatable(), delegate);
  }

  @Override
  public EntityPlayer toJava(Table luaObj) {
    Proxy proxy = getProxy(luaObj);
    return proxy.delegate;
  }

  protected Proxy getProxy(Object luaObj) {
    getConverters().getTypes().checkAssignable(METATABLE_NAME, luaObj);
    return (Proxy) luaObj;
  }

  public class Proxy extends EntityClass.EntityLivingBaseProxy {

    private final EntityPlayer delegate;

    public Proxy(Converters converters, Table metatable, EntityPlayer delegate) {
      super(converters, metatable, delegate);
      this.delegate = delegate;

      // Overwrite name, since player names can't be changed
      addReadOnly("name", this::getName);
      add("team", this::getTeam, this::setTeam);
    }

    @Override
    public void setRotationYaw(Object luaObj) {
      super.setRotationYaw(luaObj);
      if (delegate instanceof EntityPlayerMP) {
        ((EntityPlayerMP) delegate).connection.setPlayerLocation(delegate.posX, delegate.posY,
            delegate.posZ, delegate.rotationYaw, delegate.rotationPitch);
      }
    }

    @Override
    public void setRotationPitch(Object luaObj) {
      super.setRotationPitch(luaObj);
      if (delegate instanceof EntityPlayerMP) {
        ((EntityPlayerMP) delegate).connection.setPlayerLocation(delegate.posX, delegate.posY,
            delegate.posZ, delegate.rotationYaw, delegate.rotationPitch);
      }
    }

    public ByteString getTeam() {
      Team team = delegate.getTeam();
      if (team == null) {
        return null;
      }
      return ByteString.of(team.getRegisteredName());
    }

    public void setTeam(Object luaObj) {
      String teamName = getConverters().toJavaNullable(String.class, luaObj);
      if (teamName == null) {
        delegate.getWorldScoreboard().removePlayerFromTeams(delegate.getName());
      } else {
        boolean success =
            delegate.getWorldScoreboard().addPlayerToTeam(delegate.getName(), teamName);
        if (!success) {
          throw new IllegalArgumentException(String
              .format("Couldn't add player %s to unknown team %s!", delegate.getName(), teamName));
        }
      }
    }
  }
}
