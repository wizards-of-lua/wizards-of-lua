package net.wizardsoflua.testenv.log4j;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.filter.LevelRangeFilter;
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
  private final Appender appender = new AbstractAppender(getClass().getSimpleName(),
      LevelRangeFilter.createFilter(null, Level.INFO, null, null),
      PatternLayout.createDefaultLayout(), true, Property.EMPTY_ARRAY) {
    @Override
    public void append(LogEvent event) {
      if (EffectiveSide.get() == LogicalSide.SERVER) {
        Message message = event.getMessage();
        String text = message.getFormattedMessage();
        if (text.startsWith("Can't keep up! Is the server overloaded?")) {
          return;
        }
        MinecraftForge.EVENT_BUS.post(new ServerLog4jEvent(text));
      }
    }
  };

  public void activate() {
    appender.start();
    ((Logger) LogManager.getRootLogger()).addAppender(appender);
  }
}
