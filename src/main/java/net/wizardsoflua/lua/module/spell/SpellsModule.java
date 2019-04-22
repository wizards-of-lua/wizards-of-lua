package net.wizardsoflua.lua.module.spell;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.base.Predicate;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.function.NamedFunction1;
import net.wizardsoflua.spell.SpellEntity;
import net.wizardsoflua.spell.SpellRegistry;

/**
 *
 */
// TODO convert this to a SpellExtension
public class SpellsModule {
  public static SpellsModule installInto(Table env, Converters converters,
      SpellRegistry spellRegistry, SpellEntity spellEntity) {
    SpellsModule result = new SpellsModule(converters, spellRegistry, spellEntity);
    env.rawset("Spells", result.getLuaTable());
    return result;
  }

  private final Converters converters;
  private final SpellRegistry spellRegistry;
  private final SpellEntity spellEntity;
  private final Table luaTable = DefaultTable.factory().newTable();

  public SpellsModule(Converters converters, SpellRegistry spellRegistry, SpellEntity spellEntity) {
    this.converters = converters;
    this.spellRegistry = spellRegistry;
    this.spellEntity = spellEntity;
    FindFunction findFunction = new FindFunction();
    luaTable.rawset(findFunction.getName(), findFunction);
  }

  public Table getLuaTable() {
    return luaTable;
  }

  public @Nullable Iterable<SpellEntity> find(String selector) {
    Predicate<SpellEntity> predicate = byName(selector);
    Iterable<SpellEntity> result = spellRegistry.get(predicate);
    return result;
  }

  public @Nullable Iterable<SpellEntity> find(Table criteria) {
    List<Predicate<SpellEntity>> predicates = new ArrayList<>();
    String name = converters.toJavaNullable(String.class, criteria.rawget("name"), "criteria.name");
    if (name != null) {
      predicates.add(byName(name));
    }
    String owner =
        converters.toJavaNullable(String.class, criteria.rawget("owner"), "criteria.owner");
    if (owner != null) {
      predicates.add(byOwner(owner));
    }
    String tag = converters.toJavaNullable(String.class, criteria.rawget("tag"), "criteria.tag");
    if (tag != null) {
      predicates.add(byTag(tag));
    }
    Number sid = converters.toJavaNullable(Number.class, criteria.rawget("sid"), "criteria.sid");
    if (sid != null) {
      predicates.add(bySid(sid.longValue()));
    }
    Number maxradius =
        converters.toJavaNullable(Number.class, criteria.rawget("maxradius"), "criteria.maxradius");
    if (maxradius != null) {
      predicates.add(byMaxRadius(maxradius.doubleValue()));
    }
    Number minradius =
        converters.toJavaNullable(Number.class, criteria.rawget("minradius"), "criteria.minradius");
    if (minradius != null) {
      predicates.add(byMinRadius(minradius.doubleValue()));
    }
    boolean excludeSelf = converters
        .toJavaOptional(Boolean.class, criteria.rawget("excludeSelf"), "criteria.excludeSelf")
        .orElse(false);
    if (excludeSelf) {
      predicates.add((SpellEntity spell) -> spell != spellEntity);
    }
    return spellRegistry.get(predicates);
  }

  private Predicate<SpellEntity> byName(String name) {
    checkNotNull(name, "name==null!");
    return (SpellEntity spell) -> name.equals(spell.getName());
  }

  private Predicate<SpellEntity> byOwner(String owner) {
    checkNotNull(owner, "owner==null!");
    return (SpellEntity spell) -> spell.getOwner() != null
        && owner.equals(spell.getOwner().getName().getString());
  }

  private Predicate<SpellEntity> byTag(String tag) {
    checkNotNull(tag, "tag==null!");
    return (SpellEntity spell) -> spell.getTags().contains(tag);
  }

  private Predicate<SpellEntity> bySid(long sid) {
    return (SpellEntity spell) -> spell.getSid() == sid;
  }

  private Predicate<SpellEntity> byMaxRadius(double rmax) {
    double rmaxSq = rmax * rmax;
    return (SpellEntity spell) -> spell.getDistanceSq(spellEntity) <= rmaxSq;
  }

  private Predicate<SpellEntity> byMinRadius(double rmin) {
    double rminSq = rmin * rmin;
    return (SpellEntity spell) -> spell.getDistanceSq(spellEntity) >= rminSq;
  }

  private class FindFunction extends NamedFunction1 {
    @Override
    public String getName() {
      return "find";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      Table criteria = converters.toJavaOptional(Table.class, arg1, 1, "criteria", getName())
          .orElse(new DefaultTable());
      Iterable<SpellEntity> result = find(criteria);
      context.getReturnBuffer().setTo(converters.toLua(result));
    }
  }

}
