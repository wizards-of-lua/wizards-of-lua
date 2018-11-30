package net.wizardsoflua.ai;

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

  public WolMobAI(EntityLiving entity) {
    this.entity = entity;
    this.speed = 1.0;
    this.setMutexBits(1 + 2 + 4);
  }

  public void setSpeed(double speed) {
    this.speed = speed;
  }

  public void setDestination(Vec3d pos) {
    this.destinationPos = pos;
  }

  /**
   * Returns whether the EntityAIBase should begin execution.
   * <p>
   * - Called first on a new task
   */
  public boolean shouldExecute() {
    return destinationPos != null
        && this.entity.getDistanceSq(destinationPos.x, destinationPos.y, destinationPos.z) > 1.0D;
  }

  /**
   * Execute a one shot task or start executing a continuous task
   * <p>
   * - Called second on a new task after {@link #shouldExecute()}.
   */
  public void startExecuting() {
    this.entity.getNavigator().tryMoveToXYZ(destinationPos.x, destinationPos.y, destinationPos.z,
        this.speed);
    this.timeoutCounter = 0;
  }

  /**
   * Updates the task
   * <p>
   * - Called 3rd on a new task, and called continuously until task "terminates.
   */
  public void updateTask() {
    double distanceSq =
        this.entity.getDistanceSq(destinationPos.x, destinationPos.y, destinationPos.z);
    if (distanceSq > 1.0D) {
      this.isAtDestination = false;
      ++this.timeoutCounter;
      if (this.timeoutCounter % 40 == 0) {
        this.entity.getNavigator().tryMoveToXYZ(this.destinationPos.x, this.destinationPos.y,
            this.destinationPos.z, this.speed);
      }
    } else {
      this.isAtDestination = true;
    }
  }

  /**
   * Returns whether an in-progress EntityAIBase should continue executing
   * <p>
   * - Called after {@link #updateTask()} and if it returns true {@link #updateTask()} is executed
   * again.
   */
  public boolean continueExecuting() {
    return !isAtDestination && this.timeoutCounter <= 1200;
  }

  /**
   * Resets the task
   * <p>
   * - Called after {@link #continueExecuting()} returns false.
   */
  public void resetTask() {
    destinationPos = null;
  }

  protected boolean isAtDestination() {
    return this.isAtDestination;
  }

}
