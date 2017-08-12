package net.wizardsoflua.lua.wrapper.spell;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
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
    addReadOnly("block", this::getBlock);
    add("visible", this::isVisible, this::setVisible);
    setMetatable((Table) wrappers.getEnv().rawget("Spell"));
  }

  public Table getOwner() {
    return getWrappers().wrap(delegate.getOwner());
  }

  public Table getBlock() {
    BlockPos pos = new BlockPos(delegate.getPositionVector());
    IBlockState blockState = delegate.getEntityWorld().getBlockState(pos);
    return getWrappers().wrap(blockState);
  }

  public void setVisible(Object luaObj) {
    boolean value =
        checkNotNull(getWrappers().unwrapBoolean(luaObj), "Expected boolean but got nil!");
    delegate.setVisible(value);
  }

  public boolean isVisible() {
    return delegate.isVisible();
  }

}
