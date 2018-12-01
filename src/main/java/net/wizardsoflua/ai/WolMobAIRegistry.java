package net.wizardsoflua.ai;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WolMobAIRegistry {

  private final Map<EntityLiving, WolMobAI> map = new HashMap<>();

  @SubscribeEvent
  public void onLivingDeathEvent(LivingDeathEvent evt) {
    if (evt.getEntityLiving() instanceof EntityLiving) {
      EntityLiving entity = (EntityLiving) evt.getEntityLiving();
      WolMobAI ai = map.remove(entity);
      if (ai != null) {
        ai.terminate();
        for (EntityAITaskEntry e : entity.tasks.taskEntries) {
          if (e.action == ai) {
            entity.tasks.removeTask(ai);
            return;
          }
        }
      }
    }
  }

  void removeMobAi(EntityLiving entity) {
    map.remove(entity);
  }

  public WolMobAI getWolMobAi(EntityLiving entity) {
    WolMobAI result = map.computeIfAbsent(entity, (e) -> {
      return new WolMobAI(this, e);
    });
    for (EntityAITaskEntry e : entity.tasks.taskEntries) {
      if (e.action == result) {
        return result;
      }
    }
    entity.tasks.addTask(1, result);
    return result;
  }
}
