package net.wizardsoflua.testenv.junit;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;

public class TestResults extends RunNotifier {
  private final Logger logger;
  private final MinecraftServer server;
  private final String playerName;
  private int testsFinished = 0;
  private List<Failure> failures = new ArrayList<>();

  public TestResults(Logger logger, MinecraftServer server, String playerName) {
    this.logger = checkNotNull(logger, "logger==null!");
    this.server = server;
    this.playerName = playerName;
  }

  @Override
  public void fireTestStarted(Description description) throws StoppedByUserException {
    super.fireTestStarted(description);
    String message = "Running " + description.getDisplayName();
    logger.info(message);

    try {
      EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(playerName);
      ITextComponent itextcomponent = new TextComponentString(message);
      SPacketTitle packet = new SPacketTitle(SPacketTitle.Type.ACTIONBAR,
          TextComponentUtils.updateForEntity(player.getCommandSource(), itextcomponent, player));
      player.connection.sendPacket(packet);
    } catch (CommandSyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void fireTestFailure(Failure failure) {
    super.fireTestFailure(failure);
    failures.add(failure);
    logger.info("Failed " + failure.getTestHeader());
    logger.error(failure.getTrace());
  }

  @Override
  public void fireTestFinished(Description description) {
    super.fireTestFinished(description);
    testsFinished++;
    logger.info("Finished " + description.getDisplayName());
  }

  public boolean isOK() {
    return failures.isEmpty();
  }

  public Iterable<Failure> getFailures() {
    return failures;
  }

  public int getTestsFinished() {
    return testsFinished;
  }
}
