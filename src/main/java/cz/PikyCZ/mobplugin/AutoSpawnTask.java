package cz.PikyCZ.mobplugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cz.PikyCZ.mobplugin.entities.animal.flying.Bat;
import cz.PikyCZ.mobplugin.entities.animal.walking.Chicken;
import cz.PikyCZ.mobplugin.entities.animal.walking.Cow;
import cz.PikyCZ.mobplugin.entities.animal.walking.Donkey;
import cz.PikyCZ.mobplugin.entities.animal.walking.Horse;
import cz.PikyCZ.mobplugin.entities.animal.walking.Mooshroom;
import cz.PikyCZ.mobplugin.entities.animal.walking.Mule;
import cz.PikyCZ.mobplugin.entities.animal.walking.Ocelot;
import cz.PikyCZ.mobplugin.entities.animal.walking.Pig;
import cz.PikyCZ.mobplugin.entities.animal.walking.Rabbit;
import cz.PikyCZ.mobplugin.entities.animal.walking.Sheep;
import cz.PikyCZ.mobplugin.entities.animal.walking.SkeletonHorse;
import cz.PikyCZ.mobplugin.entities.animal.walking.ZombieHorse;
import cz.PikyCZ.mobplugin.entities.autospawn.IEntitySpawner;
import cz.PikyCZ.mobplugin.entities.monster.flying.Blaze;
import cz.PikyCZ.mobplugin.entities.monster.flying.Ghast;
import cz.PikyCZ.mobplugin.entities.monster.walking.CaveSpider;
import cz.PikyCZ.mobplugin.entities.monster.walking.Creeper;
import cz.PikyCZ.mobplugin.entities.monster.walking.Enderman;
import cz.PikyCZ.mobplugin.entities.monster.walking.IronGolem;
import cz.PikyCZ.mobplugin.entities.monster.walking.PigZombie;
import cz.PikyCZ.mobplugin.entities.monster.walking.Silverfish;
import cz.PikyCZ.mobplugin.entities.monster.walking.Skeleton;
import cz.PikyCZ.mobplugin.entities.monster.walking.SnowGolem;
import cz.PikyCZ.mobplugin.entities.monster.walking.Spider;
import cz.PikyCZ.mobplugin.entities.monster.walking.Wolf;
import cz.PikyCZ.mobplugin.entities.monster.walking.Zombie;
import cz.PikyCZ.mobplugin.entities.monster.walking.ZombieVillager;
import cz.PikyCZ.mobplugin.entities.spawners.BatSpawner;
import cz.PikyCZ.mobplugin.entities.spawners.ChickenSpawner;
import cz.PikyCZ.mobplugin.entities.spawners.CowSpawner;
import cz.PikyCZ.mobplugin.entities.spawners.CreeperSpawner;
import cz.PikyCZ.mobplugin.entities.spawners.EndermanSpawner;
import cz.PikyCZ.mobplugin.entities.spawners.OcelotSpawner;
import cz.PikyCZ.mobplugin.entities.spawners.PigSpawner;
import cz.PikyCZ.mobplugin.entities.spawners.RabbitSpawner;
import cz.PikyCZ.mobplugin.entities.spawners.SheepSpawner;
import cz.PikyCZ.mobplugin.entities.spawners.SkeletonSpawner;
import cz.PikyCZ.mobplugin.entities.spawners.SpiderSpawner;
import cz.PikyCZ.mobplugin.entities.spawners.WolfSpawner;
import cz.PikyCZ.mobplugin.entities.spawners.ZombieSpawner;
import cz.PikyCZ.mobplugin.entities.utils.Utils;

public class AutoSpawnTask implements Runnable {

    private Map<Integer, Integer> maxSpawns = new HashMap<>();

    private List<IEntitySpawner> entitySpawners = new ArrayList<>();

    private Config pluginConfig = null;

    // private MobPlugin plugin = null;

    public AutoSpawnTask(MobPlugin plugin) {
        this.pluginConfig = plugin.getConfig();
        // this.plugin = plugin;

        prepareMaxSpawns();
        try {
            prepareSpawnerClasses();
        } catch (Exception e) {
            FileLogger.warn("Unable to prepare spawner classes: ", e);
        }

        FileLogger.info("Starting AutoSpawnTask");
    }

    @Override
    public void run() {
        Collection<Player> onlinePlayers = Server.getInstance().getOnlinePlayers().values();
        if (onlinePlayers.size() > 0) {
            FileLogger.debug(String.format("Found %d online", onlinePlayers.size()));
            for (IEntitySpawner spawner : entitySpawners) {
                spawner.spawn(onlinePlayers);
            }
        } else {
            FileLogger.debug("No player online or offline found. Skipping auto spawn.");
        }

    }

    private void prepareSpawnerClasses() {
        entitySpawners.add(new BatSpawner(this, this.pluginConfig));
        entitySpawners.add(new ChickenSpawner(this, this.pluginConfig));
        entitySpawners.add(new CowSpawner(this, this.pluginConfig));
        entitySpawners.add(new CreeperSpawner(this, this.pluginConfig));
        entitySpawners.add(new EndermanSpawner(this, this.pluginConfig));
        entitySpawners.add(new OcelotSpawner(this, this.pluginConfig));
        entitySpawners.add(new PigSpawner(this, this.pluginConfig));
        entitySpawners.add(new RabbitSpawner(this, this.pluginConfig));
        entitySpawners.add(new SheepSpawner(this, this.pluginConfig));
        entitySpawners.add(new SkeletonSpawner(this, this.pluginConfig));
        entitySpawners.add(new SpiderSpawner(this, this.pluginConfig));
        entitySpawners.add(new WolfSpawner(this, this.pluginConfig));
        entitySpawners.add(new ZombieSpawner(this, this.pluginConfig));
        FileLogger.debug(String.format("prepared %d spawner classes", this.entitySpawners.size()));
    }

    private void prepareMaxSpawns() {
        maxSpawns.put(Bat.NETWORK_ID, this.pluginConfig.getInt("max-spawns.bat", 0));
        maxSpawns.put(Blaze.NETWORK_ID, this.pluginConfig.getInt("max-spawns.blaze", 0));
        maxSpawns.put(CaveSpider.NETWORK_ID, this.pluginConfig.getInt("max-spawns.cave-spider", 0));
        maxSpawns.put(Chicken.NETWORK_ID, this.pluginConfig.getInt("max-spawns.chicken", 0));
        maxSpawns.put(Cow.NETWORK_ID, this.pluginConfig.getInt("max-spawns.cow", 0));
        maxSpawns.put(Creeper.NETWORK_ID, this.pluginConfig.getInt("max-spawns.creeper", 0));
        maxSpawns.put(Donkey.NETWORK_ID, this.pluginConfig.getInt("max-spawns.donkey", 0));
        maxSpawns.put(Enderman.NETWORK_ID, this.pluginConfig.getInt("max-spawns.enderman", 0));
        maxSpawns.put(Ghast.NETWORK_ID, this.pluginConfig.getInt("max-spawns.ghast", 0));
        maxSpawns.put(Horse.NETWORK_ID, this.pluginConfig.getInt("max-spawns.horse", 0));
        maxSpawns.put(IronGolem.NETWORK_ID, this.pluginConfig.getInt("max-spawns.iron-golem", 0));
        maxSpawns.put(Mooshroom.NETWORK_ID, this.pluginConfig.getInt("max-spawns.mooshroom", 0));
        maxSpawns.put(Mule.NETWORK_ID, this.pluginConfig.getInt("max-spawns.mule", 0));
        maxSpawns.put(Ocelot.NETWORK_ID, this.pluginConfig.getInt("max-spawns.ocelot", 0));
        maxSpawns.put(Pig.NETWORK_ID, this.pluginConfig.getInt("max-spawns.pig", 0));
        maxSpawns.put(PigZombie.NETWORK_ID, this.pluginConfig.getInt("max-spawns.pig-zombie", 0));
        maxSpawns.put(Rabbit.NETWORK_ID, this.pluginConfig.getInt("max-spawns.rabbit", 0));
        maxSpawns.put(Silverfish.NETWORK_ID, this.pluginConfig.getInt("max-spawns.silverfish", 0));
        maxSpawns.put(Sheep.NETWORK_ID, this.pluginConfig.getInt("max-spawns.sheep", 0));
        maxSpawns.put(Skeleton.NETWORK_ID, this.pluginConfig.getInt("max-spawns.skeleton", 0));
        maxSpawns.put(SkeletonHorse.NETWORK_ID, this.pluginConfig.getInt("max-spawns.skeleton-horse", 0));
        maxSpawns.put(SnowGolem.NETWORK_ID, this.pluginConfig.getInt("max-spawns.snow-golem", 0));
        maxSpawns.put(Spider.NETWORK_ID, this.pluginConfig.getInt("max-spawns.spider", 0));
        maxSpawns.put(Wolf.NETWORK_ID, this.pluginConfig.getInt("max-spawns.wolf", 0));
        maxSpawns.put(Zombie.NETWORK_ID, this.pluginConfig.getInt("max-spawns.zombie", 0));
        maxSpawns.put(ZombieHorse.NETWORK_ID, this.pluginConfig.getInt("max-spawns.zombie-horse", 0));
        maxSpawns.put(ZombieVillager.NETWORK_ID, this.pluginConfig.getInt("max-spawns.zombie-villager", 0));

        FileLogger.debug(String.format(
                "max-spawns prepared [bat:%d] [blaze:%d] [caveSpider:%d] [chicken:%d] [cow:%d] [creeper:%d] [donkey:%d] [enderman:%d] [ghast:%d] [horse:%d] [ironGolem:%d] "
                        + "[mooshroom:%d] [mule:%d] [ocelot:%d] [pig:%d] [pigZombie:%d] [rabbit:%d] [silverfish:%d] [sheep:%d] [skeleton:%d] [skeletonHorse:%d] [snowGolem:%d] [spider:%d] [wolf:%d] [zombie:%d] "
                        + "[zombieHorse:%d] [zombieVillager:%d]",
                maxSpawns.get(Bat.NETWORK_ID), maxSpawns.get(Blaze.NETWORK_ID), maxSpawns.get(CaveSpider.NETWORK_ID),
                maxSpawns.get(Chicken.NETWORK_ID), maxSpawns.get(Cow.NETWORK_ID), maxSpawns.get(Creeper.NETWORK_ID),
                maxSpawns.get(Donkey.NETWORK_ID), maxSpawns.get(Enderman.NETWORK_ID), maxSpawns.get(Ghast.NETWORK_ID),
                maxSpawns.get(Horse.NETWORK_ID), maxSpawns.get(IronGolem.NETWORK_ID),
                maxSpawns.get(Mooshroom.NETWORK_ID), maxSpawns.get(Mule.NETWORK_ID), maxSpawns.get(Ocelot.NETWORK_ID),
                maxSpawns.get(Pig.NETWORK_ID), maxSpawns.get(PigZombie.NETWORK_ID), maxSpawns.get(Rabbit.NETWORK_ID),
                maxSpawns.get(Silverfish.NETWORK_ID), maxSpawns.get(Sheep.NETWORK_ID),
                maxSpawns.get(Skeleton.NETWORK_ID), maxSpawns.get(SkeletonHorse.NETWORK_ID),
                maxSpawns.get(SnowGolem.NETWORK_ID), maxSpawns.get(Spider.NETWORK_ID), maxSpawns.get(Wolf.NETWORK_ID),
                maxSpawns.get(Zombie.NETWORK_ID), maxSpawns.get(ZombieHorse.NETWORK_ID),
                maxSpawns.get(ZombieVillager.NETWORK_ID)));
    }

    public boolean entitySpawnAllowed(Level level, int networkId, String entityName) {
        int count = countEntity(level, networkId);
        FileLogger.debug(String.format("Found %s/%s living %s", count, maxSpawns.get(networkId), entityName));
        if (count < maxSpawns.get(networkId)) {
            return true;
        }
        return false;
    }

    private int countEntity(Level level, int networkId) {
        int count = 0;
        for (Entity entity : level.getEntities()) {
            if (entity.isAlive() && entity.getNetworkId() == networkId) {
                count++;
            }
        }
        return count;
    }

    public void createEntity(Object type, Position pos) {
        Entity entity = MobPlugin.create(type, pos);
        if (entity != null) {
            entity.spawnToAll();
        }
    }

    public int getRandomSafeXZCoord(int degree, int safeDegree, int correctionDegree) {
        int addX = Utils.rand(degree / 2 * -1, degree / 2);
        if (addX >= 0) {
            if (degree < safeDegree) {
                addX = safeDegree;
                addX += Utils.rand(correctionDegree / 2 * -1, correctionDegree / 2);
            }
        } else {
            if (degree > safeDegree) {
                addX = -safeDegree;
                addX += Utils.rand(correctionDegree / 2 * -1, correctionDegree / 2);
            }
        }
        return addX;
    }

    public int getSafeYCoord(Level level, Position pos, int needDegree) {
        int x = (int) pos.x;
        int y = (int) pos.y;
        int z = (int) pos.z;

        if (level.getBlockIdAt(x, y, z) == Block.AIR) {
            while (true) {
                y--;
                if (y > 127) {
                    y = 128;
                    break;
                }
                if (y < 1) {
                    y = 0;
                    break;
                }
                if (level.getBlockIdAt(x, y, z) != Block.AIR) {
                    int checkNeedDegree = needDegree;
                    int checkY = y;
                    while (true) {
                        checkY++;
                        checkNeedDegree--;
                        if (checkY > 255 || checkY < 1 || level.getBlockIdAt(x, checkY, z) != Block.AIR) {
                            break;
                        }

                        if (checkNeedDegree <= 0) {
                            return y;
                        }
                    }
                }
            }
        } else {
            while (true) {
                y++;
                if (y > 127) {
                    y = 128;
                    break;
                }

                if (y < 1) {
                    y = 0;
                    break;
                }

                if (level.getBlockIdAt(x, y, z) != Block.AIR) {
                    int checkNeedDegree = needDegree;
                    int checkY = y;
                    while (true) {
                        checkY--;
                        checkNeedDegree--;
                        if (checkY > 255 || checkY < 1 || level.getBlockIdAt(x, checkY, z) != Block.AIR) {
                            break;
                        }

                        if (checkNeedDegree <= 0) {
                            return y;
                        }
                    }
                }
            }
        }
        return y;
    }

}