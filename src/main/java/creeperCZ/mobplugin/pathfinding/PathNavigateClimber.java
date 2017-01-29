package creeperCZ.mobplugin.pathfinding;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import creeperCZ.mobplugin.entities.BaseEntity;

public class PathNavigateClimber extends PathNavigateGround {
    /**
     * Current path navigation target
     */
    private Vector3 targetPosition;

    public PathNavigateClimber(BaseEntity entityLivingIn, Level worldIn) {
        super(entityLivingIn, worldIn);
    }

    /**
     * Returns path to given BlockPos
     */
    public Path getPathToPos(Vector3 pos) {
        this.targetPosition = pos;
        return super.getPathToPos(pos);
    }

    /**
     * Returns the path to the given EntityLiving. Args : entity
     */
    public Path getPathToEntityLiving(Entity entityIn) {
        this.targetPosition = entityIn.clone();
        return super.getPathToEntityLiving(entityIn);
    }

    /**
     * Try to find and set a path to EntityLiving. Returns true if successful. Args : entity, speed
     */
    public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn) {
        Path path = this.getPathToEntityLiving(entityIn);

        if (path != null) {
            return this.setPath(path, speedIn);
        } else {
            this.targetPosition = entityIn.clone();
            this.speed = speedIn;
            return true;
        }
    }

    public void onUpdateNavigation() {
        if (!this.noPath()) {
            super.onUpdateNavigation();
        } else {
            if (this.targetPosition != null) {
                double d0 = (double) (this.theEntity.getWidth() * this.theEntity.getWidth());

                if (this.theEntity.distanceSquared(this.targetPosition.floor().add(0.5, 0, 0.5)) >= d0 && (this.theEntity.y <= (double) this.targetPosition.getY() || this.theEntity.distanceSquared(new Vector3(this.targetPosition.getFloorX() + 0.5, NukkitMath.floorDouble(this.theEntity.y), this.targetPosition.getFloorZ() + 0.5)) >= d0)) {
                    this.theEntity.getMoveHelper().setMoveTo((double) this.targetPosition.getX(), (double) this.targetPosition.getY(), (double) this.targetPosition.getZ(), this.speed);
                } else {
                    this.targetPosition = null;
                }
            }
        }
    }
}
