package net.wizardsoflua.scribble;

import net.minecraftforge.fml.common.eventhandler.Event;

public class LuaEventProxy extends LuaApiProxy<LuaEvent, Event> {
  public LuaEventProxy(LuaEvent api) {
    super(api);
    addReadOnly("name", this::getName);
  }

  private String getName() {
    return api.getName();
  }
}
