package com.pikycz.mobplugin.entities;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 *
 * @author PikyCZ
 */
public abstract class SwimmingEntity extends BaseEntity {

    public SwimmingEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

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
        return null;
    }

}
