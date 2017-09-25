package net.wizardsoflua.lua.classes.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.module.types.Terms;

public class PlayerClass {
  public static final String METATABLE_NAME = "Player";

  private final Converters converters;
  private final Table metatable;

  public PlayerClass(Converters converters) {
    this.converters = converters;
    // TODO do declaration outside this class
    this.metatable = converters.getTypes().declare(METATABLE_NAME, CreatureClass.METATABLE_NAME);
    metatable.rawset("putNbt", new UnsupportedFunction("putNbt", METATABLE_NAME));
  }

  public Table toLua(EntityPlayer delegate) {
    return new Proxy(converters, metatable, delegate);
  }

  public class Proxy extends CreatureClass.Proxy {

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
      String teamName = getConverters().getTypes().castString(luaObj, Terms.OPTIONAL);
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
