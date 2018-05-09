package net.wizardsoflua.lua.classes.common;

import static java.util.Objects.requireNonNull;

public class ModifiableDelegator<D> implements Delegator<D> {
  protected D delegate;

  public ModifiableDelegator(D delegate) {
    setDelegate(delegate);
  }

  /**
   * @return the value of {@link #delegate}
   */
  @Override
  public D getDelegate() {
    return delegate;
  }

  /**
   * @param delegate the new value for {@link #delegate}
   */
  public void setDelegate(D delegate) {
    this.delegate = requireNonNull(delegate, "delegate == null!");
  }
}
