package net.wizardsoflua.testenv.player;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import net.minecraft.world.WorldServer;

public class WolFakePlayerFactory {

  // TODO can we change the ID and the Name?
  private final String fakePlayerName = "[WolFakePlayer]"; // "[Minecraft]"
  private final GameProfile fakeProfile =
      new GameProfile(UUID.fromString("41C82C87-7AfB-4024-BA57-13D2C99CAE77"), fakePlayerName);

  private List<WolFakePlayer> fakePlayers = new ArrayList<>();

  private @Nullable WorldServer currentWorld;

  public void onLoadWorld(WorldServer world) {
    this.currentWorld = world;
    System.out.println("onLoadWorld");
  }

  public void onUnloadWorld(WorldServer world) {
    System.out.println("onUnloadWorld");
    if (!fakePlayers.isEmpty()) {
      // TODO other things to do?
      fakePlayers.clear();
    }
  }

  public WolFakePlayer getFakePlayer() {
    checkNotNull(currentWorld, "currentWorld==null!");
    if (fakePlayers.isEmpty()) {
      WolFakePlayer fakePlayer = new WolFakePlayer(currentWorld, fakeProfile);
      fakePlayers.add(fakePlayer);
    }
    return fakePlayers.get(0);
  }

}
