package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.world.BlockEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = BlockBreakEventClass.NAME, superClass = BlockEventClass.class)
public class BlockBreakEventClass extends
    ProxyingLuaClass<BlockEvent.BreakEvent, BlockBreakEventClass.Proxy<BlockEvent.BreakEvent>> {
  public static final String NAME = "BlockBreakEvent";

  @Override
  public Proxy<BlockEvent.BreakEvent> toLua(BlockEvent.BreakEvent javaObj) {
    return new Proxy<>(getConverters(), getMetaTable(), javaObj);
  }

  public static class Proxy<D extends BlockEvent.BreakEvent> extends BlockEventClass.Proxy<D> {
    public Proxy(Converters converters, Table metatable, D delegate) {
      super(converters, metatable, delegate);
      addReadOnly("experience", this::getExperience);
      addReadOnly("player", this::getPlayer);
    }

    private Object getExperience() {
      return delegate.getExpToDrop();
    }

    private Object getPlayer() {
      return getConverters().toLua(delegate.getPlayer());
    }
  }
}
