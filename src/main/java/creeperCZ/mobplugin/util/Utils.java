package creeperCZ.mobplugin.util;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;

import java.util.Iterator;

/**
 * Created by CreeperFace on 16. 1. 2017.
 */
public class Utils {

    public static int getBlockId(ChunkManager level, Vector3 v) {
        return level.getBlockIdAt(v.getFloorX(), v.getFloorY(), v.getFloorZ());
    }

    public static Iterable<Vector3> getAllInBox(Vector3 from, Vector3 to) {
        final Vector3 blockpos = new Vector3(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final Vector3 blockpos1 = new Vector3(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        return new Iterable<Vector3>() {
            public Iterator<Vector3> iterator() {
                return new AbstractIterator<Vector3>() {
                    private Vector3 lastReturned;

                    protected Vector3 computeNext() {
                        if (this.lastReturned == null) {
                            this.lastReturned = blockpos;
                            return this.lastReturned;
                        } else if (this.lastReturned.equals(blockpos1)) {
                            return this.endOfData();
                        } else {
                            int i = this.lastReturned.getFloorX();
                            int j = this.lastReturned.getFloorY();
                            int k = this.lastReturned.getFloorZ();

                            if (i < blockpos1.getX()) {
                                ++i;
                            } else if (j < blockpos1.getY()) {
                                i = blockpos.getFloorX();
                                ++j;
                            } else if (k < blockpos1.getZ()) {
                                i = blockpos.getFloorX();
                                j = blockpos.getFloorY();
                                ++k;
                            }

                            this.lastReturned = new Vector3(i, j, k);
                            return this.lastReturned;
                        }
                    }
                };
            }
        };
    }

    public static RayTraceResult rayTraceBlocks(Level level, Vector3 vec31, Vector3 vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        int i = NukkitMath.floorDouble(vec32.x);
        int j = NukkitMath.floorDouble(vec32.y);
        int k = NukkitMath.floorDouble(vec32.z);
        int l = NukkitMath.floorDouble(vec31.x);
        int i1 = NukkitMath.floorDouble(vec31.y);
        int j1 = NukkitMath.floorDouble(vec31.z);
        Vector3 blockpos = new Vector3(l, i1, j1);
        Block block = level.getBlock(blockpos);

        if ((!ignoreBlockWithoutBoundingBox || block.getBoundingBox() != null)/* && block.canCollideCheck(block, stopOnLiquid)*/) {
            RayTraceResult raytraceresult = rayTrace(blockpos, vec31, vec32, block.getBoundingBox());

            if (raytraceresult != null) {
                return raytraceresult;
            }
        }

        RayTraceResult raytraceresult2 = null;
        int k1 = 200;

        while (k1-- >= 0) {
            if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z)) {
                return null;
            }

            if (l == i && i1 == j && j1 == k) {
                return returnLastUncollidableBlock ? raytraceresult2 : null;
            }

            boolean flag2 = true;
            boolean flag = true;
            boolean flag1 = true;
            double d0 = 999.0D;
            double d1 = 999.0D;
            double d2 = 999.0D;

            if (i > l) {
                d0 = (double) l + 1.0D;
            } else if (i < l) {
                d0 = (double) l + 0.0D;
            } else {
                flag2 = false;
            }

            if (j > i1) {
                d1 = (double) i1 + 1.0D;
            } else if (j < i1) {
                d1 = (double) i1 + 0.0D;
            } else {
                flag = false;
            }

            if (k > j1) {
                d2 = (double) j1 + 1.0D;
            } else if (k < j1) {
                d2 = (double) j1 + 0.0D;
            } else {
                flag1 = false;
            }

            double d3 = 999.0D;
            double d4 = 999.0D;
            double d5 = 999.0D;
            double d6 = vec32.x - vec31.x;
            double d7 = vec32.y - vec31.y;
            double d8 = vec32.z - vec31.z;

            if (flag2) {
                d3 = (d0 - vec31.x) / d6;
            }

            if (flag) {
                d4 = (d1 - vec31.y) / d7;
            }

            if (flag1) {
                d5 = (d2 - vec31.z) / d8;
            }

            if (d3 == -0.0D) {
                d3 = -1.0E-4D;
            }

            if (d4 == -0.0D) {
                d4 = -1.0E-4D;
            }

            if (d5 == -0.0D) {
                d5 = -1.0E-4D;
            }

            int face;

            if (d3 < d4 && d3 < d5) {
                face = i > l ? Vector3.SIDE_WEST : Vector3.SIDE_EAST;
                vec31 = new Vector3(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
            } else if (d4 < d5) {
                face = j > i1 ? Vector3.SIDE_DOWN : Vector3.SIDE_UP;
                vec31 = new Vector3(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
            } else {
                face = k > j1 ? Vector3.SIDE_NORTH : Vector3.SIDE_SOUTH;
                vec31 = new Vector3(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
            }

            l = NukkitMath.floorDouble(vec31.x) - (face == Vector3.SIDE_EAST ? 1 : 0);
            i1 = NukkitMath.floorDouble(vec31.y) - (face == Vector3.SIDE_UP ? 1 : 0);
            j1 = NukkitMath.floorDouble(vec31.z) - (face == Vector3.SIDE_SOUTH ? 1 : 0);
            blockpos = new Vector3(l, i1, j1);
            Block block1 = level.getBlock(blockpos);

            if (!ignoreBlockWithoutBoundingBox || block1.getId() == Block.NETHER_PORTAL || block1.getBoundingBox() != null) {
                //if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) { TODO: always true?
                RayTraceResult raytraceresult1 = rayTrace(blockpos, vec31, vec32, block1.getBoundingBox());

                if (raytraceresult1 != null) {
                    return raytraceresult1;
                }
                /*} else {
                    raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, face, blockpos);
                }*/
            }
        }

        return returnLastUncollidableBlock ? raytraceresult2 : null;
    }

    private static RayTraceResult rayTrace(Vector3 pos, Vector3 start, Vector3 end, AxisAlignedBB boundingBox) {
        Vector3 vec3d = start.subtract(pos.getX(), pos.getY(), pos.getZ());
        Vector3 vec3d1 = end.subtract(pos.getX(), pos.getY(), pos.getZ());
        MovingObjectPosition raytraceresult = boundingBox.calculateIntercept(vec3d, vec3d1);

        if (raytraceresult == null) {
            return null;
        }

        RayTraceResult result = new RayTraceResult(raytraceresult.hitVector, raytraceresult.sideHit);

        return new RayTraceResult(result.hitVec.add(pos.getX(), pos.getY(), pos.getZ()), raytraceresult.sideHit, pos);
    }

    public static float wrapDegrees(float value) {
        value = value % 360.0F;

        if (value >= 180.0F) {
            value -= 360.0F;
        }

        if (value < -180.0F) {
            value += 360.0F;
        }

        return value;
    }
}
