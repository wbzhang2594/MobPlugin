package creeperCZ.mobplugin.pathfinding;

import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import creeperCZ.mobplugin.entities.BaseEntity;

public abstract class PathNavigate {
    protected BaseEntity theEntity;
    protected Level level;

    /**
     * The PathEntity being followed.
     */
    protected Path currentPath;
    protected double speed;

    /**
     * The number of blocks (extra) +/- in each axis that get pulled out as cache for the pathfinder's search space
     */
    private final Attribute pathSearchRange;

    /**
     * Time, in number of ticks, following the current path
     */
    private int totalTicks;

    /**
     * The time when the last position check was done (to detect successful movement)
     */
    private int ticksAtLastPos;

    /**
     * Coordinates of the entity's position last time a check was done (part of monitoring getting 'stuck')
     */
    private Vector3 lastPosCheck = new Vector3();
    private Vector3 timeoutCachedNode = new Vector3();
    private long timeoutTimer;
    private long lastTimeoutCheck;
    private double timeoutLimit;
    private float maxDistanceToWaypoint = 0.5F;
    private boolean tryUpdatePath;
    private long lastTimeUpdated;
    protected NodeProcessor nodeProcessor;
    private Vector3 targetPos;
    private final PathFinder pathFinder;

    public PathNavigate(BaseEntity entitylivingIn, Level worldIn) {
        this.theEntity = entitylivingIn;
        this.level = worldIn;
        //this.pathSearchRange = entitylivingIn.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE); TODO
        this.pathSearchRange = Attribute.getAttribute(Attribute.FOLLOW_RANGE);
        this.pathFinder = this.getPathFinder();
    }

    protected abstract PathFinder getPathFinder();

    /**
     * Sets the speed
     */
    public void setSpeed(double speedIn) {
        this.speed = speedIn;
    }

    /**
     * Gets the maximum distance that the path finding will search in.
     */
    public float getPathSearchRange() {
        return this.pathSearchRange.getValue();
    }

    /**
     * Returns true if path can be changed by
     * onUpdateNavigation()}
     */
    public boolean canUpdatePathOnTimeout() {
        return this.tryUpdatePath;
    }

    public void updatePath() {
        if (this.level.getServer().getTick() - this.lastTimeUpdated > 20L) //probably tick?
        {
            if (this.targetPos != null) {
                this.currentPath = null;
                this.currentPath = this.getPathToPos(this.targetPos);
                this.lastTimeUpdated = this.level.getServer().getTick();
                this.tryUpdatePath = false;
            }
        } else {
            this.tryUpdatePath = true;
        }
    }

    /**
     * Returns the path to the given coordinates. Args : x, y, z
     */
    public final Path getPathToXYZ(double x, double y, double z) {
        return this.getPathToPos(new Vector3(x, y, z));
    }

    /**
     * Returns path to given BlockPos
     */
    public Path getPathToPos(Vector3 pos) {
        if (!this.canNavigate()) {
            return null;
        } else if (this.currentPath != null && !this.currentPath.isFinished() && pos.equals(this.targetPos)) {
            return this.currentPath;
        } else {
            this.targetPos = pos;
            float f = this.getPathSearchRange();
            //this.level.theProfiler.startSection("pathfind"); TODO: timings?
            Vector3 blockpos = this.theEntity.clone();
            int i = (int) (f + 8.0F);
            //BaseFullChunk chunkcache = this.level.getChunk(((int) blockpos.x - i) >> 4, ((int) blockpos.z + i) >> 4);
            Path path = this.pathFinder.findPath(this.level, this.theEntity, this.targetPos, f);
            //this.level.theProfiler.endSection();
            return path;
        }
    }

    /**
     * Returns the path to the given EntityLiving. Args : entity
     */
    public Path getPathToEntityLiving(Entity entityIn) {
        if (!this.canNavigate()) {
            return null;
        } else {
            Vector3 blockpos = entityIn.clone();

            if (this.currentPath != null && !this.currentPath.isFinished() && blockpos.equals(this.targetPos)) {
                return this.currentPath;
            } else {
                this.targetPos = blockpos;
                float f = this.getPathSearchRange();
                //this.level.theProfiler.startSection("pathfind"); TODO: timings
                Vector3 blockpos1 = this.theEntity.getSide(Vector3.SIDE_UP);
                int i = (int) (f + 16.0F);
                //BaseFullChunk chunkcache = this.level.getChunk(((int) blockpos.x - i) >> 4, ((int) blockpos.z + i) >> 4);
                Path path = this.pathFinder.findPath(this.level, this.theEntity, entityIn, f);
                //this.level.theProfiler.endSection();
                return path;
            }
        }
    }

    /**
     * Try to find and set a path to XYZ. Returns true if successful. Args : x, y, z, speed
     */
    public boolean tryMoveToXYZ(double x, double y, double z, double speedIn) {
        return this.setPath(this.getPathToXYZ(x, y, z), speedIn);
    }

    /**
     * Try to find and set a path to EntityLiving. Returns true if successful. Args : entity, speed
     */
    public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn) {
        Path path = this.getPathToEntityLiving(entityIn);
        return path != null && this.setPath(path, speedIn);
    }

    /**
     * Sets a new path. If it's diferent from the old path. Checks to adjust path for sun avoiding, and stores start
     * coords. Args : path, speed
     */
    public boolean setPath(Path pathentityIn, double speedIn) {
        if (pathentityIn == null) {
            this.currentPath = null;
            return false;
        } else {
            if (!pathentityIn.isSamePath(this.currentPath)) {
                this.currentPath = pathentityIn;
            }

            this.removeSunnyPath();

            if (this.currentPath.getCurrentPathLength() == 0) {
                return false;
            } else {
                this.speed = speedIn;
                Vector3 vector3 = this.getEntityPosition();
                this.ticksAtLastPos = this.totalTicks;
                this.lastPosCheck = vector3;
                return true;
            }
        }
    }

    /**
     * gets the actively used PathEntity
     */
    public Path getPath() {
        return this.currentPath;
    }

    public void onUpdateNavigation() {
        ++this.totalTicks;

        if (this.tryUpdatePath) {
            this.updatePath();
        }

        if (!this.noPath()) {
            if (this.canNavigate()) {
                this.pathFollow();
            } else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength()) {
                Vector3 vec3d = this.getEntityPosition();
                Vector3 vec3d1 = this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex());

                if (vec3d.y > vec3d1.y && !this.theEntity.onGround && NukkitMath.floorDouble(vec3d.x) == NukkitMath.floorDouble(vec3d1.x) && NukkitMath.floorDouble(vec3d.z) == NukkitMath.floorDouble(vec3d1.z)) {
                    this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
                }
            }

            if (!this.noPath()) {
                Vector3 vec3d2 = this.currentPath.getPosition(this.theEntity);

                if (vec3d2 != null) {
                    Vector3 blockpos = vec3d2.getSide(Vector3.SIDE_DOWN);
                    AxisAlignedBB axisalignedbb = this.level.getBlock(blockpos).getBoundingBox();
                    vec3d2 = vec3d2.subtract(0.0D, 1.0D - axisalignedbb.maxY, 0.0D);
                    this.theEntity.getMoveHelper().setMoveTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
                }
            }
        }
    }

    protected void pathFollow() {
        Vector3 vec3d = this.getEntityPosition();
        int i = this.currentPath.getCurrentPathLength();

        for (int j = this.currentPath.getCurrentPathIndex(); j < this.currentPath.getCurrentPathLength(); ++j) {
            if ((double) this.currentPath.getPathPointFromIndex(j).yCoord != Math.floor(vec3d.y)) {
                i = j;
                break;
            }
        }

        this.maxDistanceToWaypoint = this.theEntity.getWidth() > 0.75F ? this.theEntity.getWidth() / 2.0F : 0.75F - this.theEntity.getWidth() / 2.0F;
        Vector3 vec3d1 = this.currentPath.getCurrentPos();

        if (Math.abs((float) (this.theEntity.x - (vec3d1.x + 0.5D))) < this.maxDistanceToWaypoint && Math.abs((float) (this.theEntity.z - (vec3d1.z + 0.5D))) < this.maxDistanceToWaypoint && Math.abs(this.theEntity.y - vec3d1.y) < 1.0D) {
            this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
        }

        int k = NukkitMath.ceilFloat(this.theEntity.getWidth());
        int l = NukkitMath.ceilFloat(this.theEntity.getHeight());
        int i1 = k;

        for (int j1 = i - 1; j1 >= this.currentPath.getCurrentPathIndex(); --j1) {
            if (this.isDirectPathBetweenPoints(vec3d, this.currentPath.getVectorFromIndex(this.theEntity, j1), k, l, i1)) {
                this.currentPath.setCurrentPathIndex(j1);
                break;
            }
        }

        this.checkForStuck(vec3d);
    }

    /**
     * Checks if entity haven't been moved when last checked and if so, clears current
     */
    protected void checkForStuck(Vector3 positionVec3) {
        if (this.totalTicks - this.ticksAtLastPos > 100) {
            if (positionVec3.distanceSquared(this.lastPosCheck) < 2.25D) {
                this.clearPathEntity();
            }

            this.ticksAtLastPos = this.totalTicks;
            this.lastPosCheck = positionVec3;
        }

        if (this.currentPath != null && !this.currentPath.isFinished()) {
            Vector3 vec3d = this.currentPath.getCurrentPos();

            if (vec3d.equals(this.timeoutCachedNode)) {
                this.timeoutTimer += System.currentTimeMillis() - this.lastTimeoutCheck;
            } else {
                this.timeoutCachedNode = vec3d;
                double d0 = positionVec3.distance(this.timeoutCachedNode);
                //this.timeoutLimit = this.theEntity.getAIMoveSpeed() > 0.0F ? d0 / (double)this.theEntity.getAIMoveSpeed() * 1000.0D : 0.0D; //TODO: speed
                this.timeoutLimit = this.theEntity.getMovementSpeed() > 0.0F ? d0 / (double) this.theEntity.getMovementSpeed() * 1000.0D : 0.0D;
            }

            if (this.timeoutLimit > 0.0D && (double) this.timeoutTimer > this.timeoutLimit * 3.0D) {
                this.timeoutCachedNode = new Vector3();
                this.timeoutTimer = 0L;
                this.timeoutLimit = 0.0D;
                this.clearPathEntity();
            }

            this.lastTimeoutCheck = System.currentTimeMillis();
        }
    }

    /**
     * If null path or reached the end
     */
    public boolean noPath() {
        return this.currentPath == null || this.currentPath.isFinished();
    }

    /**
     * sets active PathEntity to null
     */
    public void clearPathEntity() {
        this.currentPath = null;
    }

    protected abstract Vector3 getEntityPosition();

    /**
     * If on ground or swimming and can swim
     */
    protected abstract boolean canNavigate();

    /**
     * Returns true if the entity is in water or lava, false otherwise
     */
    protected boolean isInLiquid() {
        return this.theEntity.isInsideOfWater() || this.theEntity.inLava;
    }

    /**
     * Trims path data from the end to the first sun covered block
     */
    protected void removeSunnyPath() {
    }

    /**
     * Checks if the specified entity can safely walk to the specified location.
     */
    protected abstract boolean isDirectPathBetweenPoints(Vector3 posVec31, Vector3 posVec32, int sizeX, int sizeY, int sizeZ);

    public boolean canEntityStandOnPos(Vector3 pos) {
        return this.level.getBlock(pos.getSide(Vector3.SIDE_DOWN)).isSolid();
    }

    public NodeProcessor func_189566_q() {
        return this.nodeProcessor;
    }
}
