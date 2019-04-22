package net.wizardsoflua.testenv;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import com.google.common.collect.Iterables;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

public class WolTestBase extends TestDataFactory {
  private WolTestEnvironment testEnv = WolTestEnvironment.instance;
  private MinecraftBackdoor mcBackdoor = new MinecraftBackdoor(testEnv, MinecraftForge.EVENT_BUS);
  private boolean wasOperator;
  private long oldDayTime;

  @BeforeEach
  public void beforeTest() throws Exception {
    testEnv.runAndWait(() -> testEnv.getEventRecorder().setEnabled(true));

    mc().resetClock();
    mc().breakAllSpells();

    mc().clearWizardConfigs();

    mc().gameRules().saveState();
    mc().gameRules().setDoMobSpawning(false);
    mc().gameRules().setLogAdminCommands(false);
    mc().gameRules().setSendCommandFeedback(true);

    mc().executeCommand("/kill @e[type=!Player]");
    mc().executeCommand("/wol spell break all");
    mc().clearEvents();
    mc().clearLuaFunctionCache();
    mc().player().setMainHandItem(null);
    mc().player().setOffHandItem(null);
    wasOperator = mc().player().isOperator();
    oldDayTime = mc().getWorldtime();
  }

  @AfterEach
  public void afterTest() throws Exception {
    testEnv.runAndWait(() -> testEnv.getEventRecorder().setEnabled(false));
    mc().player().setOperator(wasOperator);
    mc().clearEvents();
    mc().breakAllSpells();

    mc().gameRules().restoreState();
    mc().setWorldTime(oldDayTime);

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
    return formatPos(pos.x, pos.y, pos.z);
  }

  protected String format(float value) {
    return String.valueOf(value);
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
