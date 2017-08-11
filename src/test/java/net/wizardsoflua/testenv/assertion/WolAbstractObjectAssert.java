package net.wizardsoflua.testenv.assertion;

import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.description.Description;
import org.assertj.core.description.TextDescription;

public abstract class WolAbstractObjectAssert<S extends WolAbstractObjectAssert<S, A>, A>
    extends AbstractObjectAssert<S, A> {

  public WolAbstractObjectAssert(A actual, Class<?> selfType) {
    super(actual, selfType);
  }

  protected Description description(String description, Object... args) {
    if (descriptionText() != null && !descriptionText().isEmpty()) {
      return new TextDescription(descriptionText() + "." + description, args);
    } else {
      return new TextDescription(description, args);
    }
  }

}
