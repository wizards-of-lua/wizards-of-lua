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
import javax.annotation.Nullable;
import com.google.common.io.Files;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
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

  public String getName() {
    return getTestenv().callOnMainThread(() -> {
      return getTestPlayer().getName().getString();
    });
  }

  public float getHealth() {
    return getTestenv().callOnMainThread(() -> {
      return getTestPlayer().getHealth();
    });
  }

  public void setHealth(float value) {
    getTestenv().runChangeOnMainThread(() -> {
      getTestPlayer().setHealth(value);
    });
  }

  public BlockPos getPosition() {
    return getTestenv().callOnMainThread(() -> {
      return getTestPlayer().getPosition();
    });
  }

  public void setPosition(BlockPos pos) {
    getTestenv().runChangeOnMainThread(() -> {
      getTestPlayer().setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
    });
  }

  public EnumFacing getFacing() {
    return getTestenv().callOnMainThread(() -> {
      return getTestPlayer().getHorizontalFacing();
    });
  }

  public Vec3d getPositionLookingAt() {
    return getTestenv().callOnMainThread(() -> {
      return SpellUtil.getPositionLookingAt(getTestPlayer());
    });
  }

  public void setRotationYaw(float yaw) {
    getTestenv().runChangeOnMainThread(() -> {
      EntityPlayerMP player = getTestPlayer();
      player.setRotationYawHead(yaw);
      player.setRenderYawOffset(yaw);
      player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw,
          player.rotationPitch);
    });
  }

  public @Nullable String getTeam() {
    return getTestenv().callOnMainThread(() -> {
      EntityPlayerMP player = getTestPlayer();
      return Optional.ofNullable(player.getTeam()).map(it -> it.getName()).orElse(null);
    });
  }

  public void setTeam(String teamName) {
    getTestenv().runOnMainThread(() -> {
      EntityPlayerMP player = getTestPlayer();
      Scoreboard scoreboard = player.getWorldScoreboard();
      ScorePlayerTeam team = scoreboard.getTeam(teamName);
      scoreboard.addPlayerToTeam(player.getScoreboardName(), team);
    });
  }

  public ItemStack getMainHandItem() {
    return getTestenv().callOnMainThread(() -> {
      return getTestPlayer().getHeldItemMainhand();
    });
  }

  public void setMainHandItem(ItemStack item) {
    getTestenv().runChangeOnMainThread(() -> {
      getTestPlayer().setItemStackToSlot(MAINHAND, ofNullable(item).orElse(EMPTY));
      getTestPlayer().inventoryContainer.detectAndSendChanges();
    });
  }

  public ItemStack getOffHandItem() {
    return getTestenv().callOnMainThread(() -> {
      return getTestPlayer().getHeldItemOffhand();
    });
  }

  public void setOffHandItem(ItemStack item) {
    getTestenv().runChangeOnMainThread(() -> {
      EntityPlayerMP player = getTestPlayer();
      player.setItemStackToSlot(OFFHAND, ofNullable(item).orElse(EMPTY));
      player.inventoryContainer.detectAndSendChanges();
    });
  }

  public void changeDimension(DimensionType dim) {
    getTestenv().runChangeOnMainThread(() -> {
      getTestenv().getServer().getPlayerList().changePlayerDimension(getTestPlayer(), dim);
    });
  }

  public void setOperator(boolean value) {
    if (isOperator() == value) {
      return;
    }
    if (value) {
      minecraftBackdoor.executeCommand("/op " + getName());
    } else {
      minecraftBackdoor.executeCommand("/deop " + getName());
    }
  }

  public boolean isOperator() {
    return getTestenv().callOnMainThread(() -> {
      return getTestenv().getPermissions().hasOperatorPrivileges(getTestPlayer().getUniqueID());
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
    String path = moduleName.replace(".", File.separator) + ".lua";
    return new File(
        getTestenv().getConfig().getOrCreateWizardConfig(getTestPlayer().getUniqueID()).getLibDir(),
        path);
  }
}
