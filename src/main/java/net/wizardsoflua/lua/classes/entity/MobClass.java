package net.wizardsoflua.lua.classes.entity;

import net.minecraft.entity.EntityLiving;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.module.types.Terms;

public class MobClass {
  public static final String METATABLE_NAME = "Mob";

  private final Converters converters;
  private final Table metatable;

  public MobClass(Converters converters) {
    this.converters = converters;
    // TODO do declaration outside this class
    this.metatable = converters.getTypes().declare(METATABLE_NAME, CreatureClass.METATABLE_NAME);
  }

  public Table toLua(EntityLiving delegate) {
    return new Proxy(converters, metatable, delegate);
  }

  public static class Proxy extends CreatureClass.Proxy {

    private final EntityLiving delegate;

    public Proxy(Converters converters, Table metatable, EntityLiving delegate) {
      super(converters, metatable, delegate);
      this.delegate = delegate;
      add("ai", this::getAi, this::setAi);
    }

    public boolean getAi() {
      return !delegate.isAIDisabled();
    }

    public void setAi(Object luaObj) {
      boolean enabled = getConverters().getTypes().castBoolean(luaObj, Terms.MANDATORY);
      delegate.setNoAI(!enabled);
    }

  }

}
