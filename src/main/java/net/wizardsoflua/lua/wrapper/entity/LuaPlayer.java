package net.wizardsoflua.lua.wrapper.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.wrapper.WrapperFactory;

public class LuaPlayer {
  public static final String METATABLE_NAME = "Player";

  private final WrapperFactory wrappers;
  private final Table metatable;

  public LuaPlayer(WrapperFactory wrappers) {
    this.wrappers = wrappers;
    // TODO do declaration outside this class
    this.metatable = wrappers.getTypes().declare(METATABLE_NAME, LuaEntity.METATABLE_NAME);
  }

  public Table wrap(EntityPlayer delegate) {
    return new Wrapper(wrappers, metatable, delegate);
  }

  public class Wrapper extends LuaEntity.Wrapper {

    private final EntityPlayer delegate;

    public Wrapper(WrapperFactory wrappers, Table metatable, EntityPlayer delegate) {
      super(wrappers, metatable, delegate);
      this.delegate = delegate;

      // Overwrite name, since player names can't be changed
      addReadOnly("name", this::getName);
    }

  }
}
