package net.karneim.luamod;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class Players {
  private static final Function<? super Entity, String> TO_ID = new Function<Entity, String>() {
    @Override
    public String apply(Entity input) {
      return input.getCachedUniqueIdString();
    }
  };

  private final MinecraftServer server;
  private final ICommandSender context;

  public Players(MinecraftServer server, ICommandSender context) {
    this.server = server;
    this.context = context;
  }

  public String[] names() {
    return server.getAllUsernames();
  }

  public Iterable<String> list() {
    return idsOf(server.getPlayerList().getPlayerList());
  }

  public @Nullable EntityPlayerMP getByName(String name) {
    return server.getPlayerList().getPlayerByUsername(name);
  }

  public @Nullable EntityPlayerMP get(String id) {
    return server.getPlayerList().getPlayerByUUID(UUID.fromString(id));
  }

  public @Nullable Iterable<String> find(String target) {
    return idsOf(
        net.minecraft.command.EntitySelector.matchEntities(context, target, EntityPlayerMP.class));
  }

  private Iterable<String> idsOf(List<EntityPlayerMP> list) {
    return Iterables.transform(list, TO_ID);
  }

}
