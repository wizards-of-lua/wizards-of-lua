package net.wizardsoflua.scribble;

import net.minecraftforge.fml.common.eventhandler.Event;

public class LuaEventProxy<A extends LuaEvent<D>, D extends Event> extends LuaApiProxy<A, D> {
  public LuaEventProxy(A api) {
    super(api);
    addReadOnly("name", this::getName);
  }

  private String getName() {
    return api.getName();
  }
}
