package net.wizardsoflua.lua.classes.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.Converters;

public class CreatureClass {
  public static final String METATABLE_NAME = "Creature";

  private final Converters converters;
  private final Table metatable;

  public CreatureClass(Converters converters) {
    this.converters = converters;
    // TODO do declaration outside this class
    this.metatable = converters.getTypes().declare(METATABLE_NAME, EntityClass.METATABLE_NAME);
  }

  public Table toLua(EntityLivingBase delegate) {
    return new Proxy(converters, metatable, delegate);
  }

  public static class Proxy extends EntityClass.Proxy {

    private final EntityLivingBase delegate;

    public Proxy(Converters converters, Table metatable, EntityLivingBase delegate) {
      super(converters, metatable, delegate);
      this.delegate = delegate;
    }

    @Override
    public double getRotationYaw() {
      float v = delegate.renderYawOffset;
      return MathHelper.wrapDegrees(v);
    }

  }

}
