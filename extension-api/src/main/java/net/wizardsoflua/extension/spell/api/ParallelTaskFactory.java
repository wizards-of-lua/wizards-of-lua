package net.wizardsoflua.extension.spell.api;

// FIXME Adrodoc 23.04.2018: @mkarneim What do you think about this interface? Should we call the
// EventsModule directly from SpellProgram as we do in WolEventHandler?
public interface ParallelTaskFactory {
  boolean isFinished();

  void terminate();
}
