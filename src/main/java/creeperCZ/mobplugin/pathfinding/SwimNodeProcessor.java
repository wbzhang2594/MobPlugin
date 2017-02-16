package creeperCZ.mobplugin.pathfinding;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import creeperCZ.mobplugin.entities.BaseEntity;
import creeperCZ.mobplugin.utils.EnumFacing;
import creeperCZ.mobplugin.utils.Utils;

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
            PathPoint pathpoint = this.getWaterNode(currentPoint.x + enumfacing.getFrontOffsetX(), currentPoint.y + enumfacing.getFrontOffsetY(), currentPoint.z + enumfacing.getFrontOffsetZ());

            if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint;
            }
        }

        return i;
    }

    public PathNodeType getPathNodeType(Level level, int x, int y, int z, BaseEntity entity, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
        return PathNodeType.WATER;
    }

    public PathNodeType getPathNodeType(Level x, int y, int z, int p_186330_4_) {
        return PathNodeType.WATER;
    }

    private PathPoint getWaterNode(int x, int y, int z) {
        PathNodeType pathnodetype = this.isFree(x, y, z);
        return pathnodetype == PathNodeType.WATER ? this.openPoint(x, y, z) : null;
    }

    private PathNodeType isFree(int x, int y, int z) {
        Vector3 pos = new Vector3();

        for (int i = x; i < x + this.entitySizeX; ++i) {
            for (int j = y; j < y + this.entitySizeY; ++j) {
                for (int k = z; k < z + this.entitySizeZ; ++k) {
                    int iblockstate = Utils.getBlockId(this.level, pos.setComponents(i, j, k));

                    if (iblockstate != Block.WATER && iblockstate != Block.STILL_WATER) {
                        return PathNodeType.BLOCKED;
                    }
                }
            }
        }

        return PathNodeType.WATER;
    }
}
