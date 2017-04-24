package net.karneim.luamod.lua.event;

import static com.google.common.base.Preconditions.checkNotNull;

import com.sun.istack.internal.Nullable;

import net.minecraftforge.fml.common.eventhandler.Event;

public class CustomLuaEvent extends Event {
  private final String type;
  private final @Nullable Object data;

  public CustomLuaEvent(String type, @Nullable Object data) {
    this.type = checkNotNull(type, "type == null!");
    this.data = data;
  }

  public String getType() {
    return type;
  }

  public @Nullable Object getData() {
    return data;
  }
}
