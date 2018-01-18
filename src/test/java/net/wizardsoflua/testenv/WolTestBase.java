package net.wizardsoflua.testenv;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;

import com.google.common.collect.Iterables;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.wizardsoflua.testenv.event.TestPlayerPreparedForTestEvent;
import net.wizardsoflua.testenv.net.PrepareForTestAction;

public class WolTestBase extends TestDataFactory {
  private static int testIdCount = 0;
  private WolTestEnvironment testEnv = WolTestEnvironment.instance;
  private MinecraftBackdoor mcBackdoor = new MinecraftBackdoor(testEnv, MinecraftForge.EVENT_BUS);;

  @Before
  public void beforeTest() throws IOException {
    testEnv.runAndWait(() -> testEnv.getEventRecorder().setEnabled(true));

    mc().resetClock();
    mc().breakAllSpells();
    int testId = testIdCount++;
    mc().player().perform(new PrepareForTestAction(testId));
    TestPlayerPreparedForTestEvent evt = mc().waitFor(TestPlayerPreparedForTestEvent.class);
    assertThat(evt.getId()).isEqualTo(testId);

    mc().clearWizardConfigs();
    mc().executeCommand("/gamerule logAdminCommands false");
    mc().executeCommand("/gamerule sendCommandFeedback false");
    mc().executeCommand("/gamerule doMobSpawning false");
    mc().executeCommand("/kill @e[type=!Player]");
    mc().clearEvents();
    mc().clearLuaFunctionCache();
    mc().player().setMainHandItem(null);
    mc().player().setOffHandItem(null);
  }

  @After
  public void afterTest() {
    testEnv.runAndWait(() -> testEnv.getEventRecorder().setEnabled(false));
    mc().clearEvents();
    mc().breakAllSpells();
    mc().resetClock();
  }

  protected MinecraftBackdoor mc() {
    return mcBackdoor;
  }

  protected Iterable<String> messagesOf(Iterable<ServerChatEvent> events) {
    return Iterables.transform(events, ServerChatEvent::getMessage);
  }

  protected Iterable<BlockPos> positionsOf(Iterable<RightClickBlock> events) {
    return Iterables.transform(events, RightClickBlock::getPos);
  }

  protected String format(BlockPos pos) {
    return formatPos((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
  }

  protected String format(Vec3d pos) {
    return formatPos(pos.xCoord, pos.yCoord, pos.zCoord);
  }

  protected String formatPos(int x, int y, int z) {
    return "{" + x + ", " + y + ", " + z + "}";
  }

  protected String formatPos(double x, double y, double z) {
    return ("{" + x + ", " + y + ", " + z + "}").replace('E', 'e');
  }

  protected void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
