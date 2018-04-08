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

  public static class Proxy<D extends PlayerInteractEvent>
      extends EventClass.Proxy<EventApi<D>, D> {
    public Proxy(ProxyingLuaClass<?, ?> luaClass, D delegate) {
      super(new EventApi<>(luaClass, delegate));
      addImmutable("player", getConverter().toLua(delegate.getEntityPlayer()));
      addImmutableNullable("face", getConverter().toLuaNullable(delegate.getFace()));
      addImmutable("hand", getConverter().toLua(delegate.getHand()));
      addImmutable("pos", getConverter().toLua(new Vec3d(delegate.getPos())));
      addImmutable("item", getConverter().toLua(delegate.getItemStack()));
    }
  }
}
