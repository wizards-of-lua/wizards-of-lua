package net.wizardsoflua.annotation.processor;

import com.google.common.collect.ImmutableSet;

public class MultipleProcessingExceptions extends Exception {
  private static final long serialVersionUID = 1L;
  private final ImmutableSet<ProcessingException> exceptions;

  public MultipleProcessingExceptions(Iterable<ProcessingException> exceptions) {
    this.exceptions = ImmutableSet.copyOf(exceptions);
  }

  /**
   * @return the value of {@link #exceptions}
   */
  public ImmutableSet<ProcessingException> getExceptions() {
    return exceptions;
  }
}
