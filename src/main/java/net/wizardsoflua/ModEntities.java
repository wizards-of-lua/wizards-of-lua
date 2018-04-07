package net.wizardsoflua;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;
import net.wizardsoflua.spell.SpellEntity;

public class ModEntities {
  static int trackingRange = 0;
  static int updateFrequency = 1;
  static boolean sendsVelocityUpdates = false;

  public static final Set<EntityEntry> SET_ENTITIES = ImmutableSet.of( //
      EntityEntryBuilder.create() //
          .entity(SpellEntity.class) //
          .id(SpellEntity.RES_LOCATION, SpellEntity.ID) //
          .name(SpellEntity.NAME) //
          .tracker(trackingRange, updateFrequency, sendsVelocityUpdates) //
          .build() //
  );

  @EventBusSubscriber(modid = WizardsOfLua.MODID)
  public static class RegistrationHandler {
    /**
     * Register this mod's {@link EntityEntry}s.
     *
     * @param event The event
     */
    @SubscribeEvent
    public static void onEvent(final RegistryEvent.Register<EntityEntry> event) {
      IForgeRegistry<EntityEntry> registry = event.getRegistry();
      for (final EntityEntry entityEntry : SET_ENTITIES) {
        registry.register(entityEntry);
      }
    }

  }
}
