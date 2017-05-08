package com.pikycz.mobplugin.entities;

import cn.nukkit.entity.EntityCreature;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 *
 * @author PikyCZ
 */
public abstract class JumpingEntity extends EntityCreature {

    public JumpingEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

}
