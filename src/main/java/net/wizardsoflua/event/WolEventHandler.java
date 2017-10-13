package net.wizardsoflua.event;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.wizardsoflua.spell.SpellEntity;

public class WolEventHandler {

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

}
