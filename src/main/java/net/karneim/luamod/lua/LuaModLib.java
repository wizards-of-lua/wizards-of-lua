package net.karneim.luamod.lua;

import java.io.IOException;
import java.io.OutputStream;

import net.karneim.luamod.TabEncoder;
import net.karneim.luamod.cursor.EnumDirection;
import net.karneim.luamod.lua.event.EventType;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.text.TextComponentString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.lib.BasicLib;
import net.sandius.rembulan.runtime.LuaFunction;

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
    
    OutputStream out = new ChatOutputStream();
    LuaFunction printFunc = BasicLib.print(out, env);
    env.rawset("print", printFunc);
  }

  private class ChatOutputStream extends org.apache.commons.io.output.ByteArrayOutputStream {
    @Override
    public void flush() throws IOException {
      String message = toString();
      // Remove trailing line-feed.
      if ( message.endsWith("\n")) {
        message = message.substring(0, message.length()-1);
      }
      reset();
      print(message);
    }
  }
  
  private void print(String message) {
    owner.addChatMessage(new TextComponentString(TabEncoder.encode(message)));
  }
  
  
}
