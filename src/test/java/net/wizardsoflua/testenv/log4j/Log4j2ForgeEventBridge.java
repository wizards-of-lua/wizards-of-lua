package net.wizardsoflua.testenv.log4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.message.Message;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.wizardsoflua.testenv.event.ServerLog4jEvent;

/**
 * The {@link Log4j2ForgeEventBridge} forwards {@link LogEvent}s to the Forge event bus as
 * {@link ServerLog4jEvent}s.
 */
public class Log4j2ForgeEventBridge {
  public static final String NET_MINECRAFT_LOGGER = "net.minecraft";
  private final Appender appender =
      new AbstractAppender(getClass().getSimpleName(), null, PatternLayout.createDefaultLayout()) {
        @Override
        public void append(LogEvent event) {
          if (EffectiveSide.get() == LogicalSide.SERVER) {
            Message message = event.getMessage();
            String text = message.getFormattedMessage();
            MinecraftForge.EVENT_BUS.post(new ServerLog4jEvent(text));
          }
        }
      };

  public void activate() {
    Logger coreLogger = (Logger) LogManager.getRootLogger();
    appender.start();
    coreLogger.addAppender(appender);
  }
}
