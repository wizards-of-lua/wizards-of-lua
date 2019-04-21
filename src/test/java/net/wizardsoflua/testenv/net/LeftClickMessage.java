package net.wizardsoflua.testenv.net;

import static java.util.Objects.requireNonNull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;
import com.google.auto.service.AutoService;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.network.NetworkEvent;

@AutoService(NetworkMessage.class)
public class LeftClickMessage implements NetworkMessage {
  private BlockPos pos;
  private EnumFacing face;

  public LeftClickMessage(BlockPos pos, EnumFacing face) {
    this.pos = requireNonNull(pos, "pos");
    this.face = requireNonNull(face, "face");
  }

  public LeftClickMessage() {}

  @Override
  public void decode(PacketBuffer buffer) {
    int x = buffer.readInt();
    int y = buffer.readInt();
    int z = buffer.readInt();
    pos = new BlockPos(x, y, z);
    int ord = buffer.readInt();
    face = EnumFacing.values()[ord];
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeInt(pos.getX());
    buffer.writeInt(pos.getY());
    buffer.writeInt(pos.getZ());
    buffer.writeInt(face.ordinal());
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
    Vec3d vec = new Vec3d(pos);

    Minecraft.getInstance().objectMouseOver = new RayTraceResult(vec, face, pos);
    try {
      Method m = Minecraft.class.getDeclaredMethod("clickMouse");
      m.setAccessible(true);
      m.invoke(Minecraft.getInstance());
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
