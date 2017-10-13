package net.wizardsoflua.lua.classes.event;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.LuaClass;

@DeclareLuaClass(name = RightClickBlockEventClass.METATABLE_NAME,
    superclassname = PlayerInteractEventClass.METATABLE_NAME)
public class RightClickBlockEventClass extends LuaClass<PlayerInteractEvent.RightClickBlock> {
  public static final String METATABLE_NAME = "RightClickBlockEvent";

  public RightClickBlockEventClass() {
    super(PlayerInteractEvent.RightClickBlock.class);
  }

  @Override
  public Table toLua(PlayerInteractEvent.RightClickBlock javaObj) {
    return new Proxy(getConverters(), getMetatable(), javaObj, METATABLE_NAME);
  }

  @Override
  public PlayerInteractEvent.RightClickBlock toJava(Table luaObj) {
    Proxy proxy = getProxy(luaObj);
    return proxy.delegate;
  }

  protected Proxy getProxy(Object luaObj) {
    getConverters().getTypes().checkAssignable(METATABLE_NAME, luaObj);
    return (Proxy) luaObj;
  }

  public static class Proxy extends PlayerInteractEventClass.Proxy {

    private final PlayerInteractEvent.RightClickBlock delegate;

    public Proxy(Converters converters, Table metatable,
        PlayerInteractEvent.RightClickBlock delegate, String name) {
      super(converters, metatable, delegate, name);
      this.delegate = checkNotNull(delegate, "delegate==null!");
      addImmutableNullable("hitVec", getConverters().toLuaNullable(delegate.getHitVec()));
    }

  }

}
