package com.pikycz.mobplugin.ai;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockGrass;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import com.pikycz.mobplugin.entities.BaseEntity;

/**
 *
 * Created by CreeperFace on 18. 1. 2017
 */
public class EntityAIEatGrass extends EntityAIBase {


    /**
     * The entity owner of this AITask
     */
    private final BaseEntity entit;

    /**
     * The world the grass eater entity is eating from
     */
    private final Level level;

    /**
     * Number of ticks since the entity started to eat grass
     */
    int eatingGrassTimer;

    public EntityAIEatGrass(BaseEntity grassEaterEntityIn) {
        this.entit = grassEaterEntityIn;
        this.level = grassEaterEntityIn.level;
        this.setMutexBits(7);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        if (this.entit.getLevel().rand.nextInt(this.entit.isBaby() ? 50 : 1000) != 0) {
            return false;
        } else {
            Block block = this.entit.getLevel().getBlock(this.entit);
            Vector3 posDown = this.entit.getSide(BlockFace.DOWN);

            return block.getId() == Block.TALL_GRASS && block.getDamage() == 1 || this.level.getBlockIdAt(posDown.getFloorX(), posDown.getFloorY(), posDown.getFloorZ()) == Block.GRASS;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.eatingGrassTimer = 40;
        //this.entit.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG); grass eating flag
        this.entit.getNavigator().clearPathEntity();
    }

    /**
     * Resets the task
     */
    public void resetTask() {
        this.eatingGrassTimer = 0;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting() {
        return this.eatingGrassTimer > 0;
    }

    /**
     * Number of ticks since the entity started to eat grass
     */
    public int getEatingGrassTimer() {
        return this.eatingGrassTimer;
    }

    /**
     * Updates the task
     */
    public void updateTask() {
        this.eatingGrassTimer = Math.max(0, this.eatingGrassTimer - 1);

        if (this.eatingGrassTimer == 4) {
            Block block = entit.getLevelBlock();

            if (block.getId() == Block.TALL_GRASS && block.getDamage() == 1) {
                /*if (this.level.getGameRules().getBoolean("mobGriefing"))
                {
                    this.level.destroyBlock(blockpos, false);
                }*/

                this.level.useBreakOn(entit);

                this.entit.eatGrassBonus();
            } else {
                Vector3 blockpos1 = entit.getSide(BlockFace.DOWN);

                if (this.level.getBlockIdAt(blockpos1.getFloorX(), blockpos1.getFloorY(), blockpos1.getFloorZ()) == Block.GRASS) {
                    /*if (this.level.getGameRules().getBoolean("mobGriefing"))
                    {
                        this.level.playEvent(2001, blockpos1, Block.getIdFromBlock(Blocks.GRASS));
                        this.level.setBlockState(blockpos1, Blocks.DIRT.getDefaultState(), 2);
                    }*/

                    this.level.addParticle(new DestroyBlockParticle(blockpos1.add(0.5, 0.5, 0.5), new BlockGrass()));
                    this.level.setBlock(blockpos1, new BlockDirt(), false, false);

                    this.entit.eatGrassBonus();
                }
            }
        }
    }
}
