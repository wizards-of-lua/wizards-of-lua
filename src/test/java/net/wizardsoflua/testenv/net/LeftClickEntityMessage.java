package net.wizardsoflua.testenv.net;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.auto.service.AutoService;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

@AutoService(NetworkMessage.class)
public class LeftClickEntityMessage implements NetworkMessage {
  private UUID entityId;

  public LeftClickEntityMessage(Entity entity) {
    entityId = entity.getUniqueID();
  }

  public LeftClickEntityMessage() {}

  @Override
  public void decode(PacketBuffer buffer) {
    entityId = UUID.fromString(buffer.readString(36));
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeString(entityId.toString());
  }

  @Override
  public void handle(NetworkEvent.Context contextSupplier) {
    Minecraft minecraft = Minecraft.getInstance();
    Entity entity = findEnityById(minecraft.world);
    if (entity != null) {
      minecraft.objectMouseOver = new RayTraceResult(entity);
      try {
        Method m = Minecraft.class.getDeclaredMethod("clickMouse");
        m.setAccessible(true);
        m.invoke(minecraft);
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    } else {
      throw new RuntimeException("Can't find entity with id = " + entityId);
    }
  }

  private @Nullable Entity findEnityById(World world) {
    for (Entity e : world.loadedEntityList) {
      if (e.getUniqueID().equals(entityId)) {
        return e;
      }
    }
    return null;
  }
}
