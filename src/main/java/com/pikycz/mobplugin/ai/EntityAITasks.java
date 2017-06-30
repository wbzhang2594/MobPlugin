package com.pikycz.mobplugin.ai;

import cn.nukkit.utils.MainLogger;
import co.aikar.timings.Timing;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * Created by CreeperFace on 18. 1. 2017
 */
public class EntityAITasks {
    private static final MainLogger LOGGER = MainLogger.getLogger();
    private final Set<EntityAITaskEntry> taskEntries = new LinkedHashSet<>();
    private final Set<EntityAITasks.EntityAITaskEntry> executingTaskEntries = new LinkedHashSet<>();

    /**
     * Instance of Profiler.
     */
    private final Timing timing;
    private int tickCount;
    private int tickRate = 3;
    private int disabledControlFlags;

    public EntityAITasks() {
        this.timing = null;
    }

    /**
     * Add a now AITask. Args : priority, task
     */
    public void addTask(int priority, EntityAIBase task) {
        this.taskEntries.add(new EntityAITasks.EntityAITaskEntry(priority, task));
    }

    /**
     * removes the indicated task from the entity's AI tasks.
     */
    public void removeTask(EntityAIBase task) {
        Iterator<EntityAITaskEntry> iterator = this.taskEntries.iterator();

        while (iterator.hasNext()) {
            EntityAITasks.EntityAITaskEntry aiTaskEntry = (EntityAITasks.EntityAITaskEntry) iterator.next();
            EntityAIBase entityaibase = aiTaskEntry.action;

            if (entityaibase == task) {
                if (aiTaskEntry.using) {
                    aiTaskEntry.using = false;
                    aiTaskEntry.action.resetTask();
                    this.executingTaskEntries.remove(aiTaskEntry);
                }

                iterator.remove();
                return;
            }
        }
    }

    public void onUpdateTasks() {
        //this.timing.startSection("goalSetup");

        if (this.tickCount++ % this.tickRate == 0) {
            for (EntityAITasks.EntityAITaskEntry aiTaskEntry : this.taskEntries) {
                if (aiTaskEntry.using) {
                    if (!this.canUse(aiTaskEntry) || !this.canContinue(aiTaskEntry)) {
                        aiTaskEntry.using = false;
                        aiTaskEntry.action.resetTask();
                        this.executingTaskEntries.remove(aiTaskEntry);
                    }
                } else if (this.canUse(aiTaskEntry) && aiTaskEntry.action.shouldExecute()) {
                    aiTaskEntry.using = true;
                    aiTaskEntry.action.startExecuting();
                    this.executingTaskEntries.add(aiTaskEntry);
                }
            }
        } else {
            Iterator<EntityAITasks.EntityAITaskEntry> iterator = this.executingTaskEntries.iterator();

            while (iterator.hasNext()) {
                EntityAITasks.EntityAITaskEntry aiTaskEntry = (EntityAITasks.EntityAITaskEntry) iterator.next();

                if (!this.canContinue(aiTaskEntry)) {
                    aiTaskEntry.using = false;
                    aiTaskEntry.action.resetTask();
                    iterator.remove();
                }
            }
        }

        //
        if (!this.executingTaskEntries.isEmpty()) {
            //this.timing.startSection("goalTick");

            for (EntityAITasks.EntityAITaskEntry aiTaskEntry : this.executingTaskEntries) {
                aiTaskEntry.action.updateTask();
            }

            //this.timing.endSection();
        }
    }

    /**
     * Determine if a specific AI Task should continue being executed.
     */
    private boolean canContinue(EntityAITasks.EntityAITaskEntry taskEntry) {
        return taskEntry.action.continueExecuting();
    }

    /**
     * Determine if a specific AI Task can be executed, which means that all running higher (= lower int value) priority
     * tasks are compatible with it or all lower priority tasks can be interrupted.
     */
    private boolean canUse(EntityAITasks.EntityAITaskEntry taskEntry) {
        if (this.executingTaskEntries.isEmpty()) {
            return true;
        } else if (this.isControlFlagDisabled(taskEntry.action.getMutexBits())) {
            return false;
        } else {
            for (EntityAITasks.EntityAITaskEntry aiTaskEntry : this.executingTaskEntries) {
                if (aiTaskEntry != taskEntry) {
                    if (taskEntry.priority >= aiTaskEntry.priority) {
                        if (!this.areTasksCompatible(taskEntry, aiTaskEntry)) {
                            return false;
                        }
                    } else if (!aiTaskEntry.action.isInterruptible()) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    /**
     * Returns whether two EntityAITaskEntries can be executed concurrently
     */
    private boolean areTasksCompatible(EntityAITasks.EntityAITaskEntry taskEntry1, EntityAITasks.EntityAITaskEntry taskEntry2) {
        return (taskEntry1.action.getMutexBits() & taskEntry2.action.getMutexBits()) == 0;
    }

    public boolean isControlFlagDisabled(int flag) {
        return (this.disabledControlFlags & flag) > 0;
    }

    public void disableControlFlag(int flag) {
        this.disabledControlFlags |= flag;
    }

    public void enableControlFlag(int flag) {
        this.disabledControlFlags &= ~flag;
    }

    public void setControlFlag(int flag, boolean value) {
        if (value) {
            this.enableControlFlag(flag);
        } else {
            this.disableControlFlag(flag);
        }
    }

    class EntityAITaskEntry {
        public final EntityAIBase action;
        public final int priority;
        public boolean using;

        public EntityAITaskEntry(int priorityIn, EntityAIBase task) {
            this.priority = priorityIn;
            this.action = task;
        }

        public boolean equals(Object obj) {
            return this == obj ? true : (obj != null && this.getClass() == obj.getClass() ? this.action.equals(((EntityAITasks.EntityAITaskEntry) obj).action) : false);
        }

        public int hashCode() {
            return this.action.hashCode();
        }
    }
}
