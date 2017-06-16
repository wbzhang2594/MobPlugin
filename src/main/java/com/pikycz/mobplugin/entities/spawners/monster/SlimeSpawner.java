package com.pikycz.mobplugin.entities.spawners.monster;

import cn.nukkit.IPlayer;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.utils.Config;
import com.pikycz.mobplugin.FileLogger;
import com.pikycz.mobplugin.entities.autospawn.SpawnResult;
import com.pikycz.mobplugin.entities.monster.walking.Slime;
import com.pikycz.mobplugin.entities.spawners.BaseSpawner;
import com.pikycz.mobplugin.task.AutoSpawnTask;
import com.pikycz.mobplugin.entities.utils.Utils;

/**
 *
 * @author PikyCZ
 */

public class SlimeSpawner extends BaseSpawner{

    public SlimeSpawner(AutoSpawnTask spawnTask, Config config) {
        super(spawnTask, config);
    }

    @Override
    protected String getLogprefix() {
       return this.getClass().getSimpleName();
    }

    @Override
    public SpawnResult spawn(IPlayer iPlayer, Position pos, Level level) {
        SpawnResult result = SpawnResult.OK;

        // as enderman spawn very seldom, we need another random spawn here ...
        if (Utils.rand(0, 3) > 0) { // spawn with a 1/3 chance (it's possible that they also spawn in overworld!)
            return SpawnResult.SPAWN_DENIED;
        }

        int blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
        int blockLightLevel = level.getBlockLightAt((int) pos.x, (int) pos.y, (int) pos.z);
        int biomeId = level.getBiomeId((int) pos.x, (int) pos.z);

        if (biomeId != Biome.SWAMP) {
            result = SpawnResult.WRONG_BLOCK;
        } else if (pos.y > 127 || pos.y < 1 || level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z) == Block.AIR) { // cannot spawn on AIR block
            result = SpawnResult.POSITION_MISMATCH;
        } else {
            this.spawnTask.createEntity(getEntityName(), pos.add(0, 3.8, 0));
        }

        FileLogger.info(String.format("[%s] spawn for %s at %s,%s,%s with lightlevel %s and blockId %s, result: %s", getLogprefix(), iPlayer.getName(), pos.x, pos.y, pos.z, blockLightLevel, blockId, result));

        return result;
    }

    @Override
    public int getEntityNetworkId() {
        return Slime.NETWORK_ID;
    }

    @Override
    public String getEntityName() {
        return "Slime";
    }
    
}
