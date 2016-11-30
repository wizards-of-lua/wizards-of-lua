package net.karneim.luamod.lua.wrapper;

import net.karneim.luamod.Players;
import net.minecraft.entity.player.EntityPlayerMP;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction0;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class PlayersWrapper {

  public static PlayersWrapper installInto(Table env, Players players) {
    PlayersWrapper result = new PlayersWrapper(players);
    env.rawset("players", result.getLuaTable());
    return result;
  }

  private final Players players;
  private final Table luaTable = new DefaultTable();

  public PlayersWrapper(Players players) {
    this.players = players;
    luaTable.rawset("list", new ListFunction());
    luaTable.rawset("get", new GetFunction());
  }

  public Table getLuaTable() {
    return luaTable;
  }

  private class ListFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      String[] names = players.list();
      StringArrayWrapper wrapper = new StringArrayWrapper(names);
      context.getReturnBuffer().setTo(wrapper.getLuaObject());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class GetFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("Player name expected but got nil!"));
      }
      String name = String.valueOf(arg1);
      EntityPlayerMP player = players.getPlayer(name);
      EntityPlayerWrapper wrapper = new EntityPlayerWrapper(player);
      context.getReturnBuffer().setTo(wrapper.getLuaObject());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

}
