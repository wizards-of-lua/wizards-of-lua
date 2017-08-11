package net.wizardsoflua.spell;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpellRegistry {
  private final List<SpellEntity> spells = new CopyOnWriteArrayList<>();

  public void add(SpellEntity spell) {
    spells.add(spell);
  }

  public void breakAll() {
    for (SpellEntity spellEntity : spells) {
      spellEntity.setDead();
    }
    if ( spells.size()>0) {
      throw new IllegalStateException("Couldn't break all spells!");
    }
  }

  public Iterable<SpellEntity> getAll() {
    return Collections.unmodifiableCollection(spells);
  }

  @SubscribeEvent
  public void onEvent(SpellTerminatedEvent evt) {
    spells.remove(evt.getSpell());
  }

}
