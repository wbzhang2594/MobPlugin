package com.pikycz.mobplugin.entities.spawners;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockVector3;
import com.google.common.collect.Sets;
import com.pikycz.mobplugin.entities.animal.Animal;
import com.pikycz.mobplugin.entities.monster.Monster;

import java.util.Set;

/**
 * Created by CreeperFace on 10.5.2017.
 */
public class EntitySpawner {

    private static final int MOB_COUNT_DIV = (int) Math.pow(17.0D, 2.0D);
    private final Set<BlockVector3> eligibleChunksForSpawning = Sets.newHashSet();

    /*public int findChunksForSpawning(Level level, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate) {
        if (!spawnHostileMobs && !spawnPeacefulMobs) {
            return 0;
        } else {
            this.eligibleChunksForSpawning.clear();
            int i = 0;
            for (Player p : level.getPlayers().values()) {
                if (!p.isSpectator()) {
                    int j = NukkitMath.floorDouble(p.x / 16D);
                    int k = NukkitMath.floorDouble(p.z / 16D);
                    int l = 8;
                    for (int i1 = -8; i1 <= 8; ++i1) {
                        for (int j1 = -8; j1 <= 8; ++j1) {
                            boolean flag = i1 == -8 || i1 == 8 || j1 == -8 || j1 == 8;
                            BlockVector3 chunkpos = new BlockVector3(i1 + j, 0, j1 + k);
                            if (!this.eligibleChunksForSpawning.contains(chunkpos)) {
                                ++i;
                                if (!flag) {
                                    FullChunk chunk = level.getChunk(chunkpos.x, chunkpos.z, false);
                                    if (chunk != null && chunk.isLoaded()) {
                                        this.eligibleChunksForSpawning.add(chunkpos);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            int j4 = 0;
            Vector3 spawnLocation = level.getSpawnLocation();
            for (CreatureType creatureType : CreatureType.values()) {
                if ((!creatureType.getPeacefulCreature() || spawnPeacefulMobs) && (creatureType.getPeacefulCreature() || spawnHostileMobs) && (!creatureType.getAnimal() || spawnOnSetTickRate)) {
                    int entityCount = countEntities(level, creatureType.getCreatureClass());
                    int maxCount = creatureType.getMaxNumberOfCreature() * i / MOB_COUNT_DIV;
                    if (entityCount <= maxCount) {
                        Vector3 pos = new Vector3();
                        label411:
                        for (BlockVector3 chunkpos : this.eligibleChunksForSpawning) {
                            Vector3 blockpos = getRandomChunkPosition(level, chunkpos.x, chunkpos.z);
                            int k1 = (int) blockpos.getX();
                            int l1 = (int) blockpos.getY();
                            int i2 = (int) blockpos.getZ();
                            Block iblockstate = level.getBlock(blockpos);
                            if (!iblockstate.isNormalBlock()) {
                                int j2 = 0;
                                for (int k2 = 0; k2 < 3; ++k2) {
                                    int l2 = k1;
                                    int i3 = l1;
                                    int j3 = i2;
                                    int k3 = 6;
                                    Biome.SpawnListEntry biome$spawnlistentry = null;
                                    IEntityLivingData ientitylivingdata = null;
                                    int l3 = NukkitMath.ceilDouble(Math.random() * 4.0D);
                                    for (int i4 = 0; i4 < l3; ++i4) {
                                        l2 += level.rand.nextInt(6) - level.rand.nextInt(6);
                                        i3 += level.rand.nextInt(1) - level.rand.nextInt(1);
                                        j3 += level.rand.nextInt(6) - level.rand.nextInt(6);
                                        pos.set(l2, i3, j3);
                                        float f = (float) l2 + 0.5F;
                                        float f1 = (float) j3 + 0.5F;
                                        if (!level.isAnyPlayerWithinRangeAt((double) f, (double) i3, (double) f1, 24.0D) && spawnLocation.distanceSq((double) f, (double) i3, (double) f1) >= 576.0D) {
                                            if (biome$spawnlistentry == null) {
                                                biome$spawnlistentry = level.getSpawnListEntryForTypeAt(creatureType, pos);
                                                if (biome$spawnlistentry == null) {
                                                    break;
                                                }
                                            }
                                            if (level.canCreatureTypeSpawnHere(creatureType, biome$spawnlistentry, pos) && canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity(biome$spawnlistentry.entityClass), level, pos)) {
                                                EntityLiving entityliving;
                                                try {
                                                    entityliving = (EntityLiving) biome$spawnlistentry.entityClass.getConstructor(new Class[]{World.class}).newInstance(new Object[]{level});
                                                } catch (Exception exception) {
                                                    exception.printStackTrace();
                                                    return j4;
                                                }
                                                entityliving.setLocationAndAngles((double) f, (double) i3, (double) f1, level.rand.nextFloat() * 360.0F, 0.0F);
                                                if (entityliving.getCanSpawnHere() && entityliving.isNotColliding()) {
                                                    ientitylivingdata = entityliving.onInitialSpawn(level.getDifficultyForLocation(new BlockPos(entityliving)), ientitylivingdata);
                                                    if (entityliving.isNotColliding()) {
                                                        ++j2;
                                                        level.spawnEntityInWorld(entityliving);
                                                    } else {
                                                        entityliving.setDead();
                                                    }
                                                    if (j2 >= entityliving.getMaxSpawnedInChunk()) {
                                                        continue label411;
                                                    }
                                                }
                                                j4 += j2;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return j4;
        }
    }
    private static Vector3 getRandomChunkPosition(Level level, int x, int z) {
        Chunk chunk = (Chunk) level.getProvider().getChunk(x, z);
        int i = x * 16 + level.rand.nextInt(16);
        int j = z * 16 + level.rand.nextInt(16);
        int k = Utils.roundUp(chunk.getHeightMap(i, j) + 1, 16);
        //int l = level.rand.nextInt(k > 0 ? k : Utils.getHIghestFilledChunkSection(chunk) + 16 - 1);
        return new Vector3(i, k, j);
    }
    public static boolean isValidEmptySpawnBlock(Block state) {
        return !state.isNormalBlock() && (!state.isPowerSource() && (!(state instanceof BlockLiquid) && !(state instanceof BlockRail)));
    }
    public static boolean canCreatureTypeSpawnAtLocation(SpawnPlacementType spawnPlacementTypeIn, Level worldIn, Vector3 pos) {
        if (!worldIn.getWorldBorder().contains(pos)) {
            return false;
        } else {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            if (spawnPlacementTypeIn == EntityLiving.SpawnPlacementType.IN_WATER) {
                return iblockstate.getMaterial().isLiquid() && worldIn.getBlockState(pos.down()).getMaterial().isLiquid() && !worldIn.getBlockState(pos.up()).isNormalCube();
            } else {
                BlockPos blockpos = pos.down();
                if (!worldIn.getBlockState(blockpos).isFullyOpaque()) {
                    return false;
                } else {
                    Block block = worldIn.getBlockState(blockpos).getBlock();
                    boolean flag = block != Blocks.BEDROCK && block != Blocks.BARRIER;
                    return flag && isValidEmptySpawnBlock(iblockstate) && isValidEmptySpawnBlock(worldIn.getBlockState(pos.up()));
                }
            }
        }
    }
    public static void performWorldGenSpawning(World worldIn, Biome biomeIn, int p_77191_2_, int p_77191_3_, int p_77191_4_, int p_77191_5_, Random randomIn) {
        List<Biome.SpawnListEntry> list = biomeIn.getSpawnableList(CreatureType.CREATURE);
        if (!list.isEmpty()) {
            while (randomIn.nextFloat() < biomeIn.getSpawningChance()) {
                Biome.SpawnListEntry biome$spawnlistentry = (Biome.SpawnListEntry) WeightedRandom.getRandomItem(worldIn.rand, list);
                int i = biome$spawnlistentry.minGroupCount + randomIn.nextInt(1 + biome$spawnlistentry.maxGroupCount - biome$spawnlistentry.minGroupCount);
                IEntityLivingData ientitylivingdata = null;
                int j = p_77191_2_ + randomIn.nextInt(p_77191_4_);
                int k = p_77191_3_ + randomIn.nextInt(p_77191_5_);
                int l = j;
                int i1 = k;
                for (int j1 = 0; j1 < i; ++j1) {
                    boolean flag = false;
                    for (int k1 = 0; !flag && k1 < 4; ++k1) {
                        BlockPos blockpos = worldIn.getTopSolidOrLiquidBlock(new BlockPos(j, 0, k));
                        if (canCreatureTypeSpawnAtLocation(SpawnPlacementType.ON_GROUND, worldIn, blockpos)) {
                            EntityLiving entityliving;
                            try {
                                entityliving = (EntityLiving) biome$spawnlistentry.entityClass.getConstructor(new Class[]{World.class}).newInstance(new Object[]{worldIn});
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                continue;
                            }
                            entityliving.setLocationAndAngles((double) ((float) j + 0.5F), (double) blockpos.getY(), (double) ((float) k + 0.5F), randomIn.nextFloat() * 360.0F, 0.0F);
                            worldIn.spawnEntityInWorld(entityliving);
                            ientitylivingdata = entityliving.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityliving)), ientitylivingdata);
                            flag = true;
                        }
                        j += randomIn.nextInt(5) - randomIn.nextInt(5);
                        for (k += randomIn.nextInt(5) - randomIn.nextInt(5); j < p_77191_2_ || j >= p_77191_2_ + p_77191_4_ || k < p_77191_3_ || k >= p_77191_3_ + p_77191_4_; k = i1 + randomIn.nextInt(5) - randomIn.nextInt(5)) {
                            j = l + randomIn.nextInt(5) - randomIn.nextInt(5);
                        }
                    }
                }
            }
        }
    }*/

    public enum SpawnPlacementType {
        ON_GROUND,
        IN_AIR,
        IN_WATER;
    }

    public enum CreatureType {
        MONSTER(Monster.class, 70, Block.AIR, false, false),
        CREATURE(Animal.class, 10, Block.AIR, true, true);

        private final Class<?> creatureClass;
        private final int maxNumberOfCreature;
        private final int creatureMaterial;

        /**
         * A flag indicating whether this creature type is peaceful.
         */
        private final boolean isPeacefulCreature;

        /**
         * Whether this creature type is an animal.
         */
        private final boolean isAnimal;

        CreatureType(Class<?> creatureClassIn, int maxNumberOfCreatureIn, int creatureMaterialIn, boolean isPeacefulCreatureIn, boolean isAnimalIn) {
            this.creatureClass = creatureClassIn;
            this.maxNumberOfCreature = maxNumberOfCreatureIn;
            this.creatureMaterial = creatureMaterialIn;
            this.isPeacefulCreature = isPeacefulCreatureIn;
            this.isAnimal = isAnimalIn;
        }

        public Class<?> getCreatureClass() {
            return this.creatureClass;
        }

        public int getMaxNumberOfCreature() {
            return this.maxNumberOfCreature;
        }

        /**
         * Gets whether or not this creature type is peaceful.
         */
        public boolean getPeacefulCreature() {
            return this.isPeacefulCreature;
        }

        /**
         * Return whether this creature type is an animal.
         */
        public boolean getAnimal() {
            return this.isAnimal;
        }
    }

    private static int countEntities(Level level, Class<?> type) {
        int count = 0;

        for (Entity entity : level.getEntities()) {
            if (!entity.closed && type.isAssignableFrom(entity.getClass())) {
                count++;
            }
        }

        return count;
    }
}