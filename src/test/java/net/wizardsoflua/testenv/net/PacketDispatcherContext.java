package net.wizardsoflua.testenv.net;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.network.NetworkEvent;

public interface PacketDispatcherContext {
  EntityPlayer getPlayerEntity(NetworkEvent.Context ctx);

  IThreadListener getThreadFromContext(NetworkEvent.Context ctx);
}
