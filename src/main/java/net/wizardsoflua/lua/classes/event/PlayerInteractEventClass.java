package net.wizardsoflua.lua.classes.event;

import com.google.auto.service.AutoService;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;
import net.wizardsoflua.lua.classes.spi.DeclaredLuaClass;

@AutoService(DeclaredLuaClass.class)
@DeclareLuaClass (name = PlayerInteractEventClass.NAME, superClass = EventClass.class)
public class PlayerInteractEventClass extends
    DelegatorLuaClass<PlayerInteractEvent, PlayerInteractEventClass.Proxy<PlayerInteractEvent>> {
  public static final String NAME = "PlayerInteractEvent";

  @Override
  public Proxy<PlayerInteractEvent> toLua(PlayerInteractEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends PlayerInteractEvent>
      extends EventClass.Proxy<EventApi<D>, D> {
    public Proxy(DelegatorLuaClass<?, ?> luaClass, D delegate) {
      super(new EventApi<>(luaClass, delegate));
      addImmutable("player", getConverters().toLua(delegate.getEntityPlayer()));
      addImmutableNullable("face", getConverters().toLuaNullable(delegate.getFace()));
      addImmutable("hand", getConverters().toLua(delegate.getHand()));
      addImmutable("pos", getConverters().toLua(new Vec3d(delegate.getPos())));
      addImmutable("item", getConverters().toLua(delegate.getItemStack()));
    }
  }
}
