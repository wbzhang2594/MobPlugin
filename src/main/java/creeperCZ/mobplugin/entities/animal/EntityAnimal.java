package creeperCZ.mobplugin.entities.animal;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.HeartParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.timings.Timings;
import creeperCZ.mobplugin.entities.EntityAgeable;

/**
 * Created by CreeperFace on 18. 1. 2017.
 */
public abstract class EntityAnimal extends EntityAgeable {

    protected int inLoveTicks = 0;
    protected int spawnBabyDelay = 0; //TODO: spawn baby animal

    public EntityAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public double getSpeed() {
        return 0.8;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean hasUpdate;
        Timings.entityBaseTickTimer.startTiming();

        hasUpdate = super.entityBaseTick(tickDiff);

        if (inLoveTicks > 0) {
            inLoveTicks--;

            if (inLoveTicks % 10 == 0) {
                Vector3 pos = new Vector3(this.x + (double) (this.level.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.y + 0.5D + (double) (this.level.rand.nextFloat() * this.getHeight()), this.z + (double) (this.level.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth());
                this.level.addParticle(new HeartParticle(pos));
            }
        }

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

        Timings.entityBaseTickTimer.stopTiming();
        return hasUpdate;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (!this.isAlive()) {
            if (++this.deadTicks >= 23) {
                this.close();
                return false;
            }
            return true;
        }

        int tickDiff = currentTick - this.lastUpdate;
        this.lastUpdate = currentTick;
        this.entityBaseTick(tickDiff);

        return true;
    }

    @Override
    public boolean onInteract(Entity entity, Item item) {
        //TODO: mating

        return false;
    }

    public void setInLove() {
        this.inLoveTicks = 600;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_INLOVE);
    }

    public boolean isBreedingItem(Item item) {
        return item != null && item.getId() == Item.WHEAT;
    }

    public float getBlockPathWeight(Vector3 pos) {
        pos = pos.getSide(Vector3.SIDE_DOWN);

        return this.level.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()) == Block.GRASS ? 10.0F : this.level.getFullLight(pos) - 0.5F;
    }
}
