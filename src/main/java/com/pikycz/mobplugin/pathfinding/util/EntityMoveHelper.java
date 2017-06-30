package com.pikycz.mobplugin.pathfinding.util;

import cn.nukkit.math.MathHelper;
import cn.nukkit.math.NukkitMath;
import com.pikycz.mobplugin.entities.BaseEntity;
import com.pikycz.mobplugin.pathfinding.NodeProcessor;
import com.pikycz.mobplugin.pathfinding.PathNavigate;
import com.pikycz.mobplugin.pathfinding.PathNodeType;

/**
 *
 * Created by CreeperFace on 29. 1. 2017.
 */
public class EntityMoveHelper {
    /**
     * The EntityLiving that is being moved
     */
    protected final BaseEntity entity;
    protected double posX;
    protected double posY;
    protected double posZ;

    /**
     * The speed at which the entity should move
     */
    protected double speed;
    protected float moveForward;
    protected float moveStrafe;
    protected EntityMoveHelper.Action action = EntityMoveHelper.Action.WAIT;

    public EntityMoveHelper(BaseEntity entitylivingIn) {
        this.entity = entitylivingIn;
    }

    public boolean isUpdating() {
        return this.action == EntityMoveHelper.Action.MOVE_TO;
    }

    public double getSpeed() {
        return this.speed;
    }

    /**
     * Sets the speed and location to move to
     */
    public void setMoveTo(double x, double y, double z, double speedIn) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.speed = speedIn;
        this.action = EntityMoveHelper.Action.MOVE_TO;
    }

    public void strafe(float forward, float strafe) {
        this.action = EntityMoveHelper.Action.STRAFE;
        this.moveForward = forward;
        this.moveStrafe = strafe;
        this.speed = 0.25D;
    }

    public void read(EntityMoveHelper that) {
        this.action = that.action;
        this.posX = that.posX;
        this.posY = that.posY;
        this.posZ = that.posZ;
        this.speed = Math.max(that.speed, 1.0D);
        this.moveForward = that.moveForward;
        this.moveStrafe = that.moveStrafe;
    }

    public void onUpdateMoveHelper() {
        if (this.action == EntityMoveHelper.Action.STRAFE) {
            float f = (float) this.entity.getMovementSpeed();
            float f1 = (float) this.speed * f;
            float f2 = this.moveForward;
            float f3 = this.moveStrafe;
            float f4 = MathHelper.sqrt(f2 * f2 + f3 * f3);

            if (f4 < 1.0F) {
                f4 = 1.0F;
            }

            f4 = f1 / f4;
            f2 = f2 * f4;
            f3 = f3 * f4;
            float f5 = MathHelper.sin((float) this.entity.yaw * 0.017453292F);
            float f6 = MathHelper.cos((float) this.entity.yaw * 0.017453292F);
            float f7 = f2 * f6 - f3 * f5;
            float f8 = f3 * f6 + f2 * f5;
            PathNavigate pathnavigate = this.entity.getNavigator();

            if (pathnavigate != null) {
                NodeProcessor nodeprocessor = pathnavigate.func_189566_q();

                if (nodeprocessor != null && nodeprocessor.getPathNodeType(this.entity.getLevel(), NukkitMath.floorDouble(this.entity.x + (double) f7), NukkitMath.floorDouble(this.entity.y), NukkitMath.floorDouble(this.entity.z + (double) f8)) != PathNodeType.WALKABLE) {
                    this.moveForward = 1.0F;
                    this.moveStrafe = 0.0F;
                    f1 = f;
                }
            }

            this.entity.setAIMoveSpeed(f1);
            this.entity.setMoveForward(this.moveForward);
            this.entity.setMoveStrafing(this.moveStrafe);
            this.action = EntityMoveHelper.Action.WAIT;
        } else if (this.action == EntityMoveHelper.Action.MOVE_TO) {
            this.action = EntityMoveHelper.Action.WAIT;
            double d0 = this.posX - this.entity.x;
            double d1 = this.posZ - this.entity.z;
            double d2 = this.posY - this.entity.y;
            double d3 = d0 * d0 + d2 * d2 + d1 * d1;

            if (d3 < 2.500000277905201E-7D) {
                this.entity.setMoveForward(0.0F);
                return;
            }

            float f9 = (float) (Math.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
            this.entity.yaw = this.limitAngle((float) this.entity.yaw, f9, 90.0F);
            this.entity.setAIMoveSpeed((float) (this.speed * this.entity.getMovementSpeed()));

            if (d2 > (double) this.entity.stepHeight && d0 * d0 + d1 * d1 < (double) Math.max(1.0F, this.entity.getWidth())) {
                this.entity.getJumpHelper().setJumping();
            }
        } else {
            this.entity.setMoveForward(0.0F);
        }
    }

    /**
     * Limits the given angle to a upper and lower limit.
     */
    protected float limitAngle(float p_75639_1_, float p_75639_2_, float p_75639_3_) {
        float f = Util.wrapDegrees(p_75639_2_ - p_75639_1_);

        if (f > p_75639_3_) {
            f = p_75639_3_;
        }

        if (f < -p_75639_3_) {
            f = -p_75639_3_;
        }

        float f1 = p_75639_1_ + f;

        if (f1 < 0.0F) {
            f1 += 360.0F;
        } else if (f1 > 360.0F) {
            f1 -= 360.0F;
        }

        return f1;
    }

    public double getX() {
        return this.posX;
    }

    public double getY() {
        return this.posY;
    }

    public double getZ() {
        return this.posZ;
    }

    public static enum Action {
        WAIT,
        MOVE_TO,
        STRAFE;
    }
}
