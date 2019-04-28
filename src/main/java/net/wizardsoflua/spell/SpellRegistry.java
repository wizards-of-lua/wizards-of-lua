package net.wizardsoflua.spell;

import static com.google.common.collect.Lists.transform;
import static java.lang.String.valueOf;
import static java.util.Collections.unmodifiableCollection;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.wizardsoflua.ServerScoped;
import net.wizardsoflua.extension.api.inject.PostConstruct;
import net.wizardsoflua.extension.api.inject.PreDestroy;

@ServerScoped
public class SpellRegistry {
  private final List<SpellEntity> spells = new CopyOnWriteArrayList<>();

  @PostConstruct
  private void postConstruct() {
    MinecraftForge.EVENT_BUS.register(this);
  }

  @PreDestroy
  private void preDestroy() {
    MinecraftForge.EVENT_BUS.unregister(this);
  }

  @SubscribeEvent
  public void onEvent(SpellTerminatedEvent evt) {
    spells.remove(evt.getSpell());
  }

  public void add(SpellEntity spell) {
    spells.add(spell);
  }

  public Collection<SpellEntity> getAll() {
    return unmodifiableCollection(spells);
  }

  public Iterable<SpellEntity> get(Predicate<SpellEntity> predicate) {
    return Iterables.filter(spells, predicate);
  }

  public Iterable<SpellEntity> get(List<Predicate<SpellEntity>> predicates) {
    Iterable<SpellEntity> result = spells;
    for (Predicate<SpellEntity> predicate : predicates) {
      result = Iterables.filter(result, predicate);
    }
    return result;
  }

  public Iterable<String> getActiveSids() {
    return transform(spells, s -> valueOf(s.getSid()));
  }

  public Iterable<String> getActiveNames() {
    return transform(spells, SpellEntity::getName);
  }
}
