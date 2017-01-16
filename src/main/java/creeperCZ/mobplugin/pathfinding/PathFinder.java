package creeperCZ.mobplugin.pathfinding;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import creeperCZ.mobplugin.entities.BaseEntity;

import java.util.HashSet;
import java.util.Set;

public class PathFinder {
    /**
     * The path being generated
     */
    private final PathHeap path = new PathHeap();
    private final Set<PathPoint> closedSet = new HashSet<>();

    /**
     * Selection of path points to add to the path
     */
    private final PathPoint[] pathOptions = new PathPoint[32];
    private final NodeProcessor nodeProcessor;

    public PathFinder(NodeProcessor processor) {
        this.nodeProcessor = processor;
    }

    public Path findPath(Level level, BaseEntity living, Entity entity, float range) {
        return this.findPath(level, living, entity.x, entity.getBoundingBox().minY, entity.z, range);
    }

    public Path findPath(Level level, BaseEntity living, Vector3 blockPos, float range) {
        return this.findPath(level, living, (double) ((float) blockPos.getX() + 0.5F), (double) ((float) blockPos.getY() + 0.5F), (double) ((float) blockPos.getZ() + 0.5F), range);
    }

    private Path findPath(Level level, BaseEntity living, double x, double y, double z, float range) {
        this.path.clearPath();
        this.nodeProcessor.initProcessor(level, living);
        PathPoint pathpoint = this.nodeProcessor.getStart();
        PathPoint pathpoint1 = this.nodeProcessor.getPathPointToCoords(x, y, z);
        Path path = this.findPath(pathpoint, pathpoint1, range);
        this.nodeProcessor.postProcess();
        return path;
    }

    private Path findPath(PathPoint startPoint, PathPoint finalPoint, float range) {
        startPoint.totalPathDistance = 0.0F;
        startPoint.distanceToNext = startPoint.distanceManhattan(finalPoint);
        startPoint.distanceToTarget = startPoint.distanceToNext;
        this.path.clearPath();
        this.closedSet.clear();
        this.path.addPoint(startPoint);
        PathPoint pathpoint = startPoint;
        int i = 0;

        while (!this.path.isPathEmpty()) {
            ++i;

            if (i >= 200) {
                break;
            }

            PathPoint pathpoint1 = this.path.dequeue();

            if (pathpoint1.equals(finalPoint)) {
                pathpoint = finalPoint;
                break;
            }

            if (pathpoint1.distanceManhattan(finalPoint) < pathpoint.distanceManhattan(finalPoint)) {
                pathpoint = pathpoint1;
            }

            pathpoint1.visited = true;
            int j = this.nodeProcessor.findPathOptions(this.pathOptions, pathpoint1, finalPoint, range);

            for (int k = 0; k < j; ++k) {
                PathPoint pathpoint2 = this.pathOptions[k];
                float f = pathpoint1.distanceManhattan(pathpoint2);
                pathpoint2.distanceFromOrigin = pathpoint1.distanceFromOrigin + f;
                pathpoint2.cost = f + pathpoint2.costMalus;
                float f1 = pathpoint1.totalPathDistance + pathpoint2.cost;

                if (pathpoint2.distanceFromOrigin < range && (!pathpoint2.isAssigned() || f1 < pathpoint2.totalPathDistance)) {
                    pathpoint2.previous = pathpoint1;
                    pathpoint2.totalPathDistance = f1;
                    pathpoint2.distanceToNext = pathpoint2.distanceManhattan(finalPoint) + pathpoint2.costMalus;

                    if (pathpoint2.isAssigned()) {
                        this.path.changeDistance(pathpoint2, pathpoint2.totalPathDistance + pathpoint2.distanceToNext);
                    } else {
                        pathpoint2.distanceToTarget = pathpoint2.totalPathDistance + pathpoint2.distanceToNext;
                        this.path.addPoint(pathpoint2);
                    }
                }
            }
        }

        if (pathpoint == startPoint) {
            return null;
        } else {
            Path path = this.createEntityPath(startPoint, pathpoint);
            return path;
        }
    }

    /**
     * Returns a new PathEntity for a given start and end point
     */
    private Path createEntityPath(PathPoint start, PathPoint end) {
        int i = 1;

        for (PathPoint pathpoint = end; pathpoint.previous != null; pathpoint = pathpoint.previous) {
            ++i;
        }

        PathPoint[] apathpoint = new PathPoint[i];
        PathPoint pathpoint1 = end;
        --i;

        for (apathpoint[i] = end; pathpoint1.previous != null; apathpoint[i] = pathpoint1) {
            pathpoint1 = pathpoint1.previous;
            --i;
        }

        return new Path(apathpoint);
    }
}
