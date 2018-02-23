package net.wizardsoflua.gist;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class GistRepoTest extends Assertions {

  private GistRepo underTest = new GistRepo();

  // @Test
  // public void test_getGistFiles_with_access_token() throws Exception {
  // // Given:
  // // Tetris
  // String url = "https://gist.github.com/mkarneim/a86a1e7a6c02fbd850d2ef4d4b618fb3";
  // String accessToken = "???";
  //
  // // When:
  // List<GistFile> act = underTest.getGistFiles(url, accessToken);
  //
  // // Then:
  // assertThat(act).isNotEmpty();
  // }

  @Test
  public void test_getGistFiles_without_access_token() throws Exception {
    // Given:
    // Tetris
    String url = "https://gist.github.com/mkarneim/a86a1e7a6c02fbd850d2ef4d4b618fb3";
    String accessToken = null;

    // When:
    List<GistFile> act = underTest.getGistFiles(url, accessToken);

    // Then:
    assertThat(act).isNotEmpty();
  }

  // @Test
  // public void test_getRateLimitRemaining_with_access_token() throws Exception {
  // // Given:
  // // Tetris
  // String accessToken = "???";
  //
  // // When:
  // RateLimit act = underTest.getRateLimitRemaining(accessToken);
  // System.out.println(act);
  //
  // // Then:
  // assertThat(act.remaining).isGreaterThan(0);
  // }

  @Test
  public void test_getRateLimitRemaining_without_access_token() throws Exception {
    // Given:
    // Tetris
    String accessToken = null;

    // When:
    RateLimit act = underTest.getRateLimitRemaining(accessToken);
    System.out.println(act);

    // Then:
    assertThat(act.remaining).isGreaterThan(0);
  }
}
