package net.wizardsoflua.lua.scheduling;

import com.google.common.base.Preconditions;

import net.sandius.rembulan.runtime.SchedulingContext;
import net.sandius.rembulan.runtime.SchedulingContextFactory;

public class SpellSchedulingContextFactory implements SchedulingContextFactory {

  private SpellSchedulingConfig config;

  public SpellSchedulingContextFactory(SpellSchedulingConfig config) {
    setConfig(config);
  }

  public SpellSchedulingConfig getConfig() {
    return config;
  }

  public void setConfig(SpellSchedulingConfig config) {
    this.config = Preconditions.checkNotNull(config, "config==null!");
  }

  @Override
  public SchedulingContext newInstance() {
    return new SpellSchedulingContext(config);
  }

}
