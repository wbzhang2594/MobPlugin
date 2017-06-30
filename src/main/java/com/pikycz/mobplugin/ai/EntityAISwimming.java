package com.pikycz.mobplugin.ai;

import com.pikycz.mobplugin.entities.BaseEntity;
import com.pikycz.mobplugin.pathfinding.PathNavigateGround;

/**
 *
 * Created by CreeperFace on 18. 1. 2017.
 */
public class EntityAISwimming extends EntityAIBase {
    private final BaseEntity theEntity;

    public EntityAISwimming(BaseEntity entitylivingIn) {
        this.theEntity = entitylivingIn;
        this.setMutexBits(4);
        ((PathNavigateGround) entitylivingIn.getNavigator()).setCanSwim(true);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        return this.theEntity.inWater || this.theEntity.inLava;
    }

    /**
     * Updates the task
     */
    public void updateTask() {
        if (this.theEntity.getLevel().rand.nextFloat() < 0.8F) {
            //this.theEntity.getJumpHelper().setJumping(); TODO: jump
        }
    }
}
