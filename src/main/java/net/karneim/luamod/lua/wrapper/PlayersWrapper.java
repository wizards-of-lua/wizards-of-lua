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
    PlayersWrapper result = new PlayersWrapper(env, players);
    env.rawset("players", result.getLuaTable());
    return result;
  }

  private final Table env;
  private final Players players;
  private final Table luaTable = DefaultTable.factory().newTable();

  public PlayersWrapper(Table env, Players players) {
    this.env = env;
    this.players = players;

    luaTable.rawset("list", new ListFunction());
    luaTable.rawset("get", new GetFunction());
    luaTable.rawset("find", new FindFunction());
    
    luaTable.rawset("names", new NamesFunction());
    luaTable.rawset("getByName", new GetByNameFunction());
  }

  public Table getLuaTable() {
    return luaTable;
  }

  private class NamesFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      String[] names = players.names();
      StringArrayWrapper wrapper = new StringArrayWrapper(env, names);
      context.getReturnBuffer().setTo(wrapper.getLuaObject());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class ListFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      Iterable<String> ids = players.list();
      StringIterableWrapper wrapper = new StringIterableWrapper(env, ids);
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
        throw new IllegalArgumentException(String.format("Player ID expected but got nil!"));
      }
      String id = String.valueOf(arg1);
      EntityPlayerMP player = players.get(id);
      EntityPlayerWrapper wrapper = new EntityPlayerWrapper(env, player);
      context.getReturnBuffer().setTo(wrapper.getLuaObject());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class GetByNameFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("Player name expected but got nil!"));
      }
      String name = String.valueOf(arg1);
      EntityPlayerMP player = players.getByName(name);
      EntityPlayerWrapper wrapper = new EntityPlayerWrapper(env, player);
      context.getReturnBuffer().setTo(wrapper.getLuaObject());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class FindFunction extends AbstractFunction1 {

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      if (arg1 == null) {
        throw new IllegalArgumentException(String.format("@ expression expected but got nil!"));
      }
      String target = String.valueOf(arg1);
      Iterable<String> ids = players.find(target);
      StringIterableWrapper wrapper = new StringIterableWrapper(env, ids);
      context.getReturnBuffer().setTo(wrapper.getLuaObject());
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

}
