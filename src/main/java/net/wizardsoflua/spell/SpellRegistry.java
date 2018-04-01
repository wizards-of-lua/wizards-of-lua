package net.wizardsoflua.spell;

import static com.google.common.collect.Lists.transform;
import static java.lang.String.valueOf;
import static java.util.Collections.unmodifiableCollection;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpellRegistry {
  private final List<SpellEntity> spells = new CopyOnWriteArrayList<>();

  public void add(SpellEntity spell) {
    spells.add(spell);
  }

  public Iterable<String> getActiveSids() {
    return transform(spells, (s) -> valueOf(s.getSid()));
  }

  public Iterable<String> getActiveNames() {
    return new HashSet<>(transform(spells, SpellEntity::getName));
  }

  public void breakAll() {
    for (SpellEntity spellEntity : spells) {
      spellEntity.setDead();
    }
    if (spells.size() > 0) {
      throw new IllegalStateException("Couldn't break all spells!");
    }
  }

  public int breakByName(String name) {
    int result = 0;
    for (SpellEntity spellEntity : spells) {
      if (name.equals(spellEntity.getName())) {
        spellEntity.setDead();
        result++;
      }
    }
    return result;
  }

  public int breakByOwner(String ownerName) {
    int result = 0;
    for (SpellEntity spellEntity : spells) {
      Entity owner = spellEntity.getOwnerEntity();
      if (owner != null && ownerName.equals(owner.getName())) {
        spellEntity.setDead();
        result++;
      }
    }
    return result;
  }

  public boolean breakBySid(long sid) {
    for (SpellEntity spellEntity : spells) {
      if (spellEntity.getSid() == sid) {
        spellEntity.setDead();
        return true;
      }
    }
    return false;
  }

  public Iterable<SpellEntity> getAll() {
    return unmodifiableCollection(spells);
  }

  @SubscribeEvent
  public void onEvent(SpellTerminatedEvent evt) {
    spells.remove(evt.getSpell());
  }

}
