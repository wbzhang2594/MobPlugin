package creeperCZ.mobplugin.entities.animal.walking;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.food.Food;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;
import creeperCZ.mobplugin.MobPlugin;
import creeperCZ.mobplugin.entities.animal.Tameable;

public class Wolf extends Tameable {

    public static final int NETWORK_ID = 14;

    private static final String NBT_KEY_ANGRY = "Angry";

    private static final String NBT_KEY_COLLAR_COLOR = "CollarColor";

    private boolean angry = false;

    protected boolean sitting = false;

    private DyeColor collarColor = DyeColor.RED; // red is default

    public Wolf(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.72f;
    }

    @Override
    public float getHeight() {
        return 0.9f;
    }

    @Override
    public double getSpeed() {
        return 1.2;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains(NBT_KEY_ANGRY)) {
            this.angry = this.namedTag.getBoolean(NBT_KEY_ANGRY);
        }

        if (this.namedTag.contains(NBT_KEY_COLLAR_COLOR)) {
            this.collarColor = DyeColor.getByDyeData(this.namedTag.getInt(NBT_KEY_COLLAR_COLOR));
        }

        this.setMaxHealth(8);
        this.fireProof = true;
        this.setDamage(new int[]{0, 3, 4, 6});
        this.setCollarColor(this.collarColor);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putBoolean(NBT_KEY_ANGRY, this.angry);
        this.namedTag.putInt(NBT_KEY_COLLAR_COLOR, this.collarColor.getDyeData());
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        if (!this.isSitting() && this.isTamed() && this.getOwner() != null) {
            double distance = this.playerOwner.distance(this);

            if (distance >= 10) {
                if (distance >= 12) {
                    int i = NukkitMath.floorDouble(this.playerOwner.x) - 2;
                    int j = NukkitMath.floorDouble(this.playerOwner.z) - 2;
                    int k = NukkitMath.floorDouble(this.playerOwner.getBoundingBox().minY);

                    for (int l = 0; l <= 4; ++l) {
                        for (int i1 = 0; i1 <= 4; ++i1) {
                            if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && Block.solid[this.level.getBlockIdAt(i + l, k - 1, j + i1)] && this.level.getBlock(new Vector3(i + l, k, j + i1)).canPassThrough() && this.level.getBlock(new Vector3(i + l, k + 1, j + i1)).canPassThrough()) {
                                this.teleport(new Location(i + l + 0.5F, k, j + i1 + 0.5F, this.yaw, this.pitch));
                                this.setTarget(null);
                            }
                        }
                    }
                } else {
                    this.isFollowingOwner = true;
                    this.setTarget(this.playerOwner);
                }
            } else if (this.isFollowingOwner) {
                this.isFollowingOwner = false;

                if (this.getTarget() != null && this.getTarget().getId() == this.playerOwner.getId()) {
                    this.setTarget(null);
                }
            }
        }

        return super.entityBaseTick(tickDiff);
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        return this.isAngry() && super.targetOption(creature, distance);
    }

    public boolean isAngry() {
        return this.angry;
    }

    public void setAngry(boolean angry) {
        this.angry = angry;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ANGRY, angry);
    }

    @Override
    public void attack(EntityDamageEvent ev) {
        super.attack(ev);

        if (!ev.isCancelled() && ev instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) ev).getDamager() instanceof Player) {
            if (!isTamed()) {
                this.setAngry(true);
            } else if (getOwner() == null || !((EntityDamageByEntityEvent) ev).getDamager().getName().toLowerCase().equals(this.owner)) {
                this.setAttackTarget(((EntityDamageByEntityEvent) ev).getDamager());
            }
        }
    }

    @Override
    public void attackEntity(Entity player) {
        if (MobPlugin.MOB_AI_ENABLED) {
            if (this.attackDelay > 10 && this.distanceSquared(player) < 1.6) {
                this.attackDelay = 0;

                player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, getDamage()));
            }
        }
    }

    @Override
    public Item[] getDrops() {
        return new Item[0];
    }

    @Override
    public int getKillExperience() {
        return 3; // gain 3 experience
    }

    /**
     * Sets the color of the wolves collar (default is 14)
     *
     * @param color
     */
    public void setCollarColor(DyeColor color) {
        this.namedTag.putInt(NBT_KEY_COLLAR_COLOR, color.getDyeData());
        this.setDataProperty(new IntEntityData(DATA_COLOUR, color.getColor().getRGB()));
        this.collarColor = color;
    }

    @Override
    public boolean isBreedingItem(Item item) {
        return Food.getByRelative(item) != null;
    }

    @Override
    public boolean canAttack(Entity entity) {
        return this.isAngry();
    }

    @Override
    public boolean onInteract(Entity entity, Item item) {
        if (this.isTamed()) {
            if (item instanceof ItemDye) {
                this.setCollarColor(((ItemDye) item).getDyeColor());
                return true;
            } else if (this.getHealth() < this.getMaxHealth() && isBreedingItem(item)) {
                this.heal(new EntityRegainHealthEvent(this, Food.getByRelative(item).getRestoreFood(), EntityRegainHealthEvent.CAUSE_EATING));
                return true;
            } else if (!isBreedingItem(item)) {
                this.setSitting(!this.sitting);
                return true;
            }
        }

        return super.onInteract(entity, item);
    }

    @Override
    public boolean isTameItem(Item item) {
        return item.getId() == Item.BONE;
    }

    @Override
    public void setTamed(Player owner) {
        super.setTamed(owner);

        this.setSitting(true);
    }

    public void setSitting(boolean sitting) {
        this.sitting = sitting;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SITTING, sitting);
    }

    public boolean isSitting() {
        return this.sitting;
    }
}
