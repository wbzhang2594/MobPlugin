package creeperCZ.mobplugin.entities;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by CreeperFace on 18. 1. 2017.
 */
public abstract class EntityAgeable extends BaseEntity {

    public EntityAgeable(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
}
