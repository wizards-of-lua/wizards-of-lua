package net.wizardsoflua.lua.wrapper.spell;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.wrapper.WrapperFactory;
import net.wizardsoflua.lua.wrapper.entity.EntityWrapper;
import net.wizardsoflua.spell.SpellEntity;

public class SpellWrapper extends EntityWrapper {
  private final SpellEntity delegate;

  public SpellWrapper(WrapperFactory wrappers, SpellEntity delegate) {
    super(wrappers, delegate);
    this.delegate = delegate;
    addReadOnly("owner", this::getOwner);
    setMetatable((Table) wrappers.getEnv().rawget("Spell"));
  }

  public Table getOwner() {
    return getWrappers().wrap(delegate.getOwner());
  }

}
