package net.wizardsoflua.testenv.player;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

public class WolFakePlayer extends FakePlayer {

  private final List<ITextComponent> receivedMessages = new ArrayList<>();

  public WolFakePlayer(WorldServer world, GameProfile name) {
    super(world, name);
  }

  public Iterable<String> getReceivedMessages() {
    return Iterables.transform(receivedMessages, ITextComponent::getUnformattedText);
  }

  @Override
  public void sendMessage(ITextComponent component) {
    receivedMessages.add(component);
  }

}
