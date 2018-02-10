package net.wizardsoflua.lua.classes.event;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = PlayerInteractEventClass.NAME, superClass = EventClass.class)
public class PlayerInteractEventClass extends
    ProxyingLuaClass<PlayerInteractEvent, PlayerInteractEventClass.Proxy<PlayerInteractEvent>> {
  public static final String NAME = "PlayerInteractEvent";

  @Override
  public Proxy<PlayerInteractEvent> toLua(PlayerInteractEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends PlayerInteractEvent> extends EventClass.Proxy<D> {
    public Proxy(ProxyingLuaClass<?, ?> luaClass, D delegate) {
      super(luaClass, delegate);
      addImmutable("player", getConverters().toLua(delegate.getEntityPlayer()));
      addImmutableNullable("face", getConverters().toLuaNullable(delegate.getFace()));
      addImmutable("hand", getConverters().toLua(delegate.getHand()));
      addImmutable("pos", getConverters().toLua(new Vec3d(delegate.getPos())));
      addImmutable("item", getConverters().toLua(delegate.getItemStack()));
    }
  }
}
