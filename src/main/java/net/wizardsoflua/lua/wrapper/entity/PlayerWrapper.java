package net.wizardsoflua.lua.wrapper.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.wrapper.WrapperFactory;

public class PlayerWrapper extends EntityWrapper {
  private final EntityPlayer delegate;

  public PlayerWrapper(WrapperFactory wrappers, EntityPlayer delegate) {
    super(wrappers, delegate);
    this.delegate = delegate;
    setMetatable((Table) wrappers.getEnv().rawget("Player"));
  }

}
