package net.wizardsoflua.testenv.player;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraft.inventory.EntityEquipmentSlot.OFFHAND;
import static net.minecraft.item.ItemStack.EMPTY;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import com.google.common.io.Files;
import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import net.wizardsoflua.config.WolConfig;
import net.wizardsoflua.spell.SpellUtil;
import net.wizardsoflua.testenv.MinecraftBackdoor;
import net.wizardsoflua.testenv.WolTestenv;
import net.wizardsoflua.testenv.net.ChatMessage;
import net.wizardsoflua.testenv.net.LeftClickMessage;
import net.wizardsoflua.testenv.net.NetworkMessage;
import net.wizardsoflua.testenv.net.ReconnectMessage;
import net.wizardsoflua.testenv.net.RespawnMessage;
import net.wizardsoflua.testenv.net.RightClickMessage;

public class PlayerBackdoor {
  private final MinecraftBackdoor minecraftBackdoor;

  public PlayerBackdoor(MinecraftBackdoor minecraftBackdoor) {
    this.minecraftBackdoor = requireNonNull(minecraftBackdoor, "minecraftBackdoor");
  }

  private WolTestenv getTestenv() {
    return minecraftBackdoor.getTestenv();
  }

  public EntityPlayerMP getTestPlayer() {
    return getTestenv().getTestPlayer();
  }

  private ListenableFuture<Object> runChangeOnMainThread(Runnable task) {
    return getTestenv().runChangeOnMainThread(task);
  }

  private ListenableFuture<Object> runOnMainThread(Runnable task) {
    return getTestenv().runOnMainThread(task);
  }

  private <V> V callOnMainThread(Callable<V> task) {
    return getTestenv().callOnMainThread(task);
  }

  public UUID getUniqueID() {
    return callOnMainThread(() -> getTestPlayer().getUniqueID());
  }

  public String getName() {
    return callOnMainThread(() -> getTestPlayer().getName().getString());
  }

  public float getHealth() {
    return callOnMainThread(() -> getTestPlayer().getHealth());
  }

  public void setHealth(float value) {
    runChangeOnMainThread(() -> getTestPlayer().setHealth(value));
  }

  public BlockPos getPosition() {
    return callOnMainThread(() -> getTestPlayer().getPosition());
  }

  public void setPosition(BlockPos pos) {
    runChangeOnMainThread(() -> {
      getTestPlayer().setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
    });
  }

  public EnumFacing getFacing() {
    return callOnMainThread(() -> getTestPlayer().getHorizontalFacing());
  }

  public Vec3d getPositionLookingAt() {
    return callOnMainThread(() -> SpellUtil.getPositionLookingAt(getTestPlayer()));
  }

  public void setRotationYaw(float rotationYaw) {
    runChangeOnMainThread(() -> {
      EntityPlayerMP player = getTestPlayer();
      player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, rotationYaw,
          player.rotationPitch);
    });
  }

  public void setRotationPitch(float rotationPitch) {
    runChangeOnMainThread(() -> {
      EntityPlayerMP player = getTestPlayer();
      player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw,
          rotationPitch);
    });
  }

  public @Nullable String getTeam() {
    return callOnMainThread(() -> {
      EntityPlayerMP player = getTestPlayer();
      return Optional.ofNullable(player.getTeam()).map(it -> it.getName()).orElse(null);
    });
  }

  public void setTeam(String teamName) {
    runOnMainThread(() -> {
      EntityPlayerMP player = getTestPlayer();
      Scoreboard scoreboard = player.getWorldScoreboard();
      ScorePlayerTeam team = scoreboard.getTeam(teamName);
      scoreboard.addPlayerToTeam(player.getScoreboardName(), team);
    });
  }

  public ItemStack getMainHandItem() {
    return callOnMainThread(() -> getTestPlayer().getHeldItemMainhand());
  }

  public void setMainHandItem(@Nullable ItemStack item) {
    runChangeOnMainThread(() -> {
      EntityPlayerMP player = getTestPlayer();
      player.setItemStackToSlot(MAINHAND, ofNullable(item).orElse(EMPTY));
      player.inventoryContainer.detectAndSendChanges();
    });
  }

  public ItemStack getOffHandItem() {
    return callOnMainThread(() -> getTestPlayer().getHeldItemOffhand());
  }

  public void setOffHandItem(ItemStack item) {
    runChangeOnMainThread(() -> {
      EntityPlayerMP player = getTestPlayer();
      player.setItemStackToSlot(OFFHAND, ofNullable(item).orElse(EMPTY));
      player.inventoryContainer.detectAndSendChanges();
    });
  }

  public void changeDimension(DimensionType dim) {
    runChangeOnMainThread(() -> {
      EntityPlayerMP player = getTestPlayer();
      MinecraftServer server = player.getServer();
      server.getPlayerList().changePlayerDimension(player, dim);
    });
  }

  public boolean isOperator() {
    return callOnMainThread(() -> {
      EntityPlayerMP player = getTestPlayer();
      MinecraftServer server = player.getServer();
      GameProfile gameProfile = player.getGameProfile();
      return server.getPlayerList().canSendCommands(gameProfile);
    });
  }

  public void setOperator(boolean operator) {
    runChangeOnMainThread(() -> {
      EntityPlayerMP player = getTestPlayer();
      PlayerList playerList = player.getServer().getPlayerList();
      GameProfile gameProfile = player.getGameProfile();
      if (operator == playerList.canSendCommands(gameProfile)) {
        return;
      }
      if (operator) {
        playerList.addOp(gameProfile);
      } else {
        playerList.removeOp(gameProfile);
      }
    });
  }

  public void chat(String format, Object... args) {
    perform(new ChatMessage(String.format(format, args)));
  }

  public void leftclick(BlockPos pos, EnumFacing face) {
    perform(new LeftClickMessage(pos, face));
  }

  public void rightclick(BlockPos pos, EnumFacing face) {
    perform(new RightClickMessage(pos, face));
  }

  public void reconnect() {
    perform(new ReconnectMessage());
  }

  public void respawn() {
    perform(new RespawnMessage());
  }

  public void perform(NetworkMessage message) {
    EntityPlayerMP player = getTestPlayer();
    getTestenv().sendTo(player, message);
  }

  public void createModule(String moduleName, String content) {
    File moduleFile = getModuleFile(moduleName);
    if (moduleFile.exists()) {
      moduleFile.delete();
    }
    moduleFile.getParentFile().mkdirs();
    try {
      Files.asCharSink(moduleFile, UTF_8).write(content);
    } catch (IOException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public void deleteModule(String moduleName) {
    File moduleFile = getModuleFile(moduleName);
    if (moduleFile.exists()) {
      moduleFile.delete();
    }
  }

  private File getModuleFile(String moduleName) {
    WolConfig config = getTestenv().getConfig();
    EntityPlayerMP player = getTestPlayer();
    String path = moduleName.replace(".", File.separator) + ".lua";
    return new File(config.getOrCreateWizardConfig(player.getUniqueID()).getLibDir(), path);
  }
}
