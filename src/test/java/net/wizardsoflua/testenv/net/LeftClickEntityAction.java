package net.wizardsoflua.testenv.net;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class LeftClickEntityAction extends ClientAction {

  private UUID entityId;

  public LeftClickEntityAction() {}

  public LeftClickEntityAction(Entity entity) {
    entityId = entity.getUniqueID();
  }

  @Override
  protected void read(PacketBuffer buffer) throws IOException {
    entityId = UUID.fromString(buffer.readString(36));
  }

  @Override
  protected void write(PacketBuffer buffer) throws IOException {
    buffer.writeString(entityId.toString());
  }

  @Override
  public void handleClientSide(EntityPlayer player) {
    System.out.println("left-click at entity: " + entityId);
    Entity entity = findEnityById(player.world);
    if (entity != null) {
      Minecraft.getMinecraft().objectMouseOver = new RayTraceResult(entity);
      try {
        Method m = Minecraft.class.getDeclaredMethod("clickMouse");
        m.setAccessible(true);
        m.invoke(Minecraft.getMinecraft());
      } catch (NoSuchMethodException | SecurityException | IllegalAccessException
          | IllegalArgumentException | InvocationTargetException e) {
        throw new UndeclaredThrowableException(e);
      }
    } else {
      throw new RuntimeException("Can't find entity with id = " + entityId);
    }
  }

  private Entity findEnityById(World world) {
    for (Entity e : world.loadedEntityList) {
      if (e.getUniqueID().equals(entityId)) {
        return e;
      }
    }
    return null;
  }

}
