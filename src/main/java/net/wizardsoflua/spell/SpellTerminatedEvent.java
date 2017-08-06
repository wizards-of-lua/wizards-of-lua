package net.wizardsoflua.spell;

import net.minecraftforge.fml.common.eventhandler.Event;

public class SpellTerminatedEvent extends Event {

  private final SpellEntity spell;

  public SpellTerminatedEvent(SpellEntity spell) {
    this.spell = spell;
  }

  public SpellEntity getSpell() {
    return spell;
  }

}
