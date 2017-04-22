package net.karneim.luamod.lua.event;

import static net.karneim.luamod.lua.wrapper.WrapperFactory.wrap;

import javax.annotation.Nullable;

import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.sandius.rembulan.Table;

public class ClickWindowEventWrapper<E extends ClickWindowEvent> extends EventWrapper<E> {
  public ClickWindowEventWrapper(LuaTypesRepo repo, @Nullable E event, EventType eventType,
      Table metatable) {
    super(repo, event, eventType.name(), metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder builder) {
    super.addProperties(builder);
    builder.addNullable("clickedItem", wrap(getRepo(), delegate.getClickedItem()));
    builder.addNullable("clickType", wrap(getRepo(), delegate.getClickType()));
    builder.addNullable("player", wrap(getRepo(), delegate.getPlayer()));
    builder.addNullable("slotId", delegate.getSlotId());
  }
}
