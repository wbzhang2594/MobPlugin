package creeperCZ.mobplugin.pathfinding;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import creeperCZ.mobplugin.entities.BaseEntity;
import creeperCZ.mobplugin.util.EnumFacing;
import creeperCZ.mobplugin.util.Utils;

public class SwimNodeProcessor extends NodeProcessor {
    public PathPoint getStart() {
        return this.openPoint(NukkitMath.floorDouble(this.entity.getBoundingBox().minX), NukkitMath.floorDouble(this.entity.getBoundingBox().minY + 0.5D), NukkitMath.floorDouble(this.entity.getBoundingBox().minZ));
    }

    /**
     * Returns PathPoint for given coordinates
     */
    public PathPoint getPathPointToCoords(double x, double y, double z) {
        return this.openPoint(NukkitMath.floorDouble(x - (double) (this.entity.getWidth() / 2.0F)), NukkitMath.floorDouble(y + 0.5D), NukkitMath.floorDouble(z - (double) (this.entity.getWidth() / 2.0F)));
    }

    public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
        int i = 0;

        for (EnumFacing enumfacing : EnumFacing.values()) {
            PathPoint pathpoint = this.getWaterNode(currentPoint.xCoord + enumfacing.getFrontOffsetX(), currentPoint.yCoord + enumfacing.getFrontOffsetY(), currentPoint.zCoord + enumfacing.getFrontOffsetZ());

            if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint;
            }
        }

        return i;
    }

    public PathNodeType getPathNodeType(Level blockaccessIn, int x, int y, int z, BaseEntity entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
        return PathNodeType.WATER;
    }

    public PathNodeType getPathNodeType(Level x, int y, int z, int p_186330_4_) {
        return PathNodeType.WATER;
    }

    private PathPoint getWaterNode(int p_186328_1_, int p_186328_2_, int p_186328_3_) {
        PathNodeType pathnodetype = this.isFree(p_186328_1_, p_186328_2_, p_186328_3_);
        return pathnodetype == PathNodeType.WATER ? this.openPoint(p_186328_1_, p_186328_2_, p_186328_3_) : null;
    }

    private PathNodeType isFree(int p_186327_1_, int p_186327_2_, int p_186327_3_) {
        Vector3 blockpos$mutableblockpos = new Vector3();

        for (int i = p_186327_1_; i < p_186327_1_ + this.entitySizeX; ++i) {
            for (int j = p_186327_2_; j < p_186327_2_ + this.entitySizeY; ++j) {
                for (int k = p_186327_3_; k < p_186327_3_ + this.entitySizeZ; ++k) {
                    int iblockstate = Utils.getBlockId(this.level, blockpos$mutableblockpos.setComponents(i, j, k));

                    if (iblockstate != Block.WATER && iblockstate != Block.STILL_WATER) {
                        return PathNodeType.BLOCKED;
                    }
                }
            }
        }

        return PathNodeType.WATER;
    }
}
