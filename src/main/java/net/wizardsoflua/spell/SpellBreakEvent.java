package net.wizardsoflua.spell;

import net.minecraftforge.eventbus.api.Event;

/**
 * The {@link SpellBreakEvent} is fired just before a spell is broken.
 * <p>
 * A spell can intercept its own break event and do some clean-up before it is going to be
 * terminated.
 */
public class SpellBreakEvent extends Event {

  private final SpellEntity spell;

  public SpellBreakEvent(SpellEntity spell) {
    this.spell = spell;
  }

  public SpellEntity getSpell() {
    return spell;
  }

}
