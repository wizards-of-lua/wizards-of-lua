package net.wizardsoflua.gist;

import java.net.URL;

public class RequestRateLimitExceededException extends Exception {

  private static final long serialVersionUID = 1L;
  private final URL url;
  private final RateLimit rateLimit;
  private final boolean requestWasAuthorized;

  public RequestRateLimitExceededException(URL url, RateLimit rateLimit,
      boolean requestWasAuthorized) {
    super("The REST call to " + url.toExternalForm() + " has exceeded the request rate limit!");
    this.url = url;
    this.rateLimit = rateLimit;
    this.requestWasAuthorized = requestWasAuthorized;
  }

  public URL getUrl() {
    return url;
  }

  public RateLimit getRateLimit() {
    return rateLimit;
  }

  public boolean requestWasAuthorized() {
    return requestWasAuthorized;
  }
}
