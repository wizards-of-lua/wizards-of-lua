package net.wizardsoflua.testenv.junit;

import static java.util.Objects.requireNonNull;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.YELLOW;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.BossInfo.Color;
import net.wizardsoflua.testenv.WolTestEnvironment;

public class WolTestExecutionListener implements TestExecutionListener {
  private static final ResourceLocation BOSSBAR_ID =
      new ResourceLocation(WolTestEnvironment.MODID, "test-progress");
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

  @Override
  public void testPlanExecutionStarted(TestPlan testPlan) {
    start = LocalDateTime.now();
    this.testPlan = testPlan;
    int testCount = (int) testPlan.countTestIdentifiers(it -> it.isTest());
    testsFound.addAndGet(testCount);
    updateProgressBar();

    ITextComponent announcement = createAnnouncement("Found " + testCount + " tests...");
    source.sendFeedback(announcement, false);
  }

  @Override
  public void testPlanExecutionFinished(TestPlan testPlan) {
    Duration duration = Duration.between(start, LocalDateTime.now());
    ITextComponent message = new TextComponentString(
        "Finished " + testsFound + " tests in " + duration.getSeconds() + " seconds:\n");
    if (testPlan.containsTests()) {
      if (testsSuccessful.get() != 0) {
        appendSibling(message, " [" + testsSuccessful + "] tests successful", GREEN);
      }
      if (testsAborted.get() != 0) {
        appendSibling(message, " [" + testsAborted + "] tests aborted", YELLOW);
      }
      if (testsSkipped.get() != 0) {
        appendSibling(message, " [" + testsSkipped + "] tests skipped", YELLOW);
      }
      if (testsFailed.get() != 0) {
        appendSibling(message, " [" + testsFailed + "] tests failed", RED);
      }
    } else {
      appendSibling(message, " No tests found", YELLOW);
    }
    ITextComponent announcement = createAnnouncement(message);
    source.sendFeedback(announcement, false);

    removeProgressBar();
  }

  private ITextComponent appendSibling(ITextComponent message, String sibling,
      TextFormatting style) {
    return message.appendSibling(new TextComponentString(sibling).applyTextStyle(style));
  }

  @Override
  public void dynamicTestRegistered(TestIdentifier testIdentifier) {
    if (testIdentifier.isTest()) {
      testsFound.incrementAndGet();
      updateProgressBar();
    }
  }

  @Override
  public void executionSkipped(TestIdentifier testIdentifier, String reason) {
    Set<TestIdentifier> descendants = testPlan.getDescendants(testIdentifier);
    int testsSkipped = (int) Stream.concat(Stream.of(testIdentifier), descendants.stream()) //
        .filter(it -> it.isTest()) //
        .count();
    this.testsSkipped.addAndGet(testsSkipped);
    updateProgressBar();

    String displayName = testIdentifier.getDisplayName();
    ITextComponent announcement = createAnnouncement("Skipped " + displayName + ":\n" + reason);
    announcement.applyTextStyle(YELLOW);
    source.sendFeedback(announcement, false);
  }

  @Override
  public void executionStarted(TestIdentifier testIdentifier) {
    if (testIdentifier.isTest()) {
      String displayName = testIdentifier.getDisplayName();
      ITextComponent announcement = createAnnouncement("Started " + displayName);
      source.sendFeedback(announcement, false);
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
            source.sendErrorMessage(message);
          });
          break;
      }
      updateProgressBar();
    }
  }

  private int getTestsFinished() {
    return testsSuccessful.get() + testsAborted.get() + testsSkipped.get() + testsFailed.get();
  }

  private void updateProgressBar() {
    CustomBossEvent progressBar = provideProgressBar();
    int testsFinished = getTestsFinished();
    progressBar.setValue(testsFinished);
    progressBar.setMax(testsFound.get());

    int testsCanceled = testsAborted.get() + testsSkipped.get();
    ITextComponent name =
        new TextComponentString("Finished " + testsFinished + " / " + testsFound + " tests")
            .appendText(" (")
            .appendSibling(new TextComponentString("" + testsSuccessful).applyTextStyle(GREEN))
            .appendText(", ")
            .appendSibling(new TextComponentString("" + testsCanceled).applyTextStyle(YELLOW))
            .appendText(", ")
            .appendSibling(new TextComponentString("" + testsFailed).applyTextStyle(RED))
            .appendText(")");
    progressBar.setName(name);

    Color color;
    if (testsFailed.get() > 0) {
      color = Color.RED;
    } else if (testsCanceled > 0) {
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
