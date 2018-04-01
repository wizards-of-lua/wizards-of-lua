package net.wizardsoflua.spell;


import static com.google.common.collect.Lists.transform;
import static java.lang.String.valueOf;
import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpellRegistry {
  private final List<SpellEntity> spells = new CopyOnWriteArrayList<>();

  public void add(SpellEntity spell) {
    spells.add(spell);
  }

  @SubscribeEvent
  public void onEvent(SpellTerminatedEvent evt) {
    spells.remove(evt.getSpell());
  }

  public Collection<SpellEntity> getAll() {
    return unmodifiableCollection(spells);
  }

  public Iterable<SpellEntity> get(Predicate<SpellEntity> predicate) {
    return Iterables.filter(spells, predicate);
  }

  public Iterable<String> getActiveSids() {
    return transform(spells, s -> valueOf(s.getSid()));
  }

  public Iterable<String> getActiveNames() {
    return transform(spells, SpellEntity::getName);
  }
}
