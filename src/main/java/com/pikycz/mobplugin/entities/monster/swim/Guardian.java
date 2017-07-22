package com.pikycz.mobplugin.entities.monster.swim;

/**
 *
 * @author PikyCZ
 *
 */
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import com.pikycz.mobplugin.entities.monster.SwimmingMonster;

public class Guardian extends SwimmingMonster {

    public static final int NETWORK_ID = 49;

    public Guardian(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }
    
    @Override
    public String getName() {
        return "Guardian";
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 2.4f;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.setMaxHealth(30);
    }

    @Override
    public int getKillExperience() {
        return 10;
    }
}
