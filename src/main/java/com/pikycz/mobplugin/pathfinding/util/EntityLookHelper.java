package com.pikycz.mobplugin.pathfinding.util;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import com.pikycz.mobplugin.entities.BaseEntity;

/**
 *
 * Created by CreeperFace on 29. 1. 2017.
 */
public class EntityLookHelper {
    private final BaseEntity entity;

    /**
     * The amount of change that is made each update for an entity facing a direction.
     */
    private float deltaLookYaw;

    /**
     * The amount of change that is made each update for an entity facing a direction.
     */
    private float deltaLookPitch;

    /**
     * Whether or not the entity is trying to look at something.
     */
    private boolean isLooking;
    private double posX;
    private double posY;
    private double posZ;

    public EntityLookHelper(BaseEntity entitylivingIn) {
        this.entity = entitylivingIn;
    }

    /**
     * Sets position to look at using entity
     */
    public void setLookPositionWithEntity(Entity entityIn, float deltaYaw, float deltaPitch) {
        this.posX = entityIn.x;

        if (entityIn instanceof EntityLiving) {
            this.posY = entityIn.y + (double) entityIn.getEyeHeight();
        } else {
            this.posY = (entityIn.getBoundingBox().minY + entityIn.getBoundingBox().maxY) / 2.0D;
        }

        this.posZ = entityIn.z;
        this.deltaLookYaw = deltaYaw;
        this.deltaLookPitch = deltaPitch;
        this.isLooking = true;
    }

    /**
     * Sets position to look at
     */
    public void setLookPosition(double x, double y, double z, float deltaYaw, float deltaPitch) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.deltaLookYaw = deltaYaw;
        this.deltaLookPitch = deltaPitch;
        this.isLooking = true;
    }

    /**
     * Updates look
     */
    public void onUpdateLook() {
        this.entity.pitch = 0.0F;

        if (this.isLooking) {
            this.isLooking = false;
            double d0 = this.posX - this.entity.x;
            double d1 = this.posY - (this.entity.y + (double) this.entity.getEyeHeight());
            double d2 = this.posZ - this.entity.z;
            double d3 = (double) Math.sqrt(d0 * d0 + d2 * d2);
            float f = (float) (Math.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
            float f1 = (float) (-(Math.atan2(d1, d3) * (180D / Math.PI)));
            this.entity.pitch = this.updateRotation((float) this.entity.pitch, f1, this.deltaLookPitch);
            this.entity.headYaw = this.updateRotation(this.entity.headYaw, f, this.deltaLookYaw);
        } else {
            //this.entity.headYaw = this.updateRotation(this.entity.headYaw, this.entity.renderYawOffset, 10.0F);
            this.entity.headYaw = this.updateRotation(this.entity.headYaw, (float) this.entity.yaw, 10.0F);
        }

        float f2 = Util.wrapDegrees(this.entity.headYaw - (float) this.entity.yaw);

        if (!this.entity.getNavigator().noPath()) {
            if (f2 < -75.0F) {
                this.entity.headYaw = (float) this.entity.yaw - 75.0F;
            }

            if (f2 > 75.0F) {
                this.entity.headYaw = (float) this.entity.yaw + 75.0F;
            }
        }
    }

    private float updateRotation(float p_75652_1_, float p_75652_2_, float p_75652_3_) {
        float f = Util.wrapDegrees(p_75652_2_ - p_75652_1_);

        if (f > p_75652_3_) {
            f = p_75652_3_;
        }

        if (f < -p_75652_3_) {
            f = -p_75652_3_;
        }

        return p_75652_1_ + f;
    }

    public boolean getIsLooking() {
        return this.isLooking;
    }

    public double getLookPosX() {
        return this.posX;
    }

    public double getLookPosY() {
        return this.posY;
    }

    public double getLookPosZ() {
        return this.posZ;
    }
}