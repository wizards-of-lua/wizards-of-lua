package net.wizardsoflua.lua.module.spell;

import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.wrapper.Wrappers;
import net.wizardsoflua.spell.SpellEntity;

public class SpellModule {

  public static void installInto(Table env, Wrappers wrappers, SpellEntity spellEntity) {
    env.rawset("spell", wrappers.wrap(spellEntity));
  }

}
