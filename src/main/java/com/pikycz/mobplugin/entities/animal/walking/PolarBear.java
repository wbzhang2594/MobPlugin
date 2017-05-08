package com.pikycz.mobplugin.entities.animal.walking;

/**
 *
 * @author PikyCZ
 *
 */
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import com.pikycz.mobplugin.entities.animal.WalkingAnimal;
import com.pikycz.mobplugin.entities.utils.Utils;

public class PolarBear extends WalkingAnimal {

    public static final int NETWORK_ID = 28;

    public PolarBear(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 1.3f;
    }

    @Override
    public float getHeight() {
        return 1.4f;
    }

    @Override
    public float getEyeHeight() {
        return 1.4f;
    }

    @Override
    public double getSpeed() {
        return 1.25;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.setMaxHealth(30);
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.RAW_FISH), Item.get(Item.RAW_SALMON)};
    }

    @Override
    public int getKillExperience() {
        return Utils.rand(1, 3);
    }

}
