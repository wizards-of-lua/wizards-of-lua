package net.wizardsoflua.imc;

import java.util.stream.Stream;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

public class TypedImc {
  public static <T> void sendTo(String modId, Class<T> messageType, T message) {
    InterModComms.sendTo(modId, messageType.getName(), () -> message);
  }

  public static <T> Stream<T> getMessages(InterModProcessEvent event, Class<T> messageType) {
    return event.getIMCStream(it -> messageType.getName().equals(it)) //
        .map(it -> it.getMessageSupplier()) //
        .map(it -> it.get()) //
        .map(messageType::cast) //
    ;
  }
}
