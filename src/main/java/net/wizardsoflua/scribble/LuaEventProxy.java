package net.wizardsoflua.scribble;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.wizardsoflua.lua.classes.GeneratedLuaInstance;

public class LuaEventProxy<A extends LuaEvent<D>, D extends Event> extends GeneratedLuaInstance<A, D> {
  public LuaEventProxy(A api) {
    super(api);
    addReadOnly("name", this::getName);
  }

  private String getName() {
    return api.getName();
  }
}
