package net.wizardsoflua.brigadier.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class LongArgumentType implements ArgumentType<Long> {
  private final long minimum;
  private final long maximum;

  private LongArgumentType(long minimum, long maximum) {
    this.minimum = minimum;
    this.maximum = maximum;
  }

  public static LongArgumentType Long() {
    return Long(Long.MIN_VALUE);
  }

  public static LongArgumentType Long(long min) {
    return Long(min, Long.MAX_VALUE);
  }

  public static LongArgumentType Long(long min, long max) {
    return new LongArgumentType(min, max);
  }

  public static long getLong(CommandContext<?> context, String name) {
    return context.getArgument(name, long.class);
  }

  public long getMinimum() {
    return minimum;
  }

  public long getMaximum() {
    return maximum;
  }

  @Override
  public <S> Long parse(StringReader reader) throws CommandSyntaxException {
    int start = reader.getCursor();
    long result = readLong(reader);
    if (result < minimum) {
      reader.setCursor(start);
      throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().createWithContext(reader,
          result, minimum);
    }
    if (result > maximum) {
      reader.setCursor(start);
      throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh().createWithContext(reader,
          result, maximum);
    }
    return result;
  }

  private long readLong(StringReader reader) throws CommandSyntaxException {
    int start = reader.getCursor();
    while (reader.canRead() && StringReader.isAllowedNumber(reader.peek())) {
      reader.skip();
    }
    String number = reader.getString().substring(start, reader.getCursor());
    if (number.isEmpty()) {
      throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedInt()
          .createWithContext(reader);
    }
    try {
      return Long.parseLong(number);
    } catch (NumberFormatException ex) {
      reader.setCursor(start);
      throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(reader,
          number);
    }
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (maximum ^ maximum >>> 32);
    result = prime * result + (int) (minimum ^ minimum >>> 32);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    LongArgumentType other = (LongArgumentType) obj;
    if (maximum != other.maximum) {
      return false;
    }
    if (minimum != other.minimum) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    if (minimum == Long.MIN_VALUE && maximum == Long.MAX_VALUE) {
      return "Long()";
    } else if (maximum == Long.MAX_VALUE) {
      return "Long(" + minimum + ")";
    } else {
      return "Long(" + minimum + ", " + maximum + ")";
    }
  }
}
