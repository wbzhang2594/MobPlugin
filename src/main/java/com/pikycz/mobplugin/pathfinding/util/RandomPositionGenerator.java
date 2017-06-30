package com.pikycz.mobplugin.pathfinding.util;

import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import com.pikycz.mobplugin.entities.BaseEntity;
import com.pikycz.mobplugin.pathfinding.PathNavigate;

import java.util.Random;

/**
 *
 * Created by CreeperFace on 18. 1. 2017.
 */
public class RandomPositionGenerator {
    /**
     * used to store a driection when the user passes a point to move towards or away from. WARNING: NEVER THREAD SAFE.
     * MULTIPLE findTowards and findAway calls, will share this var
     */
    private static Vector3 staticVector = new Vector3();

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks
     */
    public static Vector3 findRandomTarget(BaseEntity entitycreatureIn, int xz, int y) {
        return findRandomTargetBlock(entitycreatureIn, xz, y, null);
    }

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks in the direction of the point par3
     */
    public static Vector3 findRandomTargetBlockTowards(BaseEntity entitycreatureIn, int xz, int y, Vector3 targetVec3) {
        staticVector = targetVec3.subtract(entitycreatureIn.x, entitycreatureIn.y, entitycreatureIn.z);
        return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
    }

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks in the reverse direction of the point par3
     */
    public static Vector3 findRandomTargetBlockAwayFrom(BaseEntity entitycreatureIn, int xz, int y, Vector3 targetVec3) {
        staticVector = (new Vector3(entitycreatureIn.x, entitycreatureIn.y, entitycreatureIn.z)).subtract(targetVec3);
        return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
    }

    /**
     * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction of
     * par3Vec3, then points to the tile for which creature.getBlockPathWeight returns the highest number
     */
    private static Vector3 findRandomTargetBlock(BaseEntity entitycreatureIn, int xz, int y, Vector3 targetVec3) {
        PathNavigate pathnavigate = entitycreatureIn.getNavigator();
        Random random = entitycreatureIn.getLevel().rand;
        boolean flag = false;
        int i = 0;
        int j = 0;
        int k = 0;
        float f = -99999.0F;
        boolean flag1;

        if (entitycreatureIn.hasMaxHomeDistance()) {
            double d0 = entitycreatureIn.distanceSquared(new Vector3(NukkitMath.floorDouble(entitycreatureIn.x), NukkitMath.floorDouble(entitycreatureIn.y), NukkitMath.floorDouble(entitycreatureIn.z))) + 4.0D;
            double d1 = (double) (entitycreatureIn.getMaxHomeDistance() + (float) xz);
            flag1 = d0 < d1 * d1;
        } else {
            flag1 = false;
        }

        for (int j1 = 0; j1 < 10; ++j1) {
            int l = random.nextInt(2 * xz + 1) - xz;
            int k1 = random.nextInt(2 * y + 1) - y;
            int i1 = random.nextInt(2 * xz + 1) - xz;

            if (targetVec3 == null || (double) l * targetVec3.x + (double) i1 * targetVec3.z >= 0.0D) {
                if (entitycreatureIn.hasMaxHomeDistance() && xz > 1) {
                    Vector3 blockpos = entitycreatureIn.clone();

                    if (entitycreatureIn.x > blockpos.getX()) {
                        l -= random.nextInt(xz / 2);
                    } else {
                        l += random.nextInt(xz / 2);
                    }

                    if (entitycreatureIn.z > blockpos.getZ()) {
                        i1 -= random.nextInt(xz / 2);
                    } else {
                        i1 += random.nextInt(xz / 2);
                    }
                }

                Vector3 blockpos1 = new Vector3((double) l + entitycreatureIn.x, (double) k1 + entitycreatureIn.y, (double) i1 + entitycreatureIn.z);

                if ((!flag1 || entitycreatureIn.isWithinHomeDistance(blockpos1)) && pathnavigate.canEntityStandOnPos(blockpos1)) {
                    float f1 = entitycreatureIn.getBlockPathWeight(blockpos1);

                    if (f1 > f) {
                        f = f1;
                        i = l;
                        j = k1;
                        k = i1;
                        flag = true;
                    }
                }
            }
        }

        if (flag) {
            return new Vector3(i + entitycreatureIn.x, j + entitycreatureIn.y, k + entitycreatureIn.z);
        } else {
            return null;
        }
    }
}
