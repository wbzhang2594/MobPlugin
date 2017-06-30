package com.pikycz.mobplugin.pathfinding.util;

import cn.nukkit.entity.Entity;
import cn.nukkit.math.Vector3;

/**
 *
 * Created by CreeperFace on 16. 1. 2017.
 */
public class RayTraceResult {
    private Vector3 blockPos;

    /**
     * What type of ray trace hit was this? 0 = block, 1 = entity
     */
    public RayTraceResult.Type typeOfHit;
    public int sideHit;

    /**
     * The vector position of the hit
     */
    public Vector3 hitVec;

    /**
     * The hit entity
     */
    public Entity entityHit;

    public RayTraceResult(Vector3 hitVecIn, int sideHitIn, Vector3 blockPosIn) {
        this(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, blockPosIn);
    }

    public RayTraceResult(Vector3 hitVecIn, int sideHitIn) {
        this(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, new Vector3());
    }

    public RayTraceResult(Entity entityIn) {
        this(entityIn, new Vector3(entityIn.x, entityIn.y, entityIn.z));
    }

    public RayTraceResult(RayTraceResult.Type typeIn, Vector3 hitVecIn, int sideHitIn, Vector3 blockPosIn) {
        this.typeOfHit = typeIn;
        this.blockPos = blockPosIn;
        this.sideHit = sideHitIn;
        this.hitVec = new Vector3(hitVecIn.x, hitVecIn.y, hitVecIn.z);
    }

    public RayTraceResult(Entity entityHitIn, Vector3 hitVecIn) {
        this.typeOfHit = RayTraceResult.Type.ENTITY;
        this.entityHit = entityHitIn;
        this.hitVec = hitVecIn;
    }

    public Vector3 getBlockPos() {
        return this.blockPos;
    }

    public String toString() {
        return "HitResult{type=" + this.typeOfHit + ", blockpos=" + this.blockPos + ", f=" + this.sideHit + ", pos=" + this.hitVec + ", entity=" + this.entityHit + '}';
    }

    public static enum Type {
        MISS,
        BLOCK,
        ENTITY;
    }
}