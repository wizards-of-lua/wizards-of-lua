package net.karneim.luamod.lua.classes.util;

import net.karneim.luamod.lua.classes.LuaModule;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable.Builder;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.minecraft.util.DamageSource;
import net.sandius.rembulan.Table;

@LuaModule("DamageSource")
public class DamageSourceClass extends DelegatingLuaClass<DamageSource> {
  public DamageSourceClass(LuaTypesRepo repo) {
    super(repo);
  }

  @Override
  protected void addProperties(Builder<? extends DamageSource> b, DamageSource delegate) {
    b.addReadOnly("canHarmInCreative", delegate::canHarmInCreative);
    b.addReadOnly("damageLocation", delegate::getDamageLocation);
    b.addReadOnly("damageType", delegate::getDamageType);
    b.addReadOnly("hungerDamage", delegate::getHungerDamage);
    b.addReadOnly("isCreativePlayer", delegate::isCreativePlayer);
    b.addReadOnly("isDamageAbsolute", delegate::isDamageAbsolute);
    b.addReadOnly("isDifficultyScaled", delegate::isDifficultyScaled);
    b.addReadOnly("isExplosion", delegate::isExplosion);
    b.addReadOnly("isFireDamage", delegate::isFireDamage);
    b.addReadOnly("isMagicDamage", delegate::isMagicDamage);
    b.addReadOnly("isProjectile", delegate::isProjectile);
    b.addReadOnly("isUnblockable", delegate::isUnblockable);
  }

  @Override
  protected void addFunctions(Table luaClass) {}
}
