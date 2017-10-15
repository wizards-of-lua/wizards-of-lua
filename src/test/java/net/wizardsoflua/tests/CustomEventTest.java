package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.util.math.BlockPos;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

@RunWith(MinecraftJUnitRunner.class)
public class CustomEventTest extends WolTestBase {

  // /test net.wizardsoflua.tests.CustomEventTest test_with_primitive_data
  @Test
  public void test_with_primitive_data() {
    // Given:
    String eventName = "my-custom-event-name";
    String message = "hello world!";
    String expected = "received " + message;

    mc().executeCommand("/lua q=Events.connect('%s'); e=q:pop(); print('received '..e.data)",
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
        "/lua q=Events.connect('%s'); e=q:pop(); print('received '..e.data.pos); print('received '..e.data.message)",
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
        "/lua q=Events.connect('%s'); e=q:pop(); for i,d in pairs(e.data) do assert(d==i,d..'=='..i); end; print('ok')",
        eventName);

    // When:
    mc().executeCommand("/lua data={}; for i=1,10 do data[i]=i; end; Events.fire('%s',data)",
        eventName);

    // Then:
    ServerLog4jEvent act = mc().waitFor(ServerLog4jEvent.class);
    assertThat(act.getMessage()).isEqualTo("ok");
  }

}
