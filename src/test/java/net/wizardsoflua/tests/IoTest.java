package net.wizardsoflua.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import net.wizardsoflua.testenv.WolTestBase;

public class IoTest extends WolTestBase {

  private static final String SOME_TEST_TXT = "some-test.txt";
  private static final String SOME_DIR = "some-dir";

  @BeforeEach
  public void cleanUp() {
    mc().deleteWorldFile(SOME_TEST_TXT);
    mc().deleteWorldFile(SOME_DIR);
  }

  // /test net.wizardsoflua.tests.IoTest test_open_for_read
  @Test
  public void test_open_for_read() {
    // Given:
    String filename = SOME_TEST_TXT;
    String content = "some content";
    mc().writeWorldFile(filename, content);

    // When:
    mc().executeCommand("/lua f,err=io.open('%s','r'); print(f~=nil); f:close()", filename);

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.IoTest test_open_for_read_and_read_all
  @Test
  public void test_open_for_read_and_read_all() {
    // Given:
    String filename = SOME_TEST_TXT;
    String content = "some line\nanother line";
    mc().writeWorldFile(filename, content);

    // When:
    mc().executeCommand("/lua f,err=io.open('%s','r'); v=f:read('*a'); print(v); f:close()",
        filename);

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo(content);
  }

  // /test net.wizardsoflua.tests.IoTest test_open_for_write
  @Test
  public void test_open_for_write() {
    // Given:
    String filename = SOME_TEST_TXT;

    // When:
    mc().executeCommand("/lua f,err=io.open('%s','w'); print(f~=nil); f:close()", filename);

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.IoTest test_open_for_write_and_write_text
  @Test
  public void test_open_for_write_and_write_text() {
    // Given:
    String filename = SOME_TEST_TXT;
    String content = "some line\nanother line";

    // When:
    mc().executeCommand("/lua f,err=io.open('%s','w'); f:write([[%s]]); f:close(); print('ok');",
        filename, content);

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("ok");
    String actual = mc().readWorldFile(SOME_TEST_TXT);
    assertThat(actual).isEqualTo(content);
  }

}
