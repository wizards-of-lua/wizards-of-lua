package net.wizardsoflua.testenv.junit;

import static java.util.Objects.requireNonNull;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.YELLOW;
import static net.wizardsoflua.CommandSourceUtils.sendAndLogErrorMessage;
import static net.wizardsoflua.CommandSourceUtils.sendAndLogFeedback;
import static net.wizardsoflua.WolAnnouncementMessage.createAnnouncement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.CustomBossEvent;
import net.minecraft.server.CustomBossEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfo.Color;
import net.wizardsoflua.testenv.WolTestMod;

public class WolTestExecutionListener implements TestExecutionListener {
  private static final ResourceLocation BOSSBAR_ID =
      new ResourceLocation(WolTestMod.MODID, "test-progress");
  private final CommandSource source;

  private final AtomicInteger testsFound = new AtomicInteger();
  private final AtomicInteger testsSuccessful = new AtomicInteger();
  private final AtomicInteger testsAborted = new AtomicInteger();
  private final AtomicInteger testsSkipped = new AtomicInteger();
  private final AtomicInteger testsFailed = new AtomicInteger();
  private LocalDateTime start;
  private TestPlan testPlan;

  public WolTestExecutionListener(CommandSource source) {
    this.source = requireNonNull(source, "source");
  }

  private int getTestsFinished() {
    return testsSuccessful.get() + getTestsCanceled() + testsFailed.get();
  }

  private int getTestsCanceled() {
    return testsAborted.get() + testsSkipped.get();
  }

  private ITextComponent appendDetailedTestCount(ITextComponent parent) {
    return parent.appendText("(")
        .appendSibling(new TextComponentString("" + testsSuccessful).applyTextStyle(GREEN))
        .appendText(", ")
        .appendSibling(new TextComponentString("" + getTestsCanceled()).applyTextStyle(YELLOW))
        .appendText(", ")
        .appendSibling(new TextComponentString("" + testsFailed).applyTextStyle(RED))
        .appendText(")");
  }

  @Override
  public void testPlanExecutionStarted(TestPlan testPlan) {
    start = LocalDateTime.now();
    this.testPlan = testPlan;
    int testCount = (int) testPlan.countTestIdentifiers(it -> it.isTest());
    testsFound.addAndGet(testCount);
    updateProgressBar();

    sendAndLogFeedback(source, createAnnouncement("Found " + testCount + " tests..."));
  }

  @Override
  public void testPlanExecutionFinished(TestPlan testPlan) {
    Duration duration = Duration.between(start, LocalDateTime.now());
    ITextComponent message = new TextComponentString("Finished " + testsFound + " ");
    appendDetailedTestCount(message);
    message.appendText(" tests in " + duration.getSeconds() + " seconds");
    sendAndLogFeedback(source, createAnnouncement(message));
    removeProgressBar();
  }

  @Override
  public void dynamicTestRegistered(TestIdentifier testIdentifier) {
    if (testIdentifier.isTest()) {
      testsFound.incrementAndGet();
      updateProgressBar();
    }
  }

  private volatile boolean abort;

  public void onAbort() {
    ITextComponent announcement = createAnnouncement("Aborting test run...");
    announcement.applyTextStyle(YELLOW);
    sendAndLogFeedback(source, announcement);
    abort = true;
  }

  @Override
  public void executionSkipped(TestIdentifier testIdentifier, String reason) {
    Set<TestIdentifier> descendants = testPlan.getDescendants(testIdentifier);
    int tests = (int) Stream.concat(Stream.of(testIdentifier), descendants.stream()) //
        .filter(it -> it.isTest()) //
        .count();

    if (abort) {
      testsAborted.addAndGet(tests);
    } else {
      testsSkipped.addAndGet(tests);
      updateProgressBar();

      String displayName = testIdentifier.getDisplayName();
      ITextComponent announcement =
          createAnnouncement("Skipped " + displayName + " because:\n" + reason);
      announcement.applyTextStyle(YELLOW);
      sendAndLogFeedback(source, announcement);
    }
  }

  @Override
  public void executionStarted(TestIdentifier testIdentifier) {
    if (testIdentifier.isTest()) {
      String displayName = testIdentifier.getDisplayName();
      sendAndLogFeedback(source, createAnnouncement("Started " + displayName));
    }
  }

  @Override
  public void executionFinished(TestIdentifier testIdentifier,
      TestExecutionResult testExecutionResult) {
    if (testIdentifier.isTest()) {
      switch (testExecutionResult.getStatus()) {
        case SUCCESSFUL:
          testsSuccessful.incrementAndGet();
          break;
        case ABORTED:
          testsAborted.incrementAndGet();
          break;
        case FAILED:
          testsFailed.incrementAndGet();

          testExecutionResult.getThrowable().ifPresent(it -> {
            String displayName = testIdentifier.getDisplayName();
            ITextComponent message = createAnnouncement(displayName + " failed:\n" + it.toString());
            sendAndLogErrorMessage(source, message);
          });
          break;
      }
      updateProgressBar();
    }
  }

  private void updateProgressBar() {
    CustomBossEvent progressBar = provideProgressBar();
    int testsFinished = getTestsFinished();
    progressBar.setValue(testsFinished);
    progressBar.setMax(testsFound.get());

    String msg = "Finished " + testsFinished + " / " + testsFound + " tests ";
    ITextComponent name = new TextComponentString(msg);
    appendDetailedTestCount(name);
    progressBar.setName(name);

    Color color;
    if (testsFailed.get() > 0) {
      color = Color.RED;
    } else if (getTestsCanceled() > 0) {
      color = Color.YELLOW;
    } else {
      color = Color.GREEN;
    }
    progressBar.setColor(color);
  }

  private CustomBossEvent provideProgressBar() {
    CustomBossEvent progressBar = getProgressBar();
    if (progressBar == null) {
      progressBar = createProgressBar();
    }
    return progressBar;
  }

  private CustomBossEvent getProgressBar() {
    CustomBossEvents customBossEvents = source.getServer().getCustomBossEvents();
    CustomBossEvent progressBar = customBossEvents.get(BOSSBAR_ID);
    return progressBar;
  }

  private CustomBossEvent createProgressBar() {
    CustomBossEvents customBossEvents = source.getServer().getCustomBossEvents();
    ITextComponent name = new TextComponentString("Test Progress");
    CustomBossEvent progressBar = customBossEvents.add(BOSSBAR_ID, name);
    progressBar.setColor(Color.GREEN);

    Entity entity = source.getEntity();
    if (entity instanceof EntityPlayerMP) {
      EntityPlayerMP player = (EntityPlayerMP) entity;
      progressBar.addPlayer(player);
    }
    return progressBar;
  }

  private void removeProgressBar() {
    CustomBossEvent progressBar = getProgressBar();
    if (progressBar != null) {
      progressBar.removeAllPlayers();
      source.getServer().getCustomBossEvents().remove(progressBar);
    }
  }
}
