package net.karneim.luamod.lua.classes.event;

import net.karneim.luamod.lua.classes.AbstractLuaType;
import net.karneim.luamod.lua.classes.Constants;
import net.karneim.luamod.lua.classes.ModulePackage;
import net.karneim.luamod.lua.classes.TypeName;
import net.karneim.luamod.lua.event.AnimationHandEvent;
import net.karneim.luamod.lua.event.AnimationHandEventWrapper;
import net.karneim.luamod.lua.event.EventType;
import net.karneim.luamod.lua.wrapper.Metatables;

@TypeName("AnimationHandEvent")
@ModulePackage(Constants.MODULE_PACKAGE)
public class AnimationHandEventClass extends AbstractLuaType {
  public AnimationHandEventWrapper newInstance(AnimationHandEvent delegate, EventType eventType) {
    return new AnimationHandEventWrapper(getRepo(), delegate, eventType,
        Metatables.get(getRepo().getEnv(), getTypeName()));
  }

  @Override
  protected void addFunctions() {}
}
