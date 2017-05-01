package net.karneim.luamod.lua.classes;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.karneim.luamod.cursor.Spell;
import net.karneim.luamod.lua.classes.entity.EntityClass;
import net.karneim.luamod.lua.classes.entity.item.EntityItemClass;
import net.karneim.luamod.lua.classes.entity.item.EntityMinecartChestClass;
import net.karneim.luamod.lua.classes.entity.item.EntityMinecartClass;
import net.karneim.luamod.lua.classes.entity.item.EntityMinecartCommandBlockClass;
import net.karneim.luamod.lua.classes.entity.item.EntityMinecartContainerClass;
import net.karneim.luamod.lua.classes.entity.item.EntityMinecartEmptyClass;
import net.karneim.luamod.lua.classes.entity.item.EntityMinecartFurnaceClass;
import net.karneim.luamod.lua.classes.entity.item.EntityMinecartHopperClass;
import net.karneim.luamod.lua.classes.entity.item.EntityMinecartMobSpawnerClass;
import net.karneim.luamod.lua.classes.entity.item.EntityMinecartTntClass;
import net.karneim.luamod.lua.classes.event.EventClass;
import net.karneim.luamod.lua.classes.event.ServerChatEventClass;
import net.karneim.luamod.lua.classes.event.brewing.PotionBrewEventClass;
import net.karneim.luamod.lua.classes.event.brewing.PotionBrewPostEventClass;
import net.karneim.luamod.lua.classes.event.brewing.PotionBrewPreEventClass;
import net.karneim.luamod.lua.classes.event.entity.EntityEventClass;
import net.karneim.luamod.lua.classes.event.entity.item.ItemEventClass;
import net.karneim.luamod.lua.classes.event.entity.item.ItemExpireEventClass;
import net.karneim.luamod.lua.classes.event.entity.item.ItemTossEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingAttackEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingDeathEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingDropsEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingEntityUseItemEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingEntityUseItemFinishEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingEntityUseItemStartEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingEntityUseItemStopEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingEntityUseItemTickEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.LivingSpawnEventClass;
import net.karneim.luamod.lua.classes.event.entity.living.SpecialSpawnEventClass;
import net.karneim.luamod.lua.classes.event.entity.minecart.MinecartCollisionEventClass;
import net.karneim.luamod.lua.classes.event.entity.minecart.MinecartEventClass;
import net.karneim.luamod.lua.classes.event.entity.minecart.MinecartInteractEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.AchievementEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.AnvilRepairEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.AttackEntityEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.BonemealEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.EntityItemPickupEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.FillBucketEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.LeftClickBlockEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.PlayerContainerCloseEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.PlayerEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.PlayerInteractEventClass;
import net.karneim.luamod.lua.classes.event.entity.player.RightClickBlockEventClass;
import net.karneim.luamod.lua.classes.event.game.PlayerGameEventClass;
import net.karneim.luamod.lua.classes.event.game.PlayerLoggedInEventClass;
import net.karneim.luamod.lua.classes.event.game.PlayerLoggedOutEventClass;
import net.karneim.luamod.lua.classes.event.game.PlayerRespawnEventClass;
import net.karneim.luamod.lua.classes.event.wol.AnimationHandEventClass;
import net.karneim.luamod.lua.classes.event.wol.ClickWindowEventClass;
import net.karneim.luamod.lua.classes.event.wol.CustomLuaEventClass;
import net.karneim.luamod.lua.classes.event.wol.WhisperEventClass;
import net.karneim.luamod.lua.classes.inventory.InventoryClass;
import net.karneim.luamod.lua.classes.stats.AchievementClass;
import net.karneim.luamod.lua.classes.stats.StatBaseClass;
import net.karneim.luamod.lua.classes.tileentity.CommandBlockClass;
import net.karneim.luamod.lua.event.AnimationHandEvent;
import net.karneim.luamod.lua.event.ClickWindowEvent;
import net.karneim.luamod.lua.event.CustomLuaEvent;
import net.karneim.luamod.lua.event.WhisperEvent;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingLuaClass;
import net.karneim.luamod.lua.wrapper.ModifiableArrayWrapper;
import net.karneim.luamod.lua.wrapper.UnmodifiableIterableWrapper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.event.entity.minecart.MinecartCollisionEvent;
import net.minecraftforge.event.entity.minecart.MinecartEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;

public class LuaTypesRepo {

  private final Map<String, LuaClass> types = new HashMap<>();
  private final Multimap<LuaClass, LuaClass> subClasses = HashMultimap.create();
  private final Table env;

  public LuaTypesRepo(Table env) {
    this.env = checkNotNull(env);
  }

  public <T extends LuaClass> T get(Class<T> cls) {
    String moduleName = LuaClass.getModuleNameOf(cls);
    LuaClass luaClass = get(moduleName);
    return cls.cast(luaClass);
  }

  public LuaClass get(String name) {
    return types.get(name);
  }

  public Table getEnv() {
    return env;
  }

  public boolean isRegistered(String name) {
    return types.containsKey(name);
  }

  public <T extends LuaClass> void register(T luaClass) {
    String name = luaClass.getModuleName();
    if (types.containsKey(name)) {
      throw new IllegalArgumentException(String.format("Type %s is already definded!", luaClass));
    }
    types.put(name, luaClass);
    subClasses.put(luaClass.getSuperClass(), luaClass);
  }

  public @Nullable DelegatingTable<? extends Achievement> wrap(@Nullable Achievement javaObject) {
    return wrap(javaObject, get(AchievementClass.class));
  }

  public @Nullable DelegatingTable<? extends AchievementEvent> wrap(
      @Nullable AchievementEvent javaObject) {
    return wrap(javaObject, get(AchievementEventClass.class));
  }

  public @Nullable DelegatingTable<? extends AnimationHandEvent> wrap(
      @Nullable AnimationHandEvent javaObject) {
    return wrap(javaObject, get(AnimationHandEventClass.class));
  }

  public @Nullable DelegatingTable<? extends RayTraceResult> wrap(
      @Nullable RayTraceResult javaObject) {
    return wrap(javaObject, get(RayTraceResultClass.class));
  }

  public @Nullable DelegatingTable<? extends FillBucketEvent> wrap(
      @Nullable FillBucketEvent javaObject) {
    return wrap(javaObject, get(FillBucketEventClass.class));
  }

  public @Nullable DelegatingTable<? extends AnvilRepairEvent> wrap(
      @Nullable AnvilRepairEvent javaObject) {
    return wrap(javaObject, get(AnvilRepairEventClass.class));
  }

  public @Nullable DelegatingTable<? extends AttackEntityEvent> wrap(
      @Nullable AttackEntityEvent javaObject) {
    return wrap(javaObject, get(AttackEntityEventClass.class));
  }

  public @Nullable DelegatingTable<? extends BonemealEvent> wrap(
      @Nullable BonemealEvent javaObject) {
    return wrap(javaObject, get(BonemealEventClass.class));
  }

  public boolean wrap(boolean javaObject) {
    return javaObject;
  }

  public long wrap(byte javaObject) {
    return javaObject;
  }

  public @Nullable ByteString wrap(@Nullable ByteString javaObject) {
    return javaObject;
  }

  public @Nullable DelegatingTable<? extends ClickWindowEvent> wrap(
      @Nullable ClickWindowEvent javaObject) {
    return wrap(javaObject, get(ClickWindowEventClass.class));
  }

  public @Nullable DelegatingTable<? extends CommandBlockBaseLogic> wrap(
      @Nullable CommandBlockBaseLogic javaObject) {
    return wrap(javaObject, get(CommandBlockClass.class));
  }

  public @Nullable DelegatingTable<? extends CustomLuaEvent> wrap(
      @Nullable CustomLuaEvent javaObject) {
    return wrap(javaObject, get(CustomLuaEventClass.class));
  }

  public @Nullable DelegatingTable<? extends DamageSource> wrap(@Nullable DamageSource javaObject) {
    return wrap(javaObject, get(DamageSourceClass.class));
  }

  public double wrap(double javaObject) {
    return javaObject;
  }

  public @Nullable DelegatingTable<? extends Entity> wrap(@Nullable Entity javaObject) {
    return wrap(javaObject, get(EntityClass.class));
  }

  public @Nullable DelegatingTable<? extends EntityEvent> wrap(@Nullable EntityEvent javaObject) {
    return wrap(javaObject, get(EntityEventClass.class));
  }

  public @Nullable DelegatingTable<? extends EntityItem> wrap(@Nullable EntityItem javaObject) {
    return wrap(javaObject, get(EntityItemClass.class));
  }

  public @Nullable DelegatingTable<? extends EntityItemPickupEvent> wrap(
      @Nullable EntityItemPickupEvent javaObject) {
    return wrap(javaObject, get(EntityItemPickupEventClass.class));
  }

  public @Nullable DelegatingTable<? extends EntityLiving> wrap(@Nullable EntityLiving javaObject) {
    return wrap(javaObject, get(EntityLivingClass.class));
  }

  public @Nullable DelegatingTable<? extends EntityLivingBase> wrap(
      @Nullable EntityLivingBase javaObject) {
    return wrap(javaObject, get(EntityLivingBaseClass.class));
  }

  public @Nullable DelegatingTable<? extends EntityMinecart> wrap(
      @Nullable EntityMinecart javaObject) {
    return wrap(javaObject, get(EntityMinecartClass.class));
  }

  public @Nullable DelegatingTable<? extends EntityMinecartChest> wrap(
      @Nullable EntityMinecartChest javaObject) {
    return wrap(javaObject, get(EntityMinecartChestClass.class));
  }

  public @Nullable DelegatingTable<? extends EntityMinecartCommandBlock> wrap(
      @Nullable EntityMinecartCommandBlock javaObject) {
    return wrap(javaObject, get(EntityMinecartCommandBlockClass.class));
  }

  public @Nullable DelegatingTable<? extends EntityMinecartContainer> wrap(
      @Nullable EntityMinecartContainer javaObject) {
    return wrap(javaObject, get(EntityMinecartContainerClass.class));
  }

  public @Nullable DelegatingTable<? extends EntityMinecartEmpty> wrap(
      @Nullable EntityMinecartEmpty javaObject) {
    return wrap(javaObject, get(EntityMinecartEmptyClass.class));
  }

  public @Nullable DelegatingTable<? extends EntityMinecartFurnace> wrap(
      @Nullable EntityMinecartFurnace javaObject) {
    return wrap(javaObject, get(EntityMinecartFurnaceClass.class));
  }

  public @Nullable DelegatingTable<? extends EntityMinecartHopper> wrap(
      @Nullable EntityMinecartHopper javaObject) {
    return wrap(javaObject, get(EntityMinecartHopperClass.class));
  }

  public @Nullable DelegatingTable<? extends EntityMinecartMobSpawner> wrap(
      @Nullable EntityMinecartMobSpawner javaObject) {
    return wrap(javaObject, get(EntityMinecartMobSpawnerClass.class));
  }

  public @Nullable DelegatingTable<? extends EntityMinecartTNT> wrap(
      @Nullable EntityMinecartTNT javaObject) {
    return wrap(javaObject, get(EntityMinecartTntClass.class));
  }

  public @Nullable DelegatingTable<? extends EntityPlayer> wrap(@Nullable EntityPlayer javaObject) {
    return wrap(javaObject, get(EntityPlayerClass.class));
  }

  public @Nullable ByteString wrap(@Nullable Enum<?> javaObject) {
    return javaObject == null ? null : ByteString.of(javaObject.name());
  }

  public @Nullable DelegatingTable<? extends Event> wrap(@Nullable Event javaObject) {
    return wrap(javaObject, get(EventClass.class));
  }

  public double wrap(float javaObject) {
    return javaObject;
  }

  public @Nullable DelegatingTable<? extends IBlockState> wrap(@Nullable IBlockState javaObject) {
    return wrap(javaObject, get(BlockStateClass.class));
  }

  public @Nullable DelegatingTable<? extends IInventory> wrap(@Nullable IInventory javaObject) {
    return wrap(javaObject, get(InventoryClass.class));
  }

  public long wrap(int javaObject) {
    return javaObject;
  }

  public @Nullable DelegatingTable<? extends ItemEvent> wrap(@Nullable ItemEvent javaObject) {
    return wrap(javaObject, get(ItemEventClass.class));
  }

  public @Nullable DelegatingTable<? extends ItemExpireEvent> wrap(
      @Nullable ItemExpireEvent javaObject) {
    return wrap(javaObject, get(ItemExpireEventClass.class));
  }

  public @Nullable DelegatingTable<? extends ItemStack> wrap(@Nullable ItemStack javaObject) {
    return wrap(javaObject, get(ItemStackClass.class));
  }

  public @Nullable DelegatingTable<? extends ItemTossEvent> wrap(
      @Nullable ItemTossEvent javaObject) {
    return wrap(javaObject, get(ItemTossEventClass.class));
  }

  public ByteString wrap(@Nullable ITextComponent javaObject) {
    return javaObject == null ? null : wrap(javaObject.getFormattedText());
  }

  public @Nullable DelegatingTable<? extends LeftClickBlock> wrap(
      @Nullable LeftClickBlock javaObject) {
    return wrap(javaObject, get(LeftClickBlockEventClass.class));
  }

  public @Nullable DelegatingTable<? extends LivingAttackEvent> wrap(
      @Nullable LivingAttackEvent javaObject) {
    return wrap(javaObject, get(LivingAttackEventClass.class));
  }

  public @Nullable DelegatingTable<? extends LivingDeathEvent> wrap(
      @Nullable LivingDeathEvent javaObject) {
    return wrap(javaObject, get(LivingDeathEventClass.class));
  }

  public @Nullable DelegatingTable<? extends LivingDropsEvent> wrap(
      @Nullable LivingDropsEvent javaObject) {
    return wrap(javaObject, get(LivingDropsEventClass.class));
  }

  public @Nullable DelegatingTable<? extends LivingEntityUseItemEvent> wrap(
      @Nullable LivingEntityUseItemEvent javaObject) {
    return wrap(javaObject, get(LivingEntityUseItemEventClass.class));
  }

  public @Nullable DelegatingTable<? extends LivingEntityUseItemEvent.Finish> wrap(
      @Nullable LivingEntityUseItemEvent.Finish javaObject) {
    return wrap(javaObject, get(LivingEntityUseItemFinishEventClass.class));
  }

  public @Nullable DelegatingTable<? extends LivingEntityUseItemEvent.Start> wrap(
      @Nullable LivingEntityUseItemEvent.Start javaObject) {
    return wrap(javaObject, get(LivingEntityUseItemStartEventClass.class));
  }

  public @Nullable DelegatingTable<? extends LivingEntityUseItemEvent.Stop> wrap(
      @Nullable LivingEntityUseItemEvent.Stop javaObject) {
    return wrap(javaObject, get(LivingEntityUseItemStopEventClass.class));
  }

  public @Nullable DelegatingTable<? extends LivingEntityUseItemEvent.Tick> wrap(
      @Nullable LivingEntityUseItemEvent.Tick javaObject) {
    return wrap(javaObject, get(LivingEntityUseItemTickEventClass.class));
  }

  public @Nullable DelegatingTable<? extends LivingEvent> wrap(@Nullable LivingEvent javaObject) {
    return wrap(javaObject, get(LivingEventClass.class));
  }

  public @Nullable DelegatingTable<? extends LivingSpawnEvent> wrap(
      @Nullable LivingSpawnEvent javaObject) {
    return wrap(javaObject, get(LivingSpawnEventClass.class));
  }

  public long wrap(long javaObject) {
    return javaObject;
  }

  public @Nullable DelegatingTable<? extends Material> wrap(@Nullable Material javaObject) {
    return wrap(javaObject, get(MaterialClass.class));
  }

  public @Nullable DelegatingTable<? extends MinecartCollisionEvent> wrap(
      @Nullable MinecartCollisionEvent javaObject) {
    return wrap(javaObject, get(MinecartCollisionEventClass.class));
  }

  public @Nullable DelegatingTable<? extends MinecartEvent> wrap(
      @Nullable MinecartEvent javaObject) {
    return wrap(javaObject, get(MinecartEventClass.class));
  }

  public @Nullable DelegatingTable<? extends MinecartInteractEvent> wrap(
      @Nullable MinecartInteractEvent javaObject) {
    return wrap(javaObject, get(MinecartInteractEventClass.class));
  }

  public @Nullable DelegatingTable<? extends net.minecraftforge.fml.common.gameevent.PlayerEvent> wrap(
      @Nullable net.minecraftforge.fml.common.gameevent.PlayerEvent javaObject) {
    return wrap(javaObject, get(PlayerGameEventClass.class));
  }

  public @Nullable DelegatingTable<? extends PlayerEvent> wrap(@Nullable PlayerEvent javaObject) {
    return wrap(javaObject, get(PlayerEventClass.class));
  }

  public @Nullable DelegatingTable<? extends PlayerInteractEvent> wrap(
      @Nullable PlayerInteractEvent javaObject) {
    return wrap(javaObject, get(PlayerInteractEventClass.class));
  }

  public @Nullable DelegatingTable<? extends PlayerLoggedInEvent> wrap(
      @Nullable PlayerLoggedInEvent javaObject) {
    return wrap(javaObject, get(PlayerLoggedInEventClass.class));
  }

  public @Nullable DelegatingTable<? extends PlayerLoggedOutEvent> wrap(
      @Nullable PlayerLoggedOutEvent javaObject) {
    return wrap(javaObject, get(PlayerLoggedOutEventClass.class));
  }

  public @Nullable DelegatingTable<? extends PlayerRespawnEvent> wrap(
      @Nullable PlayerRespawnEvent javaObject) {
    return wrap(javaObject, get(PlayerRespawnEventClass.class));
  }

  public @Nullable DelegatingTable<? extends PotionBrewEvent> wrap(
      @Nullable PotionBrewEvent javaObject) {
    return wrap(javaObject, get(PotionBrewEventClass.class));
  }

  public @Nullable DelegatingTable<? extends PotionBrewEvent.Post> wrap(
      @Nullable PotionBrewEvent.Post javaObject) {
    return wrap(javaObject, get(PotionBrewPostEventClass.class));
  }

  public @Nullable DelegatingTable<? extends PotionBrewEvent.Pre> wrap(
      @Nullable PotionBrewEvent.Pre javaObject) {
    return wrap(javaObject, get(PotionBrewPreEventClass.class));
  }

  public @Nullable DelegatingTable<? extends RightClickBlock> wrap(
      @Nullable RightClickBlock javaObject) {
    return wrap(javaObject, get(RightClickBlockEventClass.class));
  }

  public @Nullable DelegatingTable<? extends ServerChatEvent> wrap(
      @Nullable ServerChatEvent javaObject) {
    return wrap(javaObject, get(ServerChatEventClass.class));
  }

  public long wrap(short javaObject) {
    return javaObject;
  }

  public @Nullable DelegatingTable<? extends SpecialSpawn> wrap(@Nullable SpecialSpawn javaObject) {
    return wrap(javaObject, get(SpecialSpawnEventClass.class));
  }

  public @Nullable DelegatingTable<? extends Spell> wrap(@Nullable Spell javaObject) {
    return wrap(javaObject, get(SpellClass.class));
  }

  public @Nullable DelegatingTable<? extends StatBase> wrap(@Nullable StatBase javaObject) {
    return wrap(javaObject, get(StatBaseClass.class));
  }

  public @Nullable ByteString wrap(@Nullable String javaObject) {
    return javaObject == null ? null : ByteString.of(javaObject);
  }

  private @Nullable <T> DelegatingTable<? extends T> wrap(@Nullable T javaObject,
      DelegatingLuaClass<T> luaClass) {
    checkNotNull(luaClass, "luaClass == null!");
    for (LuaClass subClass : subClasses.get(luaClass)) {
      if (subClass instanceof DelegatingLuaClass) {
        @SuppressWarnings("unchecked")
        DelegatingLuaClass<T> uncheckedSubClass = (DelegatingLuaClass<T>) subClass;
        if (uncheckedSubClass.getJavaClass().isInstance(javaObject)) {
          return wrap(javaObject, uncheckedSubClass);
        }
      }
    }
    return luaClass.getLuaObjectNullable(javaObject);
  }

  public @Nullable DelegatingTable<? extends Vec3d> wrap(@Nullable Vec3d javaObject) {
    return wrap(javaObject, get(Vec3Class.class));
  }

  public @Nullable DelegatingTable<? extends PlayerContainerEvent.Close> wrap(
      @Nullable PlayerContainerEvent.Close javaObject) {
    return wrap(javaObject, get(PlayerContainerCloseEventClass.class));
  }

  public @Nullable DelegatingTable<? extends Vec3d> wrap(@Nullable Vec3i javaObject) {
    return javaObject == null ? null : wrap(new Vec3d(javaObject));
  }

  public @Nullable DelegatingTable<? extends WhisperEvent> wrap(@Nullable WhisperEvent javaObject) {
    return wrap(javaObject, get(WhisperEventClass.class));
  }

  public @Nullable DelegatingTable<? extends World> wrap(@Nullable World javaObject) {
    return wrap(javaObject, get(WorldClass.class));
  }

  public @Nullable DelegatingTable<? extends Iterable<ItemStack>> wrapArmor(
      @Nullable Iterable<ItemStack> javaObject) {
    return wrap(javaObject, get(ArmorClass.class));
  }

  public @Nullable DelegatingTable<? extends Iterable<String>> wrapStrings(
      @Nullable Iterable<String> javaObject) {
    if (javaObject == null) {
      return null;
    }
    UnmodifiableIterableWrapper<String, ByteString> wrapper =
        new UnmodifiableIterableWrapper<>(j -> ByteString.of(j));
    return wrapper.createLuaObject(javaObject);
  }

  public @Nullable DelegatingTable<? extends String[]> wrapStrings(@Nullable String[] javaObject) {
    if (javaObject == null) {
      return null;
    }
    ModifiableArrayWrapper<String, ByteString> wrapper =
        new ModifiableArrayWrapper<>(ByteString.class, j -> ByteString.of(j), l -> l.decode());
    return wrapper.createLuaObject(javaObject);
  }
}
