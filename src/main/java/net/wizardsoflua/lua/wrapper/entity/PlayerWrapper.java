package net.wizardsoflua.lua.wrapper.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.wrapper.WrapperFactory;

public class PlayerWrapper extends EntityWrapper {

  public static final String METATABLE_NAME = "Player";

  private final EntityPlayer delegate;

  public PlayerWrapper(WrapperFactory wrappers, EntityPlayer delegate) {
    super(wrappers, delegate);
    this.delegate = delegate;

    // Overwrite name, since player names can't be changed
    addReadOnly("name", this::getName);

    setMetatable((Table) wrappers.getEnv().rawget(METATABLE_NAME));
  }

}
