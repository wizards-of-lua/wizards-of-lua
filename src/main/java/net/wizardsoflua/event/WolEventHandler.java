package net.wizardsoflua.event;

import static com.google.common.base.Preconditions.checkNotNull;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.spell.SpellEntity;

public class WolEventHandler {

  private static final String WOL_PACKET_HANDLER_NAME = WizardsOfLua.MODID + ":packet_handler";
  private static final String VANILLA_PACKET_HANDLER_NAME = "packet_handler";

  public interface Context {
    Iterable<SpellEntity> getSpells();

    boolean isSupportedLuaEvent(Event event);

    String getEventName(Event event);
  }

  private final Context context;

  public WolEventHandler(Context context) {
    this.context = checkNotNull(context, "context==null!");
  }

  @SubscribeEvent
  public void onEvent(Event event) {
    if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
      return;
    }
    if (context.isSupportedLuaEvent(event)) {
      Iterable<SpellEntity> spells = context.getSpells();
      for (SpellEntity spellEntity : spells) {
        String eventName = context.getEventName(event);
        spellEntity.getProgram().getEventHandlers().onEvent(eventName, event);
      }
    }
  }

  @SubscribeEvent
  public void onPlayerRespawnEvent(PlayerRespawnEvent evt) {
    if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
      return;
    }
    EntityPlayerMP player = (EntityPlayerMP) evt.player;
    addWolPacketHandler(player);
  }

  @SubscribeEvent
  public void onPlayerLoggedIn(PlayerLoggedInEvent evt) {
    if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
      return;
    }
    EntityPlayerMP player = (EntityPlayerMP) evt.player;
    addWolPacketHandler(player);
  }

  private void addWolPacketHandler(EntityPlayerMP player) {
    NetworkManager networkManager = player.connection.getNetworkManager();
    Channel channel = networkManager.channel();
    ChannelPipeline pipeline = channel.pipeline();
    WolChannelInboundHandlerAdapter handler = pipeline.get(WolChannelInboundHandlerAdapter.class);
    if (handler == null) {
      handler = new WolChannelInboundHandlerAdapter(player);
      pipeline.addBefore(VANILLA_PACKET_HANDLER_NAME, WOL_PACKET_HANDLER_NAME, handler);
    } else {
      // This is essential when a player respawns.
      handler.setPlayer(player);
    }
  }

}
