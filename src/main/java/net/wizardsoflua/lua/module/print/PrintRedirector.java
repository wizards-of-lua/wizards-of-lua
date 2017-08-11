package net.wizardsoflua.lua.module.print;

import java.io.IOException;
import java.io.OutputStream;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.lib.BasicLib;
import net.sandius.rembulan.runtime.LuaFunction;

public class PrintRedirector {

  public static PrintRedirector installInto(Table env, ICommandSender owner) {
    return new PrintRedirector(env, owner);
  }

  private final ICommandSender owner;

  public PrintRedirector(Table env, ICommandSender owner) {
    this.owner = owner;
    OutputStream out = new ChatOutputStream();
    LuaFunction printFunc = BasicLib.print(out, env);
    env.rawset("print", printFunc);
  }

  private class ChatOutputStream extends org.apache.commons.io.output.ByteArrayOutputStream {
    @Override
    public void flush() throws IOException {
      String message = toString();
      // Remove trailing line-feed.
      if (message.endsWith("\n")) {
        message = message.substring(0, message.length() - 1);
      }
      reset();
      print(message);
    }
  }

  private void print(String message) {
    owner.sendMessage(new TextComponentString(TabEncoder.encode(message)));
  }
}
