package net.karneim.luamod.lua;

import net.karneim.luamod.cursor.EnumDirection;
import net.karneim.luamod.lua.event.EventType;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.text.TextComponentString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunctionAnyArg;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class LuaModLib {

  public static LuaModLib installInto(Table env, ICommandSender owner) {
    return new LuaModLib(env, owner);
  }

  private ICommandSender owner;

  public LuaModLib(Table env, ICommandSender owner) {
    this.owner = owner;
    for (EnumDirection e : EnumDirection.values()) {
      env.rawset(e.name(), e.name());
    }
    for (EnumFacing e : EnumFacing.values()) {
      env.rawset(e.name(), e.name());
    }
    for (Rotation e : Rotation.values()) {
      env.rawset(e.name(), e.name());
    }
    for (EventType e : EventType.values()) {
      env.rawset(e.name(), e.name());
    }
    env.rawset("SURFACE","SURFACE");
    env.rawset("print", new PrintFunction());
  }

  private class PrintFunction extends AbstractFunctionAnyArg {
    @Override
    public void invoke(ExecutionContext context, Object[] args) throws ResolvedControlThrowable {
      // System.out.println("print: " + args);
      if (args == null) {
        args = new String[] {""};
      }
      String text = concat("\t", args);
      print(encode(text));
      context.getReturnBuffer().setTo();
    }

    private String concat(String delimter, Object[] args) {
      StringBuilder result = new StringBuilder();
      for (int i = 0; i < args.length; ++i) {
        if (i > 0) {
          result.append(delimter);
        }
        if (args[i] == null) {
          result.append("nil");
        } else {
          result.append(String.valueOf(args[i]));
        }
      }
      return result.toString();
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

  private String encode(String text) {
    return text.replaceAll("\t", "    ");
  }

  public void print(String message) {
    owner.addChatMessage(new TextComponentString(message));
  }

}
