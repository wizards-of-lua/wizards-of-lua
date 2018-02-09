package net.wizardsoflua.lua.classes.event;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = PlayerInteractEventClass.METATABLE_NAME,
    superclassname = EventClass.METATABLE_NAME)
public class PlayerInteractEventClass extends
    ProxyingLuaClass<PlayerInteractEvent, PlayerInteractEventClass.Proxy<PlayerInteractEvent>> {
  public static final String METATABLE_NAME = "PlayerInteractEvent";

  @Override
  public Proxy<PlayerInteractEvent> toLua(PlayerInteractEvent javaObj) {
    return new Proxy<>(getConverters(), getMetaTable(), javaObj);
  }

  public static class Proxy<D extends PlayerInteractEvent> extends EventClass.Proxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addImmutable("player", getConverters().toLua(delegate.getEntityPlayer()));
      addImmutableNullable("face", getConverters().toLuaNullable(delegate.getFace()));
      addImmutable("hand", getConverters().toLua(delegate.getHand()));
      addImmutable("pos", getConverters().toLua(new Vec3d(delegate.getPos())));
      addImmutable("item", getConverters().toLua(delegate.getItemStack()));
    }
  }
}
