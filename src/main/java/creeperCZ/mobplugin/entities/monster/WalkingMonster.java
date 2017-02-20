package creeperCZ.mobplugin.entities.monster;

import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import co.aikar.timings.Timings;
import creeperCZ.mobplugin.entities.WalkingEntity;
import creeperCZ.mobplugin.entities.monster.walking.Enderman;
import creeperCZ.mobplugin.entities.utils.Utils;

public abstract class WalkingMonster extends WalkingEntity implements Monster {

    public WalkingMonster(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void setTarget(Entity target) {
        this.setTarget(target, true);
    }

    public void setTarget(Entity target, boolean attack) {
        super.setTarget(target);
        this.canAttack = attack;
    }

    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (this.server.getDifficulty() < 1) {
            this.close();
            return false;
        }

        if (!this.isAlive()) {
            if (++this.deadTicks >= 23) {
                this.close();
                return false;
            }
            return true;
        }

        super.onUpdate(currentTick);
        return true;
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {

        boolean hasUpdate = false;

        Timings.entityBaseTickTimer.startTiming();

        hasUpdate = super.entityBaseTick(tickDiff);

        this.attackDelay += tickDiff;
        if (this instanceof Enderman) {
            if (this.level.getBlock(new Vector3(NukkitMath.floorDouble(this.x), (int) this.y, NukkitMath.floorDouble(this.z))) instanceof BlockWater) {
                this.attack(new EntityDamageEvent(this, EntityDamageEvent.CAUSE_DROWNING, 2));
                this.move(Utils.rand(-20, 20), Utils.rand(-20, 20), Utils.rand(-20, 20));
            }
        } else {
            if (!this.hasEffect(Effect.WATER_BREATHING) && this.isInsideOfWater()) {
                hasUpdate = true;
                int airTicks = this.getDataPropertyShort(DATA_AIR) - tickDiff;
                if (airTicks <= -20) {
                    airTicks = 0;
                    this.attack(new EntityDamageEvent(this, EntityDamageEvent.CAUSE_DROWNING, 2));
                }
                this.setDataProperty(new ShortEntityData(DATA_AIR, airTicks));
            } else {
                this.setDataProperty(new ShortEntityData(DATA_AIR, 300));
            }
        }

        Timings.entityBaseTickTimer.stopTiming();
        return hasUpdate;
    }

}
