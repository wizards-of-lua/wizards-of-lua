package net.wizardsoflua.lua.wrapper.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.wrapper.Wrappers;

public class PlayerWrapper {
  public static final String METATABLE_NAME = "Player";

  private final Wrappers wrappers;
  private final Table metatable;

  public PlayerWrapper(Wrappers wrappers) {
    this.wrappers = wrappers;
    // TODO do declaration outside this class
    this.metatable = wrappers.getTypes().declare(METATABLE_NAME, EntityWrapper.METATABLE_NAME);
  }

  public Table wrap(EntityPlayer delegate) {
    return new Proxy(wrappers, metatable, delegate);
  }

  public class Proxy extends EntityWrapper.Proxy {

    private final EntityPlayer delegate;

    public Proxy(Wrappers wrappers, Table metatable, EntityPlayer delegate) {
      super(wrappers, metatable, delegate);
      this.delegate = delegate;

      // Overwrite name, since player names can't be changed
      addReadOnly("name", this::getName);
    }

  }
}
