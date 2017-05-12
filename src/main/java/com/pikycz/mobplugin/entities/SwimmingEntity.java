package com.pikycz.mobplugin.entities;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 *
 * @author PikyCZ
 */
public abstract class SwimmingEntity extends BaseEntity {

    public SwimmingEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public void checkTarget() {
        /////////lul
    }

    @Override
    public Vector3 updateMove(int tickDiff) {
        //todo
    }

    @Override
    public int getKillExperience() {
        return 0;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

}
