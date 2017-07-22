package com.pikycz.mobplugin.entities;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 *
 * @author PikyCZ
 */
public abstract class JumpingEntity extends BaseEntity {

    /*
     * For slimes and Magma Cubes ONLY
     * Not to be confused for normal entity jumping
     */
    public JumpingEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    protected void checkTarget() {
        //TODO
    }

    @Override
    public Vector3 updateMove(int tickDiff) {
        return null;
        // TODO
    }

}
