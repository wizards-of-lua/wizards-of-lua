package net.wizardsoflua.spell;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.WorldServer;

public class WolCommandSource extends CommandSource {
  protected ICommandSource source;

  public WolCommandSource(ICommandSource source, Vec3d pos, Vec2f pitchYaw, WorldServer world,
      int permissionLevel, String name, ITextComponent displayName, MinecraftServer server,
      Entity entity) {
    super(source, pos, pitchYaw, world, permissionLevel, name, displayName, server, entity);
    this.source = source;
  }

  public ICommandSource getSource() {
    return source;
  }
}
