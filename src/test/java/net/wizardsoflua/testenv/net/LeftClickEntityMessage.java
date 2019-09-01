package net.wizardsoflua.testenv.net;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import javax.annotation.Nullable;
import com.google.auto.service.AutoService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
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
  public void handle(NetworkEvent.Context context) {
    DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> ClientProxy.handle(context, entityId));
  }

  @OnlyIn(Dist.CLIENT)
  private static class ClientProxy {
    private static void handle(NetworkEvent.Context context, UUID entityId) {
      context.enqueueWork(() -> {
        Entity entity = findEnityById(entityId);
        if (entity != null) {
          Minecraft minecraft = Minecraft.getInstance();
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
      });
    }

    private static @Nullable Entity findEnityById(UUID entityId) {
      Minecraft minecraft = Minecraft.getInstance();
      WorldClient world = minecraft.world;
      for (Entity entity : world.loadedEntityList) {
        if (entity.getUniqueID().equals(entityId)) {
          return entity;
        }
      }
      return null;
    }
  }
}
