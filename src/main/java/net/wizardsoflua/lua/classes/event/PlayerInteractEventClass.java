package net.wizardsoflua.lua.classes.event;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.LuaClass;

@DeclareLuaClass(name = PlayerInteractEventClass.METATABLE_NAME,
    superclassname = EventClass.METATABLE_NAME)
public class PlayerInteractEventClass extends LuaClass<PlayerInteractEvent> {
  public static final String METATABLE_NAME = "PlayerInteractEvent";

  public PlayerInteractEventClass() {
    super(PlayerInteractEvent.class);
  }

  @Override
  public Table toLua(PlayerInteractEvent javaObj) {
    return new Proxy(getConverters(), getMetatable(), javaObj, METATABLE_NAME);
  }

  @Override
  public PlayerInteractEvent toJava(Table luaObj) {
    Proxy proxy = getProxy(luaObj);
    return proxy.delegate;
  }

  protected Proxy getProxy(Object luaObj) {
    getConverters().getTypes().checkAssignable(METATABLE_NAME, luaObj);
    return (Proxy) luaObj;
  }

  public static class Proxy extends EventClass.Proxy {

    private final PlayerInteractEvent delegate;

    public Proxy(Converters converters, Table metatable, PlayerInteractEvent delegate,
        String name) {
      super(converters, metatable, delegate, name);
      this.delegate = checkNotNull(delegate, "delegate==null!");
      addImmutable("player", getConverters().toLua(delegate.getEntityPlayer()));
      addImmutableNullable("face", getConverters().toLuaNullable(delegate.getFace()));
      addImmutable("hand", getConverters().toLua(delegate.getHand()));
      addImmutable("pos", getConverters().toLua(new Vec3d(delegate.getPos())));
      // TODO itemStack
      // addImmutable("itemStack", getConverters().toLua(delegate.getItemStack()));
    }

  }

}
