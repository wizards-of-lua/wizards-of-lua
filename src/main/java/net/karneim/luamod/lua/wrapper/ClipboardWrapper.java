package net.karneim.luamod.lua.wrapper;

import net.karneim.luamod.cursor.Clipboard;
import net.karneim.luamod.cursor.Snapshot;
import net.karneim.luamod.cursor.Snapshots;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.AbstractFunction2;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class ClipboardWrapper {

  public static ClipboardWrapper installInto(Table env, Clipboard clipboard, Snapshots snapshots) {
    ClipboardWrapper result = new ClipboardWrapper(clipboard, snapshots);
    env.rawset("clipboard", result.getLuaTable());
    return result;
  }

  private final Clipboard clipboard;
  private final Snapshots snapshots;
  private final Table luaTable = DefaultTable.factory().newTable();

  public ClipboardWrapper(Clipboard clipboard, Snapshots snapshots) {
    this.clipboard = clipboard;
    this.snapshots = snapshots;
    luaTable.rawset("put", new PutFunction());
    luaTable.rawset("get", new GetFunction());
  }

  public Table getLuaTable() {
    return luaTable;
  }

  private class PutFunction extends AbstractFunction2 {
    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      // System.out.println("put: " + arg1+", "+arg2);
      if (arg1 == null) {
        throw new IllegalArgumentException(
            String.format("String value expected but got %s!", arg1));
      }
      if (arg2 == null) {
        throw new IllegalArgumentException(String.format("Snapshot ID expected but got %s!", arg2));
      }
      String id = String.valueOf(arg2);
      Snapshot snap = snapshots.getSnapshot(id);
      if (snap == null) {
        throw new IllegalArgumentException(String.format("Snapshot ID %s unknown!", id));
      }
      String name = String.valueOf(arg1);
      clipboard.put(name, snap);
      context.getReturnBuffer().setTo();
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
      // System.out.println("get: " + arg1);
      if (arg1 == null) {
        throw new IllegalArgumentException(
            String.format("String value expected but got %s!", arg1));
      }
      String name = String.valueOf(arg1);
      Snapshot snap = clipboard.get(name);
      if (snap != null) {
        String id = snapshots.registerSnapshot(snap);
        context.getReturnBuffer().setTo(id);
      } else {
        context.getReturnBuffer().setTo(null);
      }
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

}
