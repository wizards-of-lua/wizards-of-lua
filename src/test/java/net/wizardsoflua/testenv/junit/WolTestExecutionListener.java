package net.wizardsoflua.testenv.junit;

import static java.util.Objects.requireNonNull;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.YELLOW;
import static net.wizardsoflua.WolAnnouncementMessage.createAnnouncement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class WolTestExecutionListener implements TestExecutionListener {
  private final CommandSource source;
  private LocalDateTime start;

  private final AtomicLong testsTotal = new AtomicLong();
  private final AtomicLong testsSuccessful = new AtomicLong();
  private final AtomicLong testsAborted = new AtomicLong();
  private final AtomicLong testsSkipped = new AtomicLong();
  private final AtomicLong testsFailed = new AtomicLong();
  private TestPlan testPlan;

  public WolTestExecutionListener(CommandSource source) {
    this.source = requireNonNull(source, "source");
  }

  @Override
  public void testPlanExecutionStarted(TestPlan testPlan) {
    this.testPlan = testPlan;
    long testCount = testPlan.countTestIdentifiers(it -> it.isTest());
    ITextComponent announcement = createAnnouncement("Found " + testCount + " tests...");
    source.sendFeedback(announcement, false);
    start = LocalDateTime.now();
  }

  @Override
  public void testPlanExecutionFinished(TestPlan testPlan) {
    Duration duration = Duration.between(start, LocalDateTime.now());
    ITextComponent message = new TextComponentString(
        "Finished " + testsTotal + " tests in " + duration.getSeconds() + " seconds:\n");
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
  }

  private ITextComponent appendSibling(ITextComponent message, String sibling,
      TextFormatting style) {
    return message.appendSibling(new TextComponentString(sibling).applyTextStyle(style));
  }

  @Override
  public void executionSkipped(TestIdentifier testIdentifier, String reason) {
    Set<TestIdentifier> descendants = testPlan.getDescendants(testIdentifier);
    long testsSkipped = Stream.concat(Stream.of(testIdentifier), descendants.stream()) //
        .filter(it -> it.isTest()) //
        .count();
    this.testsSkipped.addAndGet(testsSkipped);
    testsTotal.addAndGet(testsSkipped);

    String displayName = testIdentifier.getDisplayName();
    ITextComponent announcement = createAnnouncement("Skipped " + displayName + ":\n" + reason);
    announcement.applyTextStyle(YELLOW);
    source.sendFeedback(announcement, false);
  }

  @Override
  public void executionStarted(TestIdentifier testIdentifier) {
    if (testIdentifier.isTest()) {
      testsTotal.incrementAndGet();

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
    }
  }
}
