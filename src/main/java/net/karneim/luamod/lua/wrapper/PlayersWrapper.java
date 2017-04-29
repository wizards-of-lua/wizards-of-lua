package net.karneim.luamod.lua.wrapper;

import net.karneim.luamod.Players;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction0;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class PlayersWrapper {

  public static PlayersWrapper installInto(LuaTypesRepo repo, Players players) {
    PlayersWrapper result = new PlayersWrapper(repo, players);
    repo.getEnv().rawset("Players", result.getLuaTable());
    return result;
  }

  private final LuaTypesRepo repo;
  private final Players players;
  private final Table luaTable = DefaultTable.factory().newTable();

  public PlayersWrapper(LuaTypesRepo repo, Players players) {
    this.repo = repo;
    this.players = players;

    luaTable.rawset("ids", new IdsFunction());
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
      context.getReturnBuffer().setTo(repo.wrapStrings(names));
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private class IdsFunction extends AbstractFunction0 {

    @Override
    public void invoke(ExecutionContext context) throws ResolvedControlThrowable {
      Iterable<String> ids = players.list();
      context.getReturnBuffer().setTo(repo.wrapStrings(ids));
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
      if (player != null) {
        DelegatingTable<? extends EntityPlayer> result = repo.wrap(player);
        context.getReturnBuffer().setTo(result);
      } else {
        context.getReturnBuffer().setTo();
      }
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
      if (player != null) {
        DelegatingTable<? extends EntityPlayer> result = repo.wrap(player);
        context.getReturnBuffer().setTo(result);
      } else {
        context.getReturnBuffer().setTo();
      }
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
      context.getReturnBuffer().setTo(repo.wrapStrings(ids));
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

}
