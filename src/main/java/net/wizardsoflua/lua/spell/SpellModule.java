package net.wizardsoflua.lua.spell;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.wizardsoflua.spell.SpellEntity;

public class SpellModule {

  public static SpellModule installInto(Table env, SpellEntity spellEntity) {
    SpellModule result = new SpellModule(spellEntity);
    env.rawset("spell", result.getLuaTable());
    return result;
  }

  private final SpellEntity delegate;
  private final Table luaTable = DefaultTable.factory().newTable();

  public SpellModule(SpellEntity delegate) {
    this.delegate = delegate;
  }

  public Table getLuaTable() {
    return luaTable;
  }

}
