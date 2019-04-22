package net.wizardsoflua.tests;

import org.junit.jupiter.api.Test;
import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

public class CustomEventTest extends WolTestBase {

  // /test net.wizardsoflua.tests.CustomEventTest test_with_primitive_data
  @Test
  public void test_with_primitive_data() {
    // Given:
    String eventName = "my-custom-event-name";
    String message = "hello world!";
    String expected = "received " + message;

    mc().executeCommand("/lua q=Events.collect('%s'); e=q:next(); print('received '..e.data)",
        eventName);

    // When:
    mc().executeCommand("/lua Events.fire('%s','%s')", eventName, message);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.CustomEventTest test_with_complex_data
  @Test
  public void test_with_complex_data() {
    // Given:
    BlockPos posP = new BlockPos(1, 4, 1);
    String eventName = "my-custom-event-name";
    String message = "hello world!";

    String expected1 = "received {1, 4, 1}";
    String expected2 = "received " + message;

    mc().executeCommand(
        "/lua q=Events.collect('%s'); e=q:next(); print('received '..e.data.pos); print('received '..e.data.message)",
        eventName);

    // When:
    mc().executeCommand("/lua Events.fire('%s',{message='%s',pos=Vec3(%s,%s,%s)})", eventName,
        message, posP.getX(), posP.getY(), posP.getZ());

    // Then:
    ServerLog4jEvent act1 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act1.getMessage()).isEqualTo(expected1);
    ServerLog4jEvent act2 = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act2.getMessage()).isEqualTo(expected2);
  }

  // /test net.wizardsoflua.tests.CustomEventTest test_complex_data_preserves_order
  @Test
  public void test_complex_data_preserves_order() {
    // Given:
    String eventName = "my-custom-event-name";

    mc().executeCommand(
        "/lua q=Events.collect('%s'); e=q:next(); for i,d in pairs(e.data) do assert(d==i,d..'=='..i); end; print('ok')",
        eventName);

    // When:
    mc().executeCommand("/lua data={}; for i=1,10 do data[i]=i; end; Events.fire('%s',data)",
        eventName);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
  }

  // /test net.wizardsoflua.tests.CustomEventTest test_complex_data_is_writable
  @Test
  public void test_complex_data_is_writable() {
    // Given:
    String eventName = "my-custom-event-name";

    mc().executeCommand(
        "/lua q=Events.collect('%s'); e=q:next(); e.data.key=2345; print(e.data.key)", eventName);

    // When:
    mc().executeCommand("/lua data={key=1234}; Events.fire('%s',data)", eventName);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("2345");
  }

  // /test net.wizardsoflua.tests.CustomEventTest test_forward_event_with_modified_complex_data
  @Test
  public void test_forward_event_with_modified_complex_data() {
    // Given:
    String eventName1 = "my-custom-event-name-1";
    String eventName2 = "my-custom-event-name-2";

    mc().executeCommand(
        "/lua q=Events.collect('%s'); e=q:next(); e.data.key=2345; Events.fire('%s', e)",
        eventName1, eventName2);

    mc().executeCommand("/lua q=Events.collect('%s'); e=q:next(); print(e.data.data.key)",
        eventName2);

    // When:
    mc().executeCommand("/lua data={key=1234}; Events.fire('%s',data)", eventName1);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("2345");
  }

}
