package net.wizardsoflua.lua.wrapper.entity;

import net.minecraft.entity.Entity;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.wrapper.WrapperFactory;
import net.wizardsoflua.lua.wrapper.common.DelegatingWrapper;

public class EntityWrapper extends DelegatingWrapper {
  private final Entity delegate;

  public EntityWrapper(WrapperFactory wrappers, Entity delegate) {
    super(wrappers, delegate);
    this.delegate = delegate;
    addReadOnly("pos", this::getPos);
    addReadOnly("name", this::getName);
    setMetatable((Table) wrappers.getEnv().rawget("Entity"));
  }

  public Table getPos() {
    return getWrappers().wrap(delegate.getPositionVector());
  }

  public ByteString getName() {
    return getWrappers().wrap(delegate.getName());
  }

}
