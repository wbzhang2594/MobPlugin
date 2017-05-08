package com.pikycz.mobplugin.entities.monster.swim;

/**
 *
 * @author PikyCZ
 *
 */
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import com.pikycz.mobplugin.entities.SwimmingEntity;

public class ElderGuardian extends SwimmingEntity {

    public static final int NETWORK_ID = 50;

    public ElderGuardian(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 1.9f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(80);
    }

}
