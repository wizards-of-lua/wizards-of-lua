package net.wizardsoflua.spell;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * The {@link SpellTerminatedEvent} is fired just before a spell is broken.
 * <p>
 * A spell can register for its own break event and do some clean-up before it is going to be
 * terminated.
 */
public class SpellTerminatedEvent extends Event {

  private final SpellEntity spell;

  public SpellTerminatedEvent(SpellEntity spell) {
    this.spell = spell;
  }

  public SpellEntity getSpell() {
    return spell;
  }

}
