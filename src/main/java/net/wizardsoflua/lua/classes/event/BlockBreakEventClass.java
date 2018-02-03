package net.wizardsoflua.lua.classes.event;

import net.minecraftforge.event.world.BlockEvent;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;

@DeclareLuaClass(name = BlockBreakEventClass.METATABLE_NAME,
    superclassname = BlockEventClass.METATABLE_NAME)
public class BlockBreakEventClass extends
    ProxyingLuaClass<BlockEvent.BreakEvent, BlockBreakEventClass.Proxy<BlockEvent.BreakEvent>> {
  public static final String METATABLE_NAME = "BlockBreakEvent";

  @Override
  protected String getMetatableName() {
    return METATABLE_NAME;
  }

  @Override
  public Proxy<BlockEvent.BreakEvent> toLua(BlockEvent.BreakEvent javaObj) {
    return new Proxy<>(getConverters(), getMetatable(), javaObj);
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
