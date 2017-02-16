package creeperCZ.mobplugin.pathfinding;

import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import creeperCZ.mobplugin.entities.BaseEntity;
import creeperCZ.mobplugin.utils.IntHashMap;

public abstract class NodeProcessor {
    protected Level level;
    protected BaseEntity entity;
    protected final IntHashMap<PathPoint> pointMap = new IntHashMap<>();
    protected int entitySizeX;
    protected int entitySizeY;
    protected int entitySizeZ;
    protected boolean canEnterDoors;
    protected boolean canBreakDoors;
    protected boolean canSwim;

    public void initProcessor(Level sourceIn, BaseEntity mob) {
        this.level = sourceIn;
        this.entity = mob;
        this.pointMap.clearMap();
        this.entitySizeX = NukkitMath.floorFloat(mob.getWidth() + 1.0F);
        this.entitySizeY = NukkitMath.floorFloat(mob.getHeight() + 1.0F);
        this.entitySizeZ = NukkitMath.floorFloat(mob.getWidth() + 1.0F);
    }

    /**
     * This method is called when all nodes have been processed and PathEntity is created.
     */
    public void postProcess() {
        this.level = null;
        this.entity = null;
    }

    /**
     * Returns a mapped point or creates and adds one
     */
    protected PathPoint openPoint(int x, int y, int z) {
        int i = PathPoint.makeHash(x, y, z);
        PathPoint pathpoint = this.pointMap.lookup(i);

        if (pathpoint == null) {
            pathpoint = new PathPoint(x, y, z);
            this.pointMap.addKey(i, pathpoint);
        }

        return pathpoint;
    }

    public abstract PathPoint getStart();

    /**
     * Returns PathPoint for given coordinates
     */
    public abstract PathPoint getPathPointToCoords(double x, double y, double z);

    public abstract int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance);

    public abstract PathNodeType getPathNodeType(Level blockaccessIn, int x, int y, int z, BaseEntity entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn);

    public abstract PathNodeType getPathNodeType(Level x, int y, int z, int p_186330_4_);

    public void setCanEnterDoors(boolean canEnterDoorsIn) {
        this.canEnterDoors = canEnterDoorsIn;
    }

    public void setCanBreakDoors(boolean canBreakDoorsIn) {
        this.canBreakDoors = canBreakDoorsIn;
    }

    public void setCanSwim(boolean canSwimIn) {
        this.canSwim = canSwimIn;
    }

    public boolean getCanEnterDoors() {
        return this.canEnterDoors;
    }

    public boolean getCanBreakDoors() {
        return this.canBreakDoors;
    }

    public boolean getCanSwim() {
        return this.canSwim;
    }
}
