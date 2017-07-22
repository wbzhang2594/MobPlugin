package com.pikycz.mobplugin.entities.monster.swim;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import com.pikycz.mobplugin.entities.monster.SwimmingMonster;

/**
 *
 * @author PikyCZ
 *
 */
public class ElderGuardian extends SwimmingMonster {

    public static final int NETWORK_ID = 50;

    public ElderGuardian(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }
    
    @Override
    public String getName() {
        return "ElderGuardian";
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

    @Override
    public int getKillExperience() {
        return 10;
    }

}
