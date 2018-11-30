package net.wizardsoflua.ai;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.Vec3d;

public class WolMobAI extends EntityAIBase {
  private final EntityLiving entity;

  private int timeoutCounter;

  /** Pos to move to */
  private Vec3d destinationPos = null;
  private double speed;
  private boolean isAtDestination;

  private boolean finished;
  private @Nullable Runnable runnable;

  public WolMobAI(EntityLiving entity) {
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
        && entity.getDistanceSq(destinationPos.x, destinationPos.y, destinationPos.z) > 1.0D;
  }

  /**
   * Execute a one shot task or start executing a continuous task
   * <p>
   * - Called second on a new task after {@link #shouldExecute()}.
   */
  @Override
  public void startExecuting() {
    entity.getNavigator().tryMoveToXYZ(destinationPos.x, destinationPos.y, destinationPos.z, speed);
    timeoutCounter = 0;
  }

  /**
   * Updates the task
   * <p>
   * - Called 3rd on a new task, and called continuously until task "terminates".
   */
  @Override
  public void updateTask() {
    double distanceSq = entity.getDistanceSq(destinationPos.x, destinationPos.y, destinationPos.z);
    if (distanceSq > 1.0D) {
      isAtDestination = false;
      ++timeoutCounter;
      if (timeoutCounter % 40 == 0) {
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
    return !isAtDestination && timeoutCounter <= 1200;
  }

  /**
   * Resets the task
   * <p>
   * - Called after {@link #continueExecuting()} returns false.
   */
  @Override
  public void resetTask() {
    destinationPos = null;
  }

  private void setFinished() {
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

}
