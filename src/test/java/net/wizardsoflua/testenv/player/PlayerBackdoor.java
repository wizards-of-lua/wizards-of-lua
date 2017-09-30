package net.wizardsoflua.testenv.player;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

import javax.annotation.Nullable;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wizardsoflua.spell.SpellUtil;
import net.wizardsoflua.testenv.WolTestEnvironment;
import net.wizardsoflua.testenv.net.ClientAction;

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

  public void perform(ClientAction action) {
    testEnv.runAndWait(() -> testEnv.getPacketDispatcher().sendTo(action, getDelegate()));
  }

  public void setPosition(BlockPos pos) {
    testEnv
        .runAndWait(() -> getDelegate().setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ()));
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
      return team.getRegisteredName();
    }
  }

  public EnumFacing getOrientation() {
    return getDelegate().getHorizontalFacing();
  }

  public void setProfile(String module) {
    testEnv.getWol().getConfig().getUserConfig(getDelegate()).setProfile(module);
  }

  public String getPlayerProfile() {
    return testEnv.getWol().getConfig().getUserConfig(getDelegate()).getProfile();
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
    String path = moduleName.replace('.', File.separatorChar) + ".lua";
    File moduleFile = new File(testEnv.getWol().getLuaHomeDir(getDelegate()), path);
    return moduleFile;
  }


}
