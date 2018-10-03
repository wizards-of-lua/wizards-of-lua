package net.wizardsoflua.tests;

import org.junit.Before;
import org.junit.Test;

import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

public class SystemTest extends WolTestBase {

  private static final String SOME_TEST_TXT = "some-test.txt";
  private static final String SOME_DIR = "some-dir";

  @Before
  public void cleanUp() {
    mc().deleteWorldFile(SOME_TEST_TXT);
    mc().deleteWorldFile(SOME_DIR);
  }

  // /test net.wizardsoflua.tests.SystemTest test_delete
  @Test
  public void test_delete() {
    // Given:
    String filename = SOME_TEST_TXT;
    String content = "some content";
    this.mc().writeWorldFile(filename, content);

    // When:
    mc().executeCommand("/lua System.delete('%s'); print('ok')", filename);
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");

    // Then:
    assertThat(mc().readWorldFile(filename)).isNull();
  }

  // /test net.wizardsoflua.tests.SystemTest test_makeDir
  @Test
  public void test_makeDir() {
    // Given:
    String filename = SOME_DIR;

    // When:
    mc().executeCommand("/lua System.makeDir('%s'); print('ok')", filename);
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");

    // Then:
    assertThat(mc().existsWorldFile(filename)).isTrue();
  }

  // /test net.wizardsoflua.tests.SystemTest test_isFile
  @Test
  public void test_isFile() {
    // Given:
    String filename = SOME_TEST_TXT;
    String content = "some content";
    this.mc().writeWorldFile(filename, content);

    // When:
    mc().executeCommand("/lua v=System.isFile('%s'); print(v)", filename);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.SystemTest test_isDir
  @Test
  public void test_isDir() {
    // Given:
    String filename = SOME_DIR;

    // When:
    mc().executeCommand("/lua name='%s'; System.makeDir(name); v=System.isDir(name); print(v)",
        filename);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("true");
  }

  // /test net.wizardsoflua.tests.SystemTest test_listFiles
  @Test
  public void test_listFiles() {
    // Given:
    String filename1 = SOME_DIR + "/1.txt";
    String filename2 = SOME_DIR + "/2.txt";
    this.mc().writeWorldFile(filename1, "1");
    this.mc().writeWorldFile(filename2, "2");

    // When:
    mc().executeCommand("/lua v=System.listFiles('%s'); for _,f in pairs(v) do print(f); end",
        SOME_DIR);

    // Then:
    ServerLog4jEvent act1 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act1.getMessage()).isEqualTo("1.txt");
    ServerLog4jEvent act2 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act2.getMessage()).isEqualTo("2.txt");
  }
}
