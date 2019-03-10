package net.wizardsoflua.spell;

import net.minecraftforge.eventbus.api.Event;

public class SpellTerminatedEvent extends Event {

  private final SpellEntity spell;

  public SpellTerminatedEvent(SpellEntity spell) {
    this.spell = spell;
  }

  public SpellEntity getSpell() {
    return spell;
  }

}
