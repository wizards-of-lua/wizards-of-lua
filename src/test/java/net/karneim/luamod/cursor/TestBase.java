package net.karneim.luamod.cursor;

import org.mockito.Mockito;

import net.karneim.luamod.lua.SpellEntity;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TestBase {

  private SpellEntity spellEntity = Mockito.mock(SpellEntity.class);

  protected Spell newSpell(ICommandSender commandSender, World world) {
    return new Spell(commandSender, spellEntity, world, Vec3d.ZERO, Rotation.NONE, null, null);
  }
}
