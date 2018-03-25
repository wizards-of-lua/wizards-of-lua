package net.wizardsoflua.testenv.player;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;
import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraft.inventory.EntityEquipmentSlot.OFFHAND;
import static net.minecraft.item.ItemStack.EMPTY;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

import javax.annotation.Nullable;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wizardsoflua.spell.SpellUtil;
import net.wizardsoflua.testenv.WolTestEnvironment;
import net.wizardsoflua.testenv.net.ChatAction;
import net.wizardsoflua.testenv.net.ClientAction;
import net.wizardsoflua.testenv.net.LeftClickAction;
import net.wizardsoflua.testenv.net.ReconnectAction;
import net.wizardsoflua.testenv.net.RespawnAction;
import net.wizardsoflua.testenv.net.RightClickAction;

public class PlayerBackdoor {
  private WolTestEnvironment testEnv;

  public PlayerBackdoor(WolTestEnvironment testEnv) {
    this.testEnv = testEnv;
  }

  public EntityPlayerMP getDelegate() {
    EntityPlayerMP testPlayer = testEnv.getTestPlayer();
    checkNotNull(testPlayer, "testPlayer==null!");
    return testPlayer;
  }

  public void leftclick(BlockPos pos, EnumFacing face) {
    perform(new LeftClickAction(pos, face));
  }

  public void rightclick(BlockPos pos, EnumFacing face) {
    rightclick(pos, face, new Vec3d(pos));
  }

  public void rightclick(BlockPos pos, EnumFacing face, Vec3d vec) {
    perform(new RightClickAction(pos, face, vec));
  }

  public void chat(String format, Object... args) {
    perform(new ChatAction(format, args));
  }

  public void perform(ClientAction action) {
    testEnv.runAndWait(() -> testEnv.getPacketDispatcher().sendTo(action, getDelegate()));
  }

  public void setPosition(BlockPos pos) {
    testEnv
        .runAndWait(() -> getDelegate().setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ()));
  }

  public void setRotationYaw(float yaw) {
    testEnv.runAndWait(() -> {
      EntityPlayerMP delegate = getDelegate();
      delegate.setRotationYawHead(yaw);
      delegate.setRenderYawOffset(yaw);
      delegate.connection.setPlayerLocation(delegate.posX, delegate.posY, delegate.posZ,
          delegate.rotationYaw, delegate.rotationPitch);
    });
  }

  public BlockPos getBlockPos() {
    return getDelegate().getPosition();
  }

  public Vec3d getPositionLookingAt() {
    Vec3d result = SpellUtil.getPositionLookingAt(getDelegate());
    return result;
  }

  public BlockPos getBlockPosLookingAt() {
    return new BlockPos(SpellUtil.getPositionLookingAt(getDelegate()));
  }

  public void setTeam(String team) {
    getDelegate().getWorldScoreboard().addPlayerToTeam(getDelegate().getName(), team);
  }

  public @Nullable String getTeam() {
    Team team = getDelegate().getTeam();
    if (team == null) {
      return null;
    } else {
      return team.getName();
    }
  }

  public EnumFacing getFacing() {
    return getDelegate().getHorizontalFacing();
  }

  public void createModule(String moduleName, String content) {
    File moduleFile = getModuleFile(moduleName);
    if (moduleFile.exists()) {
      moduleFile.delete();
    }
    moduleFile.getParentFile().mkdirs();
    try {
      Files.write(content, moduleFile, Charsets.UTF_8);
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
    return new File(testEnv.getWol().getConfig()
        .getOrCreateWizardConfig(getDelegate().getUniqueID()).getLibDir(), path);
  }

  public String getName() {
    return getDelegate().getName();
  }

  public void setMainHandItem(ItemStack item) {
    testEnv.runAndWait(() -> {
      getDelegate().setItemStackToSlot(MAINHAND, ofNullable(item).orElse(EMPTY));
      getDelegate().inventoryContainer.detectAndSendChanges();
    });
  }

  public ItemStack getMainHandItem() {
    return getDelegate().getHeldItemMainhand();
  }

  public void setOffHandItem(ItemStack item) {
    testEnv.runAndWait(() -> {
      getDelegate().setItemStackToSlot(OFFHAND, ofNullable(item).orElse(EMPTY));
      getDelegate().inventoryContainer.detectAndSendChanges();
    });
  }

  public ItemStack getOffHandItem() {
    return getDelegate().getHeldItemOffhand();
  }

  public void changeDimension(int dim) {
    testEnv.runAndWait(() -> {
      getDelegate().getEntityWorld().getMinecraftServer().getPlayerList()
          .changePlayerDimension(getDelegate(), dim);
    });
  }

  public void reconnect() {
    perform(new ReconnectAction());
  }

  public void respawn() {
    perform(new RespawnAction());
  }

  public void waitForPlayer(long duration) {
    long started = System.currentTimeMillis();
    while (testEnv.getTestPlayer() == null) {
      if (started + duration > System.currentTimeMillis()) {
        sleep(100);
      } else {
        throw new RuntimeException("Timeout! Testplayer not available within " + duration + " ms");
      }
    }
  }

  private void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }


}
