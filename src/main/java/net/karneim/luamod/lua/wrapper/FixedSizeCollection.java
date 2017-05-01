package net.karneim.luamod.lua.wrapper;

public interface FixedSizeCollection<E, D> {
  E getAt(int i);

  void setAt(int i, E element);

  int getLength();

  D getDelegate();
}
