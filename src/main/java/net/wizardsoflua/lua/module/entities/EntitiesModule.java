package net.wizardsoflua.lua.module.entities;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.auto.service.AutoService;

import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaModuleTable;
import net.wizardsoflua.annotation.LuaFunction;
import net.wizardsoflua.annotation.LuaFunctionDoc;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.SpellExtension;
import net.wizardsoflua.lua.classes.entity.EntityClass;
import net.wizardsoflua.lua.extension.LuaTableExtension;
import net.wizardsoflua.lua.nbt.NbtConverter;
import net.wizardsoflua.spell.SpellEntity;

/**
 * The <span class="notranslate">Entities</span> module provides access to all [Entity](/module/Entity) objects currently loaded.
 *
 */
@AutoService(SpellExtension.class)
@GenerateLuaModuleTable
@GenerateLuaDoc(name = EntitiesModule.NAME, subtitle = "Knowing What Happened")
public class EntitiesModule extends LuaTableExtension {
  public static final String NAME = "Entities";
  @Resource
  private LuaConverters converters;
  @Resource
  private SpellEntity spellEntity;
  @Inject
  private NbtConverter nbtConverter;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Table getTable() {
    return new EntitiesModuleTable<>(this, converters);
  }

  /**
   * The ‘find’ function returns a table of Entity objects that match the given selector.
   * 
   * #### Example
   * 
   * Printing the number of all players currently logged in.
   * 
   * <code>
   * found = Entities.find("@a")
   * print(#found)
   * </code>
   *  
   * #### Example
   * 
   * Printing the position of player mickkay.
   * 
   * <code>
   * found = Entities.find("@a[name=mickkay]")[1]
   * print(found.pos)
   * </code>
   * 
   * #### Example
   * 
   * Printing the positions of all cows in the (loaded part of the) world.
   * 
   * <code>
   * found = Entities.find("@e[type=cow]")
   * for _,cow in pairs(found) do
   *   print(cow.pos)
   * end
   * </code>
   * 
   * #### Example
   * 
   * Printing the names of all dropped items in the (loaded part of the) world.
   * 
   * <code>
   * found = Entities.find("@e[type=item]")
   * for _,e in pairs(found) do
   *   print(e.name)
   * end
   * </code>
   * 
   */
  @LuaFunction
  public List<Entity> find(String selector) {
    try {
      List<Entity> list = EntitySelector.<Entity>matchEntities(spellEntity, selector, Entity.class);
      return list;
    } catch (CommandException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  /**
   * The ‘summon’ function returns a freshly created entity of the given type,
   * having the optionally given Nbt values.
   * 
   * #### Example
   * 
   * Creating a pig and moving it half a meter upwards.
   * 
   * <code>
   * pig = Entities.summon("pig")
   * pig:move("up",0.5)
   * </code>
   *  
   * #### Example
   * 
   * Creating a creeper with no AI.
   * 
   * <code>
   * Entities.summon("creeper", {NoAI=1}) 
   * </code>
   * 
   * #### Example
   * 
   * Creating a zombie with no AI that is spinning around.
   * 
   * <code>
   * z = Entities.summon("zombie", {NoAI=1})
   * while true do
   *   z.rotationYaw = z.rotationYaw + 10
   *   sleep(1)
   * end
   * </code>
   * 
   */
  @LuaFunction
  @LuaFunctionDoc(returnType = EntityClass.NAME, args = {"nbt"})
  public Entity summon(String name, @Nullable Table nbt) {
    if (nbt == null) {
      nbt = DefaultTable.factory().newTable();
    }
    nbt.rawset("id", name);
    NBTTagCompound xy = nbtConverter.toNbtCompound(nbt);
    World world = spellEntity.getEntityWorld();
    Vec3d vec = spellEntity.getPositionVector();
    double x = vec.x;
    double y = vec.y;
    double z = vec.z;
    boolean attemptSpawn = true;
    Entity result = AnvilChunkLoader.readWorldEntityPos(xy, world, x, y, z, attemptSpawn);
    return result;
  }

}
