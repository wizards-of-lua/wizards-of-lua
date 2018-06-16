package net.wizardsoflua.event;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nullable;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.wizardsoflua.lua.view.ViewFactory;

public class CustomLuaEvent extends Event {
  private final String name;
  private final @Nullable Object data;
  private final ViewFactory provider;

  public CustomLuaEvent(String name, @Nullable Object data, ViewFactory provider) {
    this.name = requireNonNull(name, "name == null!");
    this.data = data;
    this.provider = requireNonNull(provider, "provider == null!");
  }

  public String getName() {
    return name;
  }

  public @Nullable Object getData(ViewFactory viewer) {
    if (viewer == provider) {
      return data;
    }
    return viewer.getView(data, provider);
  }
}
