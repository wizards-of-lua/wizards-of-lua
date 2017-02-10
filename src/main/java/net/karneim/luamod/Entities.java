package net.karneim.luamod;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import net.karneim.luamod.lua.NBTTagUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.sandius.rembulan.Table;

public class Entities {
  private static final Function<? super Entity, String> TO_ID = new Function<Entity, String>() {
    @Override
    public String apply(Entity input) {
      return input.getCachedUniqueIdString();
    }
  };

  private final MinecraftServer server;
  private final ICommandSender context;

  public Entities(MinecraftServer server, ICommandSender context) {
    this.server = server;
    this.context = context;
  }

  public Iterable<String> list() {
    List<Entity> list = context.getEntityWorld().loadedEntityList;
    return idsOf(list);
  }

  public @Nullable Entity get(String id) {
    try {
      return CommandBase.getEntity(server, context, id);
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  public void put(String id, Table data) {
    Entity entity;
    try {
      entity = CommandBase.getEntity(server, context, id);
    } catch (EntityNotFoundException e) {
      return;
    }
    UUID uuid = entity.getUniqueID();
    NBTTagCompound origTag = entity.writeToNBT(new NBTTagCompound());
    NBTTagCompound mergedTag = NBTTagUtil.merge(origTag, data);
    entity.readFromNBT(mergedTag);
    entity.setUniqueId(uuid);
  }

  public @Nullable Iterable<String> find(String target) {
    List<Entity> list = EntitySelector.<Entity>matchEntities(context, target, Entity.class);
    return idsOf(list);
  }

  private Iterable<String> idsOf(List<Entity> list) {
    return Iterables.transform(list, TO_ID);
  }

}
