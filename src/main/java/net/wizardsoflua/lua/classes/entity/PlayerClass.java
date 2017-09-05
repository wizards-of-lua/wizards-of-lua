package net.wizardsoflua.lua.classes.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;

public class PlayerClass {
  public static final String METATABLE_NAME = "Player";

  private final Converters converters;
  private final Table metatable;

  public PlayerClass(Converters converters) {
    this.converters = converters;
    // TODO do declaration outside this class
    this.metatable = converters.getTypes().declare(METATABLE_NAME, EntityClass.METATABLE_NAME);
  }

  public Table toLua(EntityPlayer delegate) {
    return new Proxy(converters, metatable, delegate);
  }

  public class Proxy extends EntityClass.Proxy {

    private final EntityPlayer delegate;

    public Proxy(Converters converters, Table metatable, EntityPlayer delegate) {
      super(converters, metatable, delegate);
      this.delegate = delegate;

      // Overwrite name, since player names can't be changed
      addReadOnly("name", this::getName);
    }

  }
}
