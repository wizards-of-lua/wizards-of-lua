package net.karneim.luamod;

import javax.annotation.Nullable;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class Players {
  private final MinecraftServer server;
  private final ICommandSender context;
  
  public Players(MinecraftServer server, ICommandSender context) {
    this.server = server;
    this.context = context;
  }

  public String[] list() {
    return server.getAllUsernames();
  }

  public @Nullable EntityPlayerMP get(String name) {
    return server.getPlayerList().getPlayerByUsername(name);
  }
  
  public @Nullable EntityPlayerMP find(String target) {
    return net.minecraft.command.EntitySelector.matchOnePlayer(context, target);
  }
 
}
