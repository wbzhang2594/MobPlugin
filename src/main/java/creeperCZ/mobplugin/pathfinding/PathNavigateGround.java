package creeperCZ.mobplugin.pathfinding;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import creeperCZ.mobplugin.entities.BaseEntity;
import creeperCZ.mobplugin.util.Utils;

public class PathNavigateGround extends PathNavigate {
    private boolean shouldAvoidSun;

    public PathNavigateGround(BaseEntity entitylivingIn, Level worldIn) {
        super(entitylivingIn, worldIn);
    }

    protected PathFinder getPathFinder() {
        this.nodeProcessor = new WalkNodeProcessor();
        this.nodeProcessor.setCanEnterDoors(true);
        return new PathFinder(this.nodeProcessor);
    }

    /**
     * If on ground or swimming and can swim
     */
    protected boolean canNavigate() {
        return this.theEntity.onGround || this.getCanSwim() && this.isInLiquid() || this.theEntity.riding != null;
    }

    protected Vector3 getEntityPosition() {
        return new Vector3(this.theEntity.x, (double) this.getPathablePosY(), this.theEntity.z);
    }

    /**
     * Returns path to given BlockPos
     */
    public Path getPathToPos(Vector3 pos) {
        if (this.level.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()) == Block.AIR) {
            Vector3 blockpos = pos.getSide(Vector3.SIDE_DOWN);
            int x = blockpos.getFloorX();
            int z = blockpos.getFloorZ();


            for (int y = blockpos.getFloorY(); y > 0 && this.level.getBlockIdAt(x, y, z) == Block.AIR; y--) {

            }

            if (blockpos.getY() > 0) {
                return super.getPathToPos(blockpos.getSide(Vector3.SIDE_UP));
            }

            while (blockpos.getY() < 256 && Utils.getBlockId(this.level, blockpos) == Block.AIR) {
                blockpos = blockpos.getSide(Vector3.SIDE_UP);
            }

            pos = blockpos;
        }

        if (!this.level.getBlock(pos).isSolid()) {
            return super.getPathToPos(pos);
        } else {
            Vector3 blockpos1;

            for (blockpos1 = pos.getSide(Vector3.SIDE_UP); blockpos1.getY() < 256 && Block.solid[Utils.getBlockId(this.level, blockpos1)]; blockpos1 = blockpos1.getSide(Vector3.SIDE_UP)) {
                ;
            }

            return super.getPathToPos(blockpos1);
        }
    }

    /**
     * Returns the path to the given EntityLiving. Args : entity
     */
    public Path getPathToEntityLiving(Entity entityIn) {
        Vector3 blockpos = entityIn.clone();
        return this.getPathToPos(blockpos);
    }

    /**
     * Gets the safe pathing Y position for the entity depending on if it can path swim or not
     */
    private int getPathablePosY() {
        if (this.theEntity.inWater && this.getCanSwim()) {
            int i = (int) this.theEntity.getBoundingBox().minY;
            int block = Utils.getBlockId(this.level, new Vector3(NukkitMath.floorDouble(this.theEntity.x), i, NukkitMath.floorDouble(this.theEntity.z)));
            int j = 0;

            while (block == Block.WATER || block == Block.STILL_WATER) {
                ++i;
                block = Utils.getBlockId(this.level, new Vector3(NukkitMath.floorDouble(this.theEntity.x), i, NukkitMath.floorDouble(this.theEntity.z)));
                ++j;

                if (j > 16) {
                    return (int) this.theEntity.getBoundingBox().minY;
                }
            }

            return i;
        } else {
            return (int) (this.theEntity.getBoundingBox().minY + 0.5D);
        }
    }

    /**
     * Trims path data from the end to the first sun covered block
     */
    protected void removeSunnyPath() {
        super.removeSunnyPath();

        for (int i = 0; i < this.currentPath.getCurrentPathLength(); ++i) {
            PathPoint pathpoint = this.currentPath.getPathPointFromIndex(i);
            PathPoint pathpoint1 = i + 1 < this.currentPath.getCurrentPathLength() ? this.currentPath.getPathPointFromIndex(i + 1) : null;
            int block = Utils.getBlockId(this.level, new Vector3(pathpoint.xCoord, pathpoint.yCoord, pathpoint.zCoord));

            if (block == Item.CAULDRON_BLOCK) {
                this.currentPath.setPoint(i, pathpoint.cloneMove(pathpoint.xCoord, pathpoint.yCoord + 1, pathpoint.zCoord));

                if (pathpoint1 != null && pathpoint.yCoord >= pathpoint1.yCoord) {
                    this.currentPath.setPoint(i + 1, pathpoint1.cloneMove(pathpoint1.xCoord, pathpoint.yCoord + 1, pathpoint1.zCoord));
                }
            }
        }

        if (this.shouldAvoidSun) {
            if (this.level.canBlockSeeSky(new Vector3(NukkitMath.floorDouble(this.theEntity.x), (int) (this.theEntity.getBoundingBox().minY + 0.5D), NukkitMath.floorDouble(this.theEntity.z)))) {
                return;
            }

            for (int j = 0; j < this.currentPath.getCurrentPathLength(); ++j) {
                PathPoint pathpoint2 = this.currentPath.getPathPointFromIndex(j);

                if (this.level.canBlockSeeSky(new Vector3(pathpoint2.xCoord, pathpoint2.yCoord, pathpoint2.zCoord))) {
                    this.currentPath.setCurrentPathLength(j - 1);
                    return;
                }
            }
        }
    }

    /**
     * Checks if the specified entity can safely walk to the specified location.
     */
    protected boolean isDirectPathBetweenPoints(Vector3 posVec31, Vector3 posVec32, int sizeX, int sizeY, int sizeZ) {
        int i = NukkitMath.floorDouble(posVec31.x);
        int j = NukkitMath.floorDouble(posVec31.z);
        double d0 = posVec32.x - posVec31.x;
        double d1 = posVec32.z - posVec31.z;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 < 1.0E-8D) {
            return false;
        } else {
            double d3 = 1.0D / Math.sqrt(d2);
            d0 = d0 * d3;
            d1 = d1 * d3;
            sizeX = sizeX + 2;
            sizeZ = sizeZ + 2;

            if (!this.isSafeToStandAt(i, (int) posVec31.y, j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) {
                return false;
            } else {
                sizeX = sizeX - 2;
                sizeZ = sizeZ - 2;
                double d4 = 1.0D / Math.abs(d0);
                double d5 = 1.0D / Math.abs(d1);
                double d6 = (double) i - posVec31.x;
                double d7 = (double) j - posVec31.z;

                if (d0 >= 0.0D) {
                    ++d6;
                }

                if (d1 >= 0.0D) {
                    ++d7;
                }

                d6 = d6 / d0;
                d7 = d7 / d1;
                int k = d0 < 0.0D ? -1 : 1;
                int l = d1 < 0.0D ? -1 : 1;
                int i1 = NukkitMath.floorDouble(posVec32.x);
                int j1 = NukkitMath.floorDouble(posVec32.z);
                int k1 = i1 - i;
                int l1 = j1 - j;

                while (k1 * k > 0 || l1 * l > 0) {
                    if (d6 < d7) {
                        d6 += d4;
                        i += k;
                        k1 = i1 - i;
                    } else {
                        d7 += d5;
                        j += l;
                        l1 = j1 - j;
                    }

                    if (!this.isSafeToStandAt(i, (int) posVec31.y, j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    /**
     * Returns true when an entity could stand at a position, including solid blocks under the entire entity.
     */
    private boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vector3 vec31, double p_179683_8_, double p_179683_10_) {
        int i = x - sizeX / 2;
        int j = z - sizeZ / 2;

        if (!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, vec31, p_179683_8_, p_179683_10_)) {
            return false;
        } else {
            for (int k = i; k < i + sizeX; ++k) {
                for (int l = j; l < j + sizeZ; ++l) {
                    double d0 = (double) k + 0.5D - vec31.x;
                    double d1 = (double) l + 0.5D - vec31.z;

                    if (d0 * p_179683_8_ + d1 * p_179683_10_ >= 0.0D) {
                        PathNodeType pathnodetype = this.nodeProcessor.getPathNodeType(this.level, k, y - 1, l, this.theEntity, sizeX, sizeY, sizeZ, true, true);

                        if (pathnodetype == PathNodeType.WATER) {
                            return false;
                        }

                        if (pathnodetype == PathNodeType.LAVA) {
                            return false;
                        }

                        if (pathnodetype == PathNodeType.OPEN) {
                            return false;
                        }

                        pathnodetype = this.nodeProcessor.getPathNodeType(this.level, k, y, l, this.theEntity, sizeX, sizeY, sizeZ, true, true);
                        float f = this.theEntity.getPathPriority(pathnodetype);

                        if (f < 0.0F || f >= 8.0F) {
                            return false;
                        }

                        if (pathnodetype == PathNodeType.DAMAGE_FIRE || pathnodetype == PathNodeType.DANGER_FIRE || pathnodetype == PathNodeType.DAMAGE_OTHER) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    /**
     * Returns true if an entity does not collide with any solid blocks at the position.
     */
    private boolean isPositionClear(int p_179692_1_, int p_179692_2_, int p_179692_3_, int p_179692_4_, int p_179692_5_, int p_179692_6_, Vector3 p_179692_7_, double p_179692_8_, double p_179692_10_) {
        for (Vector3 blockpos : Utils.getAllInBox(new Vector3(p_179692_1_, p_179692_2_, p_179692_3_), new Vector3(p_179692_1_ + p_179692_4_ - 1, p_179692_2_ + p_179692_5_ - 1, p_179692_3_ + p_179692_6_ - 1))) {
            double d0 = (double) blockpos.getX() + 0.5D - p_179692_7_.x;
            double d1 = (double) blockpos.getZ() + 0.5D - p_179692_7_.z;

            if (d0 * p_179692_8_ + d1 * p_179692_10_ >= 0.0D) {
                Block block = this.level.getBlock(blockpos);

                if (!block.canPassThrough()) {
                    return false;
                }
            }
        }

        return true;
    }

    public void setBreakDoors(boolean canBreakDoors) {
        this.nodeProcessor.setCanBreakDoors(canBreakDoors);
    }

    public void setEnterDoors(boolean enterDoors) {
        this.nodeProcessor.setCanEnterDoors(enterDoors);
    }

    public boolean getEnterDoors() {
        return this.nodeProcessor.getCanEnterDoors();
    }

    public void setCanSwim(boolean canSwim) {
        this.nodeProcessor.setCanSwim(canSwim);
    }

    public boolean getCanSwim() {
        return this.nodeProcessor.getCanSwim();
    }

    public void setAvoidSun(boolean avoidSun) {
        this.shouldAvoidSun = avoidSun;
    }
}
