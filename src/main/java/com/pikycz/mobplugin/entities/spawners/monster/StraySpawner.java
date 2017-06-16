package com.pikycz.mobplugin.entities.spawners.monster;

import cn.nukkit.IPlayer;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import com.pikycz.mobplugin.task.AutoSpawnTask;
import com.pikycz.mobplugin.FileLogger;
import com.pikycz.mobplugin.entities.autospawn.SpawnResult;
import com.pikycz.mobplugin.entities.monster.walking.Stray;
import com.pikycz.mobplugin.entities.spawners.BaseSpawner;

/**
 * @author PikyCZ
 */
public class StraySpawner extends BaseSpawner {

    /**
     * @param spawnTask
     * @param config
     */
    public StraySpawner(AutoSpawnTask spawnTask, Config config) {
        super(spawnTask, config);
    }

    /**
     * @param iPlayer
     * @param pos
     * @param level
     * @return
     */
    @Override
    public SpawnResult spawn(IPlayer iPlayer, Position pos, Level level) {
        SpawnResult result = SpawnResult.OK;

        if (level.getTime() > Level.TIME_NIGHT) {
            int blockLightLevel = level.getBlockLightAt((int) pos.x, (int) pos.y, (int) pos.z);

            if (blockLightLevel > 7 || (level.getTime() < Level.TIME_NIGHT && !level.isThundering() && !level.isRaining() && level.canBlockSeeSky(pos))) {
                result = SpawnResult.WRONG_LIGHTLEVEL;
            } else if (pos.y > 127 || pos.y < 1 || level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z) == Block.AIR) { // cannot spawn on AIR block
                result = SpawnResult.POSITION_MISMATCH;
            } else {
                this.spawnTask.createEntity(getEntityName(), pos.add(0, 2.8, 0));
            }

            FileLogger.info(String.format("[%s] spawn for %s at %s,%s,%s with lightlevel %s, result: %s", getLogprefix(), iPlayer.getName(), pos.x, pos.y, pos.z, blockLightLevel, result));
        } else {
            result = SpawnResult.WRONG_LIGHTLEVEL;
        }

        return result;
    }

    @Override
    public int getEntityNetworkId() {
        return Stray.NETWORK_ID;
    }

    @Override
    public String getEntityName() {
        return "Stray";
    }

    @Override
    protected String getLogprefix() {
        return this.getClass().getSimpleName();
    }
}
