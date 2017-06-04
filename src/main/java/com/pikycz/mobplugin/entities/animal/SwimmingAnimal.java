package com.pikycz.mobplugin.entities.animal;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import static cn.nukkit.entity.Entity.DATA_AIR;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import co.aikar.timings.Timings;
import com.pikycz.mobplugin.entities.SwimmingEntity;

/**
 *
 * @author PikyCZ
 */
abstract class SwimmingAnimal extends SwimmingEntity implements Animal {

    public SwimmingAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public double getSpeed() {
        return 1.0;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        if (this.getDataFlag(DATA_FLAG_BABY, 0)) {
            this.setDataFlag(DATA_FLAG_BABY, DATA_TYPE_BYTE);
        }
    }

    @Override
    public boolean isBaby() {
        return this.getDataFlag(DATA_FLAG_BABY, 0);
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {

        Timings.entityBaseTickTimer.startTiming();

        boolean hasUpdate = false;

        if (!this.hasEffect(Effect.WATER_BREATHING) && this.isInsideOfWater()) {
            hasUpdate = true;
            int airTicks = this.getDataPropertyShort(DATA_AIR) - tickDiff;
            if (airTicks <= -20) {
                airTicks = 0;
                this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.DROWNING, 2));
            }
            this.setDataProperty(new ShortEntityData(DATA_AIR, airTicks));
        } else {
            this.setDataProperty(new ShortEntityData(DATA_AIR, 300));
        }

        Timings.entityBaseTickTimer.stopTiming();

        return hasUpdate;
    }

    @Override
    public boolean onUpdate(int tickDiff) {
        if (!this.isAlive()) {
            if (++this.deadTicks >= 230) {
                this.close();
                return false;
            }
            return true;
        }

        tickDiff = this.lastUpdate;
        this.lastUpdate = tickDiff;
        this.entityBaseTick(tickDiff);

        target = this.updateMove(tickDiff);
        if (target instanceof Player) {
            if (this.distance(target) <= 2) {
                this.pitch = 22;
                this.x = this.lastX;
                this.y = this.lastY;
                this.z = this.lastZ;
            }
        } else if (target instanceof Vector3
                && this.distance(target) <= 1) {
            this.moveTime = 0;
        }
        return true;
    }

}
