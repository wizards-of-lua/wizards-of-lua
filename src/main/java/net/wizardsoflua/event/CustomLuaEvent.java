package net.wizardsoflua.event;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.wizardsoflua.lua.data.Data;

public class CustomLuaEvent extends Event {

  private final String name;
  private final Data data;

  public CustomLuaEvent(String name, Data data) {
    this.name = checkNotNull(name, "eventName==null!");
    this.data = checkNotNull(data, "data==null!");;
  }

  public String getName() {
    return name;
  }

  public Data getData() {
    return data;
  }

}
