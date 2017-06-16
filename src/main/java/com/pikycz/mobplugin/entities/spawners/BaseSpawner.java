package com.pikycz.mobplugin.entities.spawners;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import com.pikycz.mobplugin.FileLogger;

import com.pikycz.mobplugin.MobPlugin;
import com.pikycz.mobplugin.entities.autospawn.Difficulty;
import com.pikycz.mobplugin.entities.autospawn.IEntitySpawner;
import com.pikycz.mobplugin.entities.autospawn.SpawnResult;
import com.pikycz.mobplugin.entities.utils.Utils;
import com.pikycz.mobplugin.task.AutoSpawnTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author PikyCZ
 */
public abstract class BaseSpawner implements IEntitySpawner {

    protected MobPlugin plugin;

    protected AutoSpawnTask spawnTask;

    protected Server server;

    public final HashMap<Integer, Level> levelsToSpawn = new HashMap<>();

    private List<String> disabledWorlds;

    protected int maxSpawn = -1;

    private static final int MIN_SPAWN_RADIUS = 8; // in blocks

    private int counter = 0;

    public static final int NETWORK_ID = Entity.NETWORK_ID;
    
    public BaseSpawner (AutoSpawnTask spawnTask, Config config) {
        this.server = Server.getInstance();
        String DisableWorlds = config.getString("worlds-spawn-disabled");
        if (DisableWorlds != null && !DisableWorlds.trim().isEmpty()) {
            StringTokenizer tokenizer = new StringTokenizer(DisableWorlds, ",");
            while (tokenizer.hasMoreTokens()) {
                disabledWorlds.add(tokenizer.nextToken());
            }
            FileLogger.debug(String.format("[%s] Disabled spawn for the following worlds: %s", getLogprefix(), disabledWorlds));
        }
    }

    /**
     * @param Level level
     * @return bool
     */
    protected boolean spawnAllowedByEntityCount(Level level, Entity entity) {
        if (this.maxSpawn <= 0) {
            return false;
        }
        counter = 0;
        if (entity.isAlive() && !entity.closed && entity.NETWORK_ID == this.getNetworkId()) {
            counter++;
        }

        if (counter < this.maxSpawn) {
            return true;
        }
        return false;
    }

    public void spawn(Collection<Player> onlinePlayers) {
        if (isSpawnAllowedByDifficulty()) {
            SpawnResult lastSpawnResult = null;
            for (Player player : onlinePlayers) {
                if (isWorldSpawnAllowed(player.getLevel())) {
                    lastSpawnResult = spawn(player);
                    if (lastSpawnResult.equals(SpawnResult.MAX_SPAWN_REACHED)) {
                        break;
                    }
                }
            }
        } else {
            FileLogger.debug(String.format("[%s] Spawn not allowed because of difficulty [entityName:%s]", getLogprefix(), getEntityName()));
        }
    }

    /**
     * Checks if the given level's name is on blacklist for auto spawn
     *
     * @param level the level to be checked
     * @return <code>true</code> when world spawn is allowed
     */
    private boolean isWorldSpawnAllowed(Level level) {
        for (String worldName : this.disabledWorlds) {
            if (level.getName().toLowerCase().equals(worldName.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    protected SpawnResult spawn(IPlayer iPlayer) {
        Position pos = ((Player) iPlayer).getPosition();
        Level level = ((Player) iPlayer).getLevel();

        if (this.spawnTask.entitySpawnAllowed(level, getEntityNetworkId(), getEntityName())) {
            if (pos != null) {
                // get a random safe position for spawn
                pos.x += this.spawnTask.getRandomSafeXZCoord(50, 26, 6);
                pos.z += this.spawnTask.getRandomSafeXZCoord(50, 26, 6);
                pos.y = this.spawnTask.getSafeYCoord(level, pos, 3);
            }

            if (pos == null) {
                return SpawnResult.POSITION_MISMATCH;
            }
        } else {
            return SpawnResult.MAX_SPAWN_REACHED;
        }

        return spawn(iPlayer, pos, level);
    }

    public boolean checkPlayerDistance(Player player, Position pos) {
        return player.distance(pos) > MIN_SPAWN_RADIUS;
    }

    /**
     * A simple method that evaluates based on the difficulty set in server if a
     * spawn is allowed or not
     *
     * @return
     */
    protected boolean isSpawnAllowedByDifficulty() {

        int randomNumber = Utils.rand(0, 4);

        switch (getCurrentDifficulty()) {
            case PEACEFUL:
                return randomNumber == 0;
            case EASY:
                return randomNumber <= 1;
            case NORMAL:
                return randomNumber <= 2;
            case HARD:
                return true; // in hard: always spawn
            default:
                return true;
        }
    }

    /**
     * Returns currently set difficulty as en {@link Enum}
     *
     * @return a {@link Difficulty} instance
     */
    protected Difficulty getCurrentDifficulty() {
        return Difficulty.getByDiffculty(this.server.getDifficulty());
    }

    public int getNetworkId() {
        return NETWORK_ID;
    }

    protected abstract String getLogprefix();

}
