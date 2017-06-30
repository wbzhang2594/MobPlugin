package com.pikycz.mobplugin.pathfinding.util;

import com.pikycz.mobplugin.entities.BaseEntity;

/**
 *
 * Created by CreeperFace on 29. 1. 2017
 */
public class EntityJumpHelper {
    private final BaseEntity entity;
    protected boolean isJumping;

    public EntityJumpHelper(BaseEntity entityIn) {
        this.entity = entityIn;
    }

    public void setJumping() {
        this.isJumping = true;
    }

    /**
     * Called to actually make the entity jump if isJumping is true.
     */
    public void doJump() {
        this.entity.setJumping(this.isJumping);
        this.isJumping = false;
    }
}
