package creeperCZ.mobplugin.entities.ai;

import cn.nukkit.math.Vector3;
import creeperCZ.mobplugin.entities.BaseEntity;
import creeperCZ.mobplugin.util.RandomPositionGenerator;

/**
 * Created by CreeperFace on 18. 1. 2017.
 */
public class EntityAIWander extends EntityAIBase {
    private final BaseEntity entity;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private final double speed;
    private int executionChance;
    private boolean mustUpdate;

    public EntityAIWander(BaseEntity creatureIn, double speedIn) {
        this(creatureIn, speedIn, 120);
    }

    public EntityAIWander(BaseEntity creatureIn, double speedIn, int chance) {
        this.entity = creatureIn;
        this.speed = speedIn;
        this.executionChance = chance;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        if (!this.mustUpdate) {
            if (this.entity.getAge() >= 100) {
                return false;
            }

            if (this.entity.getLevel().rand.nextInt(this.executionChance) != 0) {
                return false;
            }
        }

        Vector3 pos = RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);

        if (pos == null) {
            return false;
        } else {
            this.xPosition = pos.x;
            this.yPosition = pos.y;
            this.zPosition = pos.z;
            this.mustUpdate = false;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting() {
        return !this.entity.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }

    /**
     * Makes task to bypass chance
     */
    public void makeUpdate() {
        this.mustUpdate = true;
    }

    /**
     * Changes task random possibility for execution
     */
    public void setExecutionChance(int newchance) {
        this.executionChance = newchance;
    }
}
