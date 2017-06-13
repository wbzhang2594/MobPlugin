package com.pikycz.mobplugin.entities.autospawn;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import com.pikycz.mobplugin.task.AutoSpawnTask;
import com.pikycz.mobplugin.FileLogger;
import com.pikycz.mobplugin.entities.utils.Utils;

import java.util.*;

//TODO: REWRITE 
/**
 * @author <a href="mailto:kniffman@googlemail.com">Michael Gertz</a>
 */
public abstract class AbstractEntitySpawner implements IEntitySpawner {

    protected AutoSpawnTask spawnTask;

    protected Server server;

    protected List<String> disabledSpawnWorlds;

    protected static Random random = new Random();

    public AbstractEntitySpawner(AutoSpawnTask spawnTask, Config config) {
        this.spawnTask = spawnTask;
        this.server = Server.getInstance();
        String disabledWorlds = config.getString("entities.worlds-spawn-disabled");
        if (disabledWorlds != null && !disabledWorlds.trim().isEmpty()) {
            StringTokenizer tokenizer = new StringTokenizer(disabledWorlds, ",");
            while (tokenizer.hasMoreTokens()) {
                disabledSpawnWorlds.add(tokenizer.nextToken());
            }
            FileLogger.debug(String.format("[%s] Disabled spawn for the following worlds: %s", getLogprefix(), disabledSpawnWorlds));
        }
    }

    /*
     * (@Override)
     * @see cn.nukkit.entity.ai.IEntitySpawner#spawn(java.util.List, java.util.List)
     */
    @Override
    public void spawn(Collection<Player> onlinePlayers) {
        // first spawn everything for online players ...
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
        for (String worldName : this.disabledSpawnWorlds) {
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

    protected abstract String getLogprefix();

}
