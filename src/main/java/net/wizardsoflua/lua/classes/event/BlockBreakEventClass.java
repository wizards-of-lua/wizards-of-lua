package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.world.BlockEvent;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;

@DeclareLuaClass(name = BlockBreakEventClass.NAME, superClass = BlockEventClass.class)
public class BlockBreakEventClass extends
    DelegatorLuaClass<BlockEvent.BreakEvent, BlockBreakEventClass.Proxy<BlockEvent.BreakEvent>> {
  public static final String NAME = "BlockBreakEvent";

  @Override
  public Proxy<BlockEvent.BreakEvent> toLua(BlockEvent.BreakEvent javaObj) {
    return new Proxy<>(this, javaObj);
  }

  public static class Proxy<D extends BlockEvent.BreakEvent> extends BlockEventClass.Proxy<D> {
    public Proxy(DelegatorLuaClass<?, ?> luaClass, D delegate) {
      super(luaClass, delegate);
      addReadOnly("experience", this::getExperience);
      addReadOnly("player", this::getPlayer);
    }

    private Object getExperience() {
      return delegate.getExpToDrop();
    }

    private Object getPlayer() {
      return getConverter().toLua(delegate.getPlayer());
    }
  }
}
