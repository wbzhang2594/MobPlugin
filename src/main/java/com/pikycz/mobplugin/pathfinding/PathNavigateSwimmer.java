package com.pikycz.mobplugin.pathfinding;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import com.pikycz.mobplugin.entities.BaseEntity;
import com.pikycz.mobplugin.pathfinding.util.RayTraceResult;
import com.pikycz.mobplugin.pathfinding.util.Util;

/**
 *
 * @author CreeperFace
 */
public class PathNavigateSwimmer extends PathNavigate {
    public PathNavigateSwimmer(BaseEntity entitylivingIn, Level level) {
        super(entitylivingIn, level);
    }

    protected PathFinder getPathFinder() {
        return new PathFinder(new SwimNodeProcessor());
    }

    /**
     * If on ground or swimming and can swim
     */
    protected boolean canNavigate() {
        return this.isInLiquid();
    }

    protected Vector3 getEntityPosition() {
        return new Vector3(this.theEntity.x, this.theEntity.y + (double) this.theEntity.getHeight() * 0.5D, this.theEntity.z);
    }

    protected void pathFollow() {
        Vector3 pos = this.getEntityPosition();
        float f = this.theEntity.getWidth() * this.theEntity.getWidth();
        int i = 6;

        if (pos.distanceSquared(this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex())) < (double) f) {
            this.currentPath.incrementPathIndex();
        }

        for (int j = Math.min(this.currentPath.getCurrentPathIndex() + 6, this.currentPath.getCurrentPathLength() - 1); j > this.currentPath.getCurrentPathIndex(); --j) {
            Vector3 pos1 = this.currentPath.getVectorFromIndex(this.theEntity, j);

            if (pos1.distanceSquared(pos) <= 36.0D && this.isDirectPathBetweenPoints(pos, pos1, 0, 0, 0)) {
                this.currentPath.setCurrentPathIndex(j);
                break;
            }
        }

        this.checkForStuck(pos);
    }

    /**
     * Trims path data from the end to the first sun covered block
     */
    protected void removeSunnyPath() {
        super.removeSunnyPath();
    }

    /**
     * Checks if the specified entity can safely walk to the specified location.
     */
    protected boolean isDirectPathBetweenPoints(Vector3 posVec31, Vector3 posVec32, int sizeX, int sizeY, int sizeZ) {
        RayTraceResult raytraceresult = Util.rayTraceBlocks(this.level, posVec31, new Vector3(posVec32.x, posVec32.y + (double) this.theEntity.getHeight() * 0.5D, posVec32.z), false, true, false);
        return raytraceresult == null || raytraceresult.typeOfHit == RayTraceResult.Type.MISS;
    }

    public boolean canEntityStandOnPos(Vector3 pos) {
        Block b = this.level.getBlock(pos);

        return !b.isSolid();
    }
}