package com.pikycz.mobplugin.entities.monster.walking;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
//import com.pikycz.mobplugin.entities.JumpingEntity;
import com.pikycz.mobplugin.entities.WalkingEntity;
import com.pikycz.mobplugin.utils.Utils;

/**
 *
 * @author PikyCZ
 *
 */
public class LavaSlime extends WalkingEntity {
    //>>>>In 1.1 mojang rename MagmaCube to LavaSlime<<<<
    public static final int NETWORK_ID = 42;

    public LavaSlime(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 2.04f;
    }

    @Override
    public float getHeight() {
        return 2.04f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(16);
        super.initEntity();
    }

    public int getKillExperience() {
        return Utils.rand(1, 5);
    }

}
