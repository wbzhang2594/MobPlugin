package com.pikycz.mobplugin.pathfinding;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import com.pikycz.mobplugin.entities.BaseEntity;
import java.util.ArrayList;

import java.util.List;

/**
 *
 * @author CreeperFace
 */
public class PathWorldListener {
    private final List<PathNavigate> navigators = new ArrayList<>();

    public void notifyBlockUpdate(Level level, Vector3 pos, Block oldState, Block newState, int flags) {
        if (this.didBlockChange(level, pos, oldState, newState)) {
            int i = 0;

            for (int j = this.navigators.size(); i < j; ++i) {
                PathNavigate pathnavigate = (PathNavigate) this.navigators.get(i);

                if (pathnavigate != null && !pathnavigate.canUpdatePathOnTimeout()) {
                    Path path = pathnavigate.getPath();

                    if (path != null && !path.isFinished() && path.getCurrentPathLength() != 0) {
                        PathPoint pathpoint = pathnavigate.currentPath.getFinalPathPoint();
                        double d0 = pos.distanceSquared(new Vector3(((double) pathpoint.xCoord + pathnavigate.theEntity.x) / 2.0D, ((double) pathpoint.yCoord + pathnavigate.theEntity.y) / 2.0D, ((double) pathpoint.zCoord + pathnavigate.theEntity.z) / 2.0D));
                        int k = (path.getCurrentPathLength() - path.getCurrentPathIndex()) * (path.getCurrentPathLength() - path.getCurrentPathIndex());

                        if (d0 < (double) k) {
                            pathnavigate.updatePath();
                        }
                    }
                }
            }
        }
    }

    protected boolean didBlockChange(Level level, Vector3 pos, Block oldState, Block newState) {
        AxisAlignedBB axisalignedbb = oldState.getBoundingBox();
        AxisAlignedBB axisalignedbb1 = newState.getBoundingBox();
        return axisalignedbb != axisalignedbb1 && (axisalignedbb == null || !axisalignedbb.equals(axisalignedbb1));
    }

    public void notifyLightSet(Vector3 pos) {
    }

    /**
     * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing.
     */
    /*public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2)
    {
    }
    public void playSoundToAllNearExcept(Player player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch)
    {
    }
    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
    }*/

    /**
     * Called on all IWorldAccesses when an entity is created or loaded. On client worlds, starts downloading any
     * necessary textures. On server worlds, adds the entity to the entity tracker.
     */
    public void onEntityAdded(Entity entityIn) {
        if (entityIn instanceof BaseEntity) {
            this.navigators.add(((BaseEntity) entityIn).getNavigator());
        }
    }

    /**
     * Called on all IWorldAccesses when an entity is unloaded or destroyed. On client worlds, releases any downloaded
     * textures. On server worlds, removes the entity from the entity tracker.
     */
    public void onEntityRemoved(Entity entityIn) {
        if (entityIn instanceof BaseEntity) {
            this.navigators.remove(((BaseEntity) entityIn).getNavigator());
        }
    }

    /*public void playRecord(SoundEvent soundIn, BlockPos pos)
    {
    }
    public void broadcastSound(int soundID, BlockPos pos, int data)
    {
    }
    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data)
    {
    }
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
    {
    }*/
}