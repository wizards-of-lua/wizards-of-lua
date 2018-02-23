package net.wizardsoflua.gist;

/**
 * Response of a rate limit call.
 * 
 * @see https://developer.github.com/v3/rate_limit/
 */
public class RateLimit {
  public final int limit;
  public final int remaining;
  public final long reset;

  public RateLimit(int limit, int remaining, long reset) {
    this.limit = limit;
    this.remaining = remaining;
    this.reset = reset;
  }

  @Override
  public String toString() {
    return "RateLimit [limit=" + limit + ", remaining=" + remaining + ", reset=" + reset + "]";
  }

}
