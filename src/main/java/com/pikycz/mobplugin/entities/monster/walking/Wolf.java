package com.pikycz.mobplugin.entities.monster.walking;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;
import com.pikycz.mobplugin.MobPlugin;
import com.pikycz.mobplugin.entities.monster.TameableMonster;

public class Wolf extends TameableMonster {

    public static final int NETWORK_ID = 14;

    private static final String NBT_KEY_ANGRY = "Angry"; //0 - not angry, > 0 angry

    //private static final String NBT_KEY_SITTING = "Sitting"; // 1 or 0 (true/false)
    private static final String NBT_KEY_COLLAR_COLOR = "CollarColor"; // 0 -14 (14 - RED)

    //private static final String NBT_SERVER_KEY_OWNER_NAME = "OwnerName"; // this is our own tag - only for server side ...
    private int angry = 0;

    /*private int teleportDistance = 12;
    
    private int followDistance = 10;
    
    private boolean tamed = false;
    
    private int angryValue = 0;
    
    private Player owner = null;*/
    private DyeColor collarColor = DyeColor.RED; // red is default

    /*private boolean sitting = false;
    
    private Player ownerName = null;*/
    public Wolf(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 0.8f;
    }

    @Override
    public double getSpeed() {
        return 1.2;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        /*if (this.namedTag.contains(NBT_KEY_ANGRY)) {
            this.angry = this.namedTag.getInt(NBT_KEY_ANGRY);
        }

        if (this.namedTag.contains(NBT_KEY_COLLAR_COLOR)) {
            this.collarColor = DyeColor.getByDyeData(this.namedTag.getInt(NBT_KEY_COLLAR_COLOR));
        }*/
        this.setMaxHealth(8);
        this.fireProof = true;
        this.setDamage(new int[]{0, 3, 4, 5});

    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putInt(NBT_KEY_ANGRY, this.angry);
        this.namedTag.putInt(NBT_KEY_COLLAR_COLOR, this.collarColor.getDyeData());
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        return this.isAngry() && super.targetOption(creature, distance);
    }

    public boolean isAngry() {
        return this.angry > 0;
    }

    public void setAngry(boolean angry) {
        this.angry = angry ? 1 : 0;
    }

    @Override
    public boolean attack(EntityDamageEvent ev) {
        boolean result = super.attack(ev);

        if (!ev.isCancelled()) {
            this.setAngry(true);
        }

        return result;
    }

    @Override
    public void attackEntity(Entity player) {
        if (MobPlugin.MOB_AI_ENABLED) {
            if (this.attackDelay > 10 && this.distanceSquared(player) < 1.6) {
                this.attackDelay = 0;
                player.attack(new EntityDamageByEntityEvent(this, player, DamageCause.ENTITY_ATTACK, getDamage()));
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

}
