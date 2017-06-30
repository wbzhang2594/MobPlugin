package com.pikycz.mobplugin.entities.animal;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import com.pikycz.mobplugin.entities.BaseEntity;

/**
 *
 * Created by CreeperFace on 18. 1. 2017.
 */
public abstract class EntityAgeable extends BaseEntity {

    public EntityAgeable(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
