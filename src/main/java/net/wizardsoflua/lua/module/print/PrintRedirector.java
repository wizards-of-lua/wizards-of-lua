package net.wizardsoflua.lua.module.print;

import java.io.IOException;
import java.io.OutputStream;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.lib.BasicLib;
import net.sandius.rembulan.runtime.LuaFunction;

public class PrintRedirector {

  public interface Context {
    public void send(String message);
  }

  public static PrintRedirector installInto(Table env, Context context) {
    return new PrintRedirector(env, context);
  }

  private final Context context;

  public PrintRedirector(Table env, Context context) {
    this.context = context;
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
    context.send(TabEncoder.encode(message));
  }
}
