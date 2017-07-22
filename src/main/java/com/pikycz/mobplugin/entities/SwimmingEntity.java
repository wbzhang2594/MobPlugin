package com.pikycz.mobplugin.entities;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import com.pikycz.mobplugin.entities.animal.Animal;
import com.pikycz.mobplugin.utils.Utils;

/**
 *
 * @author PikyCZ
 */
public abstract class SwimmingEntity extends BaseEntity {

    public SwimmingEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    protected void checkTarget() {
        if (this.isKnockback()) {
            return;
        }

        if (this.followTarget != null && !this.followTarget.closed && this.followTarget.isAlive()) {
            return;
        }

        Vector3 target = this.target;
        if (!(target instanceof EntityCreature) || !this.targetOption((EntityCreature) target, this.distanceSquared(target))) {
            double near = Integer.MAX_VALUE;

            for (Entity entity : this.getLevel().getEntities()) {
                if (entity == this || !(entity instanceof EntityCreature) || entity instanceof Animal) {
                    continue;
                }

                EntityCreature creature = (EntityCreature) entity;
                if (creature instanceof BaseEntity && ((BaseEntity) creature).isFriendly() == this.isFriendly()) {
                    continue;
                }

                double distance = this.distanceSquared(creature);
                if (distance > near || !this.targetOption(creature, distance)) {
                    continue;
                }
                near = distance;

                this.stayTime = 0;
                this.moveTime = 0;
                this.target = creature;
            }
        }

        if (this.target instanceof EntityCreature && !((EntityCreature) this.target).closed && ((EntityCreature) this.target).isAlive()) {
            return;
        }

        int x, z;
        if (this.stayTime > 0) {
            if (Utils.rand(1, 100) > 5) {
                return;
            }
            x = Utils.rand(10, 30);
            z = Utils.rand(10, 30);
            this.target = this.add(Utils.rand() ? x : -x, Utils.rand(-20, 20) / 10, Utils.rand() ? z : -z);
        } else if (Utils.rand(1, 410) == 1) {
            x = Utils.rand(10, 30);
            z = Utils.rand(10, 30);
            this.stayTime = Utils.rand(90, 400);
            this.target = this.add(Utils.rand() ? x : -x, Utils.rand(-20, 20) / 10, Utils.rand() ? z : -z);
        } else if (this.moveTime <= 0 || this.target == null) {
            x = Utils.rand(20, 100);
            z = Utils.rand(20, 100);
            this.stayTime = 0;
            this.moveTime = Utils.rand(300, 1200);
            this.target = this.add(Utils.rand() ? x : -x, 0, Utils.rand() ? z : -z);
        }

    }

    @Override
    public Vector3 updateMove(int tickDiff) {
        if (!this.isMovement()) {
            return null;
        }

        if (this.isKnockback()) {
            this.move(this.motionX * tickDiff, this.motionY * tickDiff, this.motionZ * tickDiff);
            this.motionY -= 0.2 * tickDiff;
            this.updateMovement();
            return null;
        }

        Vector3 before = this.target;
        this.checkTarget();
        if (this.target instanceof EntityCreature || before != this.target) {
            x = this.getTarget().x - this.x;
            y = this.getTarget().y - this.y;
            z = this.getTarget().z - this.z;
            double diff = Math.abs(x) + Math.abs(z);

            if (x * 2 + z * 2 < 0.7) {
                this.motionX = 0;
                this.motionZ = 0;
            } else {
                this.motionX = this.getSpeed() * 0.15 * (x / tickDiff);
                this.motionZ = this.getSpeed() * 0.15 * (z / tickDiff);
            }

            this.yaw = -Math.atan2(x / tickDiff, z / tickDiff) * 180;
            this.pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * 2 + z * 2)));
        }

        double dx = this.motionX * tickDiff;
        double dz = this.motionZ * tickDiff;
        if (this.stayTime > 0) {
            this.stayTime -= tickDiff;
            this.move(0, this.motionY * tickDiff, 0);
        } else {
            Vector2 be = new Vector2(this.x + dx, this.z + dz);
            this.move(dx, this.motionY * tickDiff, dz);
            Vector2 af = new Vector2(this.x, this.z);
            if ((be.x != af.x || be.y != af.y)) {
                this.moveTime -= 90 * tickDiff;
            }
        }

        this.updateMovement();
        return this.getTarget();
    }

}
