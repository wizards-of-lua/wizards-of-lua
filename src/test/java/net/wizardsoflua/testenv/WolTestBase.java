package net.wizardsoflua.testenv;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import com.google.common.collect.Iterables;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.wizardsoflua.testenv.junit.AbortExtension;
import net.wizardsoflua.testenv.junit.DisabledOnDistCondition;

@ExtendWith(DisabledOnDistCondition.class)
public class WolTestBase extends TestDataFactory {
  private final WolTestenv testenv = WolTestenv.getInstanceForCurrentThread();
  private final EventRecorder eventRecorder = testenv.getEventRecorder();
  private final MinecraftBackdoor mcBackdoor = new MinecraftBackdoor(testenv);
  private boolean wasOperator;
  private long oldDayTime;
  @RegisterExtension
  AbortExtension abortExtension = testenv.getAbortExtension();

  @BeforeEach
  void beforeEach() throws Exception {
    mc().breakAllSpells();
    testenv.getLuaFunctionBinaryCache().clear();
    mc().clearWizardConfigs();

    mc().resetClock();
    oldDayTime = mc().getDayTime();

    mc().player().setMainHandItem(null);
    mc().player().setOffHandItem(null);
    mc().player().clearInventory();
    wasOperator = mc().player().isOperator();

    mc().gameRules().saveState();
    mc().gameRules().setDoMobLoot(false);
    mc().gameRules().setDoMobSpawning(false);
    mc().gameRules().setLogAdminCommands(false);

    mc().gameRules().setSendCommandFeedback(false);
    mc().executeCommand("kill @e[type=!player]");
    mc().gameRules().setSendCommandFeedback(true);

    eventRecorder.setEnabled(true);
    testenv.waitForSyncedClient();
    eventRecorder.clear();
  }

  @AfterEach
  void afterEach() throws Exception {
    eventRecorder.setEnabled(false);
    eventRecorder.clear();

    mc().breakAllSpells();

    mc().gameRules().restoreState();

    mc().player().setOperator(wasOperator);

    mc().setDayTime(oldDayTime);
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

  protected String toJsonString(String text) {
    return "{\"text\":\"" + text + "\"}";
  }

  protected String quoteJson(String text) {
    return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
  }
}
