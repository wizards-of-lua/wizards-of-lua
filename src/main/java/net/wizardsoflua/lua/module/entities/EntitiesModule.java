package net.wizardsoflua.lua.module.entities;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.function.NamedFunction1;
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
    FindFunction findFunction = new FindFunction();
    luaTable.rawset(findFunction.getName(), findFunction);
  }

  public Table getLuaTable() {
    return luaTable;
  }

  public @Nullable Iterable<Entity> find(String selector) {
    try {
      List<Entity> list = EntitySelector.<Entity>matchEntities(spellEntity, selector, Entity.class);
      return list;
    } catch (CommandException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  /**
   * Returns the entities matching the given selector.
   */
  private class FindFunction extends NamedFunction1 {
    @Override
    public String getName() {
      return "find";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      String selector = converters.toJava(String.class, arg1, 1, "selector", getName());
      Iterable<Entity> entities = find(selector);
      Table result = converters.toLuaIterable(entities);
      context.getReturnBuffer().setTo(result);
    }
  }
}
