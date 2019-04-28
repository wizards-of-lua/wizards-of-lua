package net.wizardsoflua.extension.spell.api.resource;

import net.wizardsoflua.extension.api.resource.RealTime;

public interface Time extends RealTime {
  long getGameTime();
}
