package net.wizardsoflua.lua.module.entities;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.module.types.Terms;
import net.wizardsoflua.spell.SpellEntity;

public class EntitiesModule {
  public static EntitiesModule installInto(Table env, Converters converters,
      SpellEntity spellEntity) {
    EntitiesModule result = new EntitiesModule(converters, spellEntity);
    env.rawset("Entities", result.getLuaTable());
    return result;
  }

  private final Converters converters;
  private final SpellEntity spellEntity;
  private final Table luaTable = DefaultTable.factory().newTable();

  public EntitiesModule(Converters converters, SpellEntity spellEntity) {
    this.converters = converters;
    this.spellEntity = spellEntity;
    luaTable.rawset("find", new FindFunction());
  }

  public Table getLuaTable() {
    return luaTable;
  }

  public @Nullable Iterable<Entity> find(String target) {
    try {
      List<Entity> list = EntitySelector.<Entity>matchEntities(spellEntity, target, Entity.class);
      return list;
    } catch (CommandException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  /**
   * Returns the entities matching the given selector.
   */
  private class FindFunction extends AbstractFunction1 {
    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      String target = converters.getTypes().castString(arg1, Terms.MANDATORY);
      Iterable<Entity> entities = find(target);
      context.getReturnBuffer().setTo(converters.entitiesToLua(entities));
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }
}
