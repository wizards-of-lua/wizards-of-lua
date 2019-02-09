package net.wizardsoflua.ai;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.Vec3d;

public class WolMobAI extends EntityAIBase {
  private static final double MAX_OFFSET = 0.3D * 0.3D;
  private final WolMobAIRegistry wolMobAIRegistry;
  private final EntityLiving entity;

  private int age;

  /** Pos to move to */
  private Vec3d destinationPos = null;
  private double speed;
  private boolean isAtDestination;

  private boolean finished;
  private @Nullable Runnable runnable;


  public WolMobAI(WolMobAIRegistry wolMobAIRegistry, EntityLiving entity) {
    this.wolMobAIRegistry = wolMobAIRegistry;
    this.entity = entity;
    speed = 1.0;
    setMutexBits(1 + 2 + 4);
  }

  public void setSpeed(double speed) {
    this.speed = speed;
  }

  public void setDestination(Vec3d pos) {
    destinationPos = pos;
  }

  /**
   * Returns whether the EntityAIBase should begin execution.
   * <p>
   * - Called first on a new task
   */
  @Override
  public boolean shouldExecute() {
    return destinationPos != null
        && entity.getDistanceSq(destinationPos.x, destinationPos.y, destinationPos.z) > MAX_OFFSET;
  }

  /**
   * Execute a one shot task or start executing a continuous task
   * <p>
   * - Called second on a new task after {@link #shouldExecute()}.
   */
  @Override
  public void startExecuting() {
    entity.getNavigator().tryMoveToXYZ(destinationPos.x, destinationPos.y, destinationPos.z, speed);
    age = 0;
    finished = false;
    isAtDestination = false;
  }

  /**
   * Updates the task
   * <p>
   * - Called 3rd on a new task, and called continuously until task "terminates".
   */
  @Override
  public void updateTask() {
    double distanceSq = entity.getDistanceSq(destinationPos.x, destinationPos.y, destinationPos.z);
    if (distanceSq > MAX_OFFSET) {
      isAtDestination = false;
      ++age;
      if (age % 40 == 0) {
        entity.getNavigator().tryMoveToXYZ(destinationPos.x, destinationPos.y, destinationPos.z,
            speed);
      }
    } else {
      isAtDestination = true;
      setFinished();
    }
  }


  /**
   * Returns whether an in-progress EntityAIBase should continue executing
   * <p>
   * - Called after {@link #updateTask()} and if it returns true {@link #updateTask()} is executed
   * again.
   */
  @Override
  public boolean shouldContinueExecuting() {
    return !isAtDestination;
  }

  /**
   * Resets the task
   * <p>
   * - Called after {@link #continueExecuting()} returns false.
   */
  @Override
  public void resetTask() {
    destinationPos = null;
    isAtDestination = false;
  }

  private void setFinished() {
    wolMobAIRegistry.removeMobAi(entity);
    finished = true;
    if (runnable != null) {
      runnable.run();
    }
  }

  public boolean isFinished() {
    return finished;
  }

  public void setOnFinished(Runnable runnable) {
    this.runnable = runnable;
  }

  public void terminate() {
    setFinished();
  }

}
