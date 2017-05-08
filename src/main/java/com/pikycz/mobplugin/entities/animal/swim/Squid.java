package com.pikycz.mobplugin.entities.animal.swim;

import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;

import com.pikycz.mobplugin.entities.SwimmingEntity;

public class Squid extends SwimmingEntity {

    public static final int NETWORK_ID = 17;

    public Squid(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.95f;
    }

    @Override
    public float getHeight() {
        return 0.95f;
    }

    @Override
    public float getLength() {
        return 0.95f;
    }

    @Override
    public float getEyeHeight() {
        return 0.7f;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.setMaxHealth(10);
    }

    @Override
    public Item[] getDrops() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            return new Item[]{new ItemDye(DyeColor.BLACK.getDyeData())};
        }

        return new Item[0];
    }

}
