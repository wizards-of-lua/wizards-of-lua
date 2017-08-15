package net.wizardsoflua.lua.module.spell;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.converters.Converters;
import net.wizardsoflua.spell.SpellEntity;

public class SpellModule {

  public static void installInto(Table env, Converters converters, SpellEntity spellEntity) {
    env.rawset("spell", converters.entityToLua(spellEntity));
  }

}
