package com.pikycz.mobplugin.pathfinding;

import cn.nukkit.block.*;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import com.google.common.collect.Sets;
import com.pikycz.mobplugin.entities.BaseEntity;
import com.pikycz.mobplugin.pathfinding.util.EnumFacing;
import com.pikycz.mobplugin.pathfinding.util.Util;

import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author CreeperFace
 */
public class WalkNodeProcessor extends NodeProcessor {
    private float avoidsWater;

    public void initProcessor(Level sourceIn, BaseEntity mob) {
        super.initProcessor(sourceIn, mob);
        this.avoidsWater = mob.getPathPriority(PathNodeType.WATER);
    }

    /**
     * This method is called when all nodes have been processed and PathEntity is created.
     */
    public void postProcess() {
        this.entity.setPathPriority(PathNodeType.WATER, this.avoidsWater);
        super.postProcess();
    }

    public PathPoint getStart() {
        int i;

        if (this.getCanSwim() && this.entity.inWater) {
            i = (int) this.entity.getBoundingBox().minY;
            Vector3 pos = new Vector3(NukkitMath.floorDouble(this.entity.x), i, NukkitMath.floorDouble(this.entity.z));

            for (int block = Util.getBlockId(level, pos); block == Block.STILL_WATER || block == Block.WATER; block = Util.getBlockId(this.level, pos)) {
                ++i;
                pos.setComponents(NukkitMath.floorDouble(this.entity.x), i, NukkitMath.floorDouble(this.entity.z));
            }
        } else if (this.entity.onGround) {
            i = NukkitMath.floorDouble(this.entity.getBoundingBox().minY + 0.5D);
        } else {
            Vector3 blockpos = this.entity.clone();

            int id = Util.getBlockId(this.level, blockpos);
            Block block = Block.get(id);

            while ((id == 0 || block.canPassThrough()) && blockpos.y > 0) {
                blockpos = blockpos.getSide(BlockFace.DOWN);
            }

            i = blockpos.getSide(BlockFace.UP).getFloorY();
        }

        Vector3 blockpos2 = this.entity.clone();
        PathNodeType pathnodetype1 = this.getPathNodeType(this.entity, blockpos2.getFloorX(), i, blockpos2.getFloorZ());

        if (this.entity.getPathPriority(pathnodetype1) < 0.0F) {
            Set<Vector3> set = Sets.<Vector3>newHashSet();
            set.add(new Vector3(this.entity.getBoundingBox().minX, (double) i, this.entity.getBoundingBox().minZ));
            set.add(new Vector3(this.entity.getBoundingBox().minX, (double) i, this.entity.getBoundingBox().maxZ));
            set.add(new Vector3(this.entity.getBoundingBox().maxX, (double) i, this.entity.getBoundingBox().minZ));
            set.add(new Vector3(this.entity.getBoundingBox().maxX, (double) i, this.entity.getBoundingBox().maxZ));

            for (Vector3 blockpos1 : set) {
                PathNodeType pathnodetype = this.getPathNodeType(this.entity, blockpos1);

                if (this.entity.getPathPriority(pathnodetype) >= 0.0F) {
                    return this.openPoint(blockpos1.getFloorX(), blockpos1.getFloorY(), blockpos1.getFloorZ());
                }
            }
        }

        return this.openPoint(blockpos2.getFloorX(), i, blockpos2.getFloorZ());
    }

    /**
     * Returns PathPoint for given coordinates
     */
    public PathPoint getPathPointToCoords(double x, double y, double z) {
        return this.openPoint(NukkitMath.floorDouble(x - (double) (this.entity.getWidth() / 2.0F)), NukkitMath.floorDouble(y), NukkitMath.floorDouble(z - (double) (this.entity.getWidth() / 2.0F)));
    }

    public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
        int i = 0;
        int j = 0;
        PathNodeType pathnodetype = this.getPathNodeType(this.entity, currentPoint.xCoord, currentPoint.yCoord + 1, currentPoint.zCoord);

        if (this.entity.getPathPriority(pathnodetype) >= 0.0F) {
            j = NukkitMath.floorFloat(Math.max(1.0F, this.entity.stepHeight));
        }

        Vector3 blockpos = (new Vector3(currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord)).getSide(BlockFace.DOWN);
        double d0 = (double) currentPoint.yCoord - (1.0D - this.level.getBlock(blockpos).getBoundingBox().maxY);
        PathPoint pathpoint = this.getSafePoint(currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord + 1, j, d0, EnumFacing.SOUTH);
        PathPoint pathpoint1 = this.getSafePoint(currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord, j, d0, EnumFacing.WEST);
        PathPoint pathpoint2 = this.getSafePoint(currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord, j, d0, EnumFacing.EAST);
        PathPoint pathpoint3 = this.getSafePoint(currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord - 1, j, d0, EnumFacing.NORTH);

        if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint;
        }

        if (pathpoint1 != null && !pathpoint1.visited && pathpoint1.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint1;
        }

        if (pathpoint2 != null && !pathpoint2.visited && pathpoint2.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint2;
        }

        if (pathpoint3 != null && !pathpoint3.visited && pathpoint3.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint3;
        }

        boolean flag = pathpoint3 == null || pathpoint3.nodeType == PathNodeType.OPEN || pathpoint3.costMalus != 0.0F;
        boolean flag1 = pathpoint == null || pathpoint.nodeType == PathNodeType.OPEN || pathpoint.costMalus != 0.0F;
        boolean flag2 = pathpoint2 == null || pathpoint2.nodeType == PathNodeType.OPEN || pathpoint2.costMalus != 0.0F;
        boolean flag3 = pathpoint1 == null || pathpoint1.nodeType == PathNodeType.OPEN || pathpoint1.costMalus != 0.0F;

        if (flag && flag3) {
            PathPoint pathpoint4 = this.getSafePoint(currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord - 1, j, d0, EnumFacing.NORTH);

            if (pathpoint4 != null && !pathpoint4.visited && pathpoint4.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint4;
            }
        }

        if (flag && flag2) {
            PathPoint pathpoint5 = this.getSafePoint(currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord - 1, j, d0, EnumFacing.NORTH);

            if (pathpoint5 != null && !pathpoint5.visited && pathpoint5.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint5;
            }
        }

        if (flag1 && flag3) {
            PathPoint pathpoint6 = this.getSafePoint(currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord + 1, j, d0, EnumFacing.SOUTH);

            if (pathpoint6 != null && !pathpoint6.visited && pathpoint6.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint6;
            }
        }

        if (flag1 && flag2) {
            PathPoint pathpoint7 = this.getSafePoint(currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord + 1, j, d0, EnumFacing.SOUTH);

            if (pathpoint7 != null && !pathpoint7.visited && pathpoint7.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint7;
            }
        }

        return i;
    }

    /**
     * Returns a point that the entity can safely move to
     */
    private PathPoint getSafePoint(int x, int y, int z, int p_186332_4_, double p_186332_5_, EnumFacing facing) {
        PathPoint pathpoint = null;
        Vector3 blockpos = new Vector3(x, y, z);
        Vector3 blockpos1 = blockpos.getSide(BlockFace.DOWN);
        double d0 = (double) y - (1.0D - this.level.getBlock(blockpos1).getBoundingBox().maxY);

        if (d0 - p_186332_5_ > 1.125D) {
            return null;
        } else {
            PathNodeType pathnodetype = this.getPathNodeType(this.entity, x, y, z);
            float f = this.entity.getPathPriority(pathnodetype);
            double d1 = (double) this.entity.getWidth() / 2.0D;

            if (f >= 0.0F) {
                pathpoint = this.openPoint(x, y, z);
                pathpoint.nodeType = pathnodetype;
                pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
            }

            if (pathnodetype == PathNodeType.WALKABLE) {
                return pathpoint;
            } else {
                if (pathpoint == null && p_186332_4_ > 0 && pathnodetype != PathNodeType.FENCE && pathnodetype != PathNodeType.TRAPDOOR) {
                    pathpoint = this.getSafePoint(x, y + 1, z, p_186332_4_ - 1, p_186332_5_, facing);

                    if (pathpoint != null && (pathpoint.nodeType == PathNodeType.OPEN || pathpoint.nodeType == PathNodeType.WALKABLE) && this.entity.getWidth() < 1.0F) {
                        double d2 = (double) (x - facing.getFrontOffsetX()) + 0.5D;
                        double d3 = (double) (z - facing.getFrontOffsetZ()) + 0.5D;
                        AxisAlignedBB axisalignedbb = new AxisAlignedBB(d2 - d1, (double) y + 0.001D, d3 - d1, d2 + d1, (double) ((float) y + this.entity.getHeight()), d3 + d1);
                        AxisAlignedBB axisalignedbb1 = this.level.getBlock(blockpos).getBoundingBox();
                        AxisAlignedBB axisalignedbb2 = axisalignedbb.addCoord(0.0D, axisalignedbb1.maxY - 0.002D, 0.0D);

                        if (this.entity.getLevel().getCollisionBlocks(axisalignedbb2, true).length != 0) {
                            pathpoint = null;
                        }
                    }
                }

                if (pathnodetype == PathNodeType.OPEN) {
                    AxisAlignedBB axisalignedbb3 = new AxisAlignedBB((double) x - d1 + 0.5D, (double) y + 0.001D, (double) z - d1 + 0.5D, (double) x + d1 + 0.5D, (double) ((float) y + this.entity.getHeight()), (double) z + d1 + 0.5D);

                    if (this.entity.getLevel().getCollisionBlocks(axisalignedbb3, true).length != 0) {
                        return null;
                    }

                    if (this.entity.getWidth() >= 1.0F) {
                        PathNodeType pathnodetype1 = this.getPathNodeType(this.entity, x, y - 1, z);

                        if (pathnodetype1 == PathNodeType.BLOCKED) {
                            pathpoint = this.openPoint(x, y, z);
                            pathpoint.nodeType = PathNodeType.WALKABLE;
                            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                            return pathpoint;
                        }
                    }

                    int i = 0;

                    while (y > 0 && pathnodetype == PathNodeType.OPEN) {
                        --y;

                        if (i++ >= this.entity.getMaxFallHeight()) {
                            return null;
                        }

                        pathnodetype = this.getPathNodeType(this.entity, x, y, z);
                        f = this.entity.getPathPriority(pathnodetype);

                        if (pathnodetype != PathNodeType.OPEN && f >= 0.0F) {
                            pathpoint = this.openPoint(x, y, z);
                            pathpoint.nodeType = pathnodetype;
                            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                            break;
                        }

                        if (f < 0.0F) {
                            return null;
                        }
                    }
                }

                return pathpoint;
            }
        }
    }

    public PathNodeType getPathNodeType(Level blockaccessIn, int x, int y, int z, BaseEntity entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
        EnumSet<PathNodeType> enumset = EnumSet.<PathNodeType>noneOf(PathNodeType.class);
        PathNodeType pathnodetype = PathNodeType.BLOCKED;
        double d0 = (double) entitylivingIn.getWidth() / 2.0D;
        Vector3 blockpos = entitylivingIn.clone();

        for (int i = 0; i < xSize; ++i) {
            for (int j = 0; j < ySize; ++j) {
                for (int k = 0; k < zSize; ++k) {
                    int l = i + x;
                    int i1 = j + y;
                    int j1 = k + z;
                    PathNodeType pathnodetype1 = this.getPathNodeType(blockaccessIn, l, i1, j1);

                    if (pathnodetype1 == PathNodeType.DOOR_WOOD_CLOSED && canBreakDoorsIn && canEnterDoorsIn) {
                        pathnodetype1 = PathNodeType.WALKABLE;
                    }

                    if (pathnodetype1 == PathNodeType.DOOR_OPEN && !canEnterDoorsIn) {
                        pathnodetype1 = PathNodeType.BLOCKED;
                    }

                    if (pathnodetype1 == PathNodeType.RAIL && !(blockaccessIn.getBlock(blockpos) instanceof BlockRail) && !(blockaccessIn.getBlock(blockpos.getSide(BlockFace.DOWN)) instanceof BlockRail)) {
                        pathnodetype1 = PathNodeType.FENCE;
                    }

                    if (i == 0 && j == 0 && k == 0) {
                        pathnodetype = pathnodetype1;
                    }

                    enumset.add(pathnodetype1);
                }
            }
        }

        if (enumset.contains(PathNodeType.FENCE)) {
            return PathNodeType.FENCE;
        } else {
            PathNodeType pathnodetype2 = PathNodeType.BLOCKED;

            for (PathNodeType pathnodetype3 : enumset) {
                if (entitylivingIn.getPathPriority(pathnodetype3) < 0.0F) {
                    return pathnodetype3;
                }

                if (entitylivingIn.getPathPriority(pathnodetype3) >= entitylivingIn.getPathPriority(pathnodetype2)) {
                    pathnodetype2 = pathnodetype3;
                }
            }

            if (pathnodetype == PathNodeType.OPEN && entitylivingIn.getPathPriority(pathnodetype2) == 0.0F) {
                return PathNodeType.OPEN;
            } else {
                return pathnodetype2;
            }
        }
    }

    private PathNodeType getPathNodeType(BaseEntity entitylivingIn, Vector3 pos) {
        return this.getPathNodeType(entitylivingIn, pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
    }

    private PathNodeType getPathNodeType(BaseEntity entitylivingIn, int x, int y, int z) {
        return this.getPathNodeType(this.level, x, y, z, entitylivingIn, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanBreakDoors(), this.getCanEnterDoors());
    }

    public PathNodeType getPathNodeType(Level level, int x, int y, int z) {
        PathNodeType pathnodetype = this.func_189553_b(level, x, y, z);

        if (pathnodetype == PathNodeType.OPEN && y >= 1) {
            Block block = level.getBlock(new Vector3(x, y - 1, z));
            PathNodeType pathnodetype1 = this.func_189553_b(level, x, y - 1, z);
            pathnodetype = pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.WATER && pathnodetype1 != PathNodeType.LAVA ? PathNodeType.WALKABLE : PathNodeType.OPEN;

            if (pathnodetype1 == PathNodeType.DAMAGE_FIRE/* || block == Block.MAGMA*/) //for future versions
            {
                pathnodetype = PathNodeType.DAMAGE_FIRE;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
                pathnodetype = PathNodeType.DAMAGE_CACTUS;
            }
        }

        Vector3 pos = new Vector3();

        if (pathnodetype == PathNodeType.WALKABLE) {
            for (int j = -1; j <= 1; ++j) {
                for (int i = -1; i <= 1; ++i) {
                    if (j != 0 || i != 0) {
                        int block1 = Util.getBlockId(level, pos.setComponents(j + x, y, i + j));

                        if (block1 == Block.CACTUS) {
                            pathnodetype = PathNodeType.DANGER_CACTUS;
                        } else if (block1 == Block.FIRE) {
                            pathnodetype = PathNodeType.DANGER_FIRE;
                        }
                    }
                }
            }
        }

        //pos.release(); doufam ze se nic nestane kdyz to vynecham :D
        return pathnodetype;
    }

    private PathNodeType func_189553_b(Level level, int x, int y, int z) {
        Vector3 blockpos = new Vector3(x, y, z);
        Block block = level.getBlock(blockpos);
        int blockId = block.getId();

        return blockId == Block.AIR ? PathNodeType.OPEN : (blockId != Block.TRAPDOOR && blockId != Block.IRON_TRAPDOOR && blockId != Block.WATER_LILY ? (blockId == Block.FIRE ? PathNodeType.DAMAGE_FIRE : (blockId == Block.CACTUS ? PathNodeType.DAMAGE_CACTUS : (block instanceof BlockDoor && !((BlockDoor) block).isOpen() ? PathNodeType.DOOR_WOOD_CLOSED : (block instanceof BlockDoorIron && !((BlockDoorIron) block).isOpen() ? PathNodeType.DOOR_IRON_CLOSED : (block instanceof BlockDoor && ((BlockDoor) block).isOpen() ? PathNodeType.DOOR_OPEN : (block instanceof BlockRail ? PathNodeType.RAIL : (!(block instanceof BlockFence) && !(block instanceof BlockWall) && (!(block instanceof BlockFenceGate) || ((BlockFenceGate) block).isOpen()) ? (blockId == Block.WATER || blockId == Block.STILL_WATER ? PathNodeType.WATER : (blockId == Block.LAVA || blockId == Block.STILL_LAVA ? PathNodeType.LAVA : (block.canPassThrough() ? PathNodeType.OPEN : PathNodeType.BLOCKED))) : PathNodeType.FENCE))))))) : PathNodeType.TRAPDOOR);
    }
}    
