package creeperCZ.mobplugin.entities.spawners;

import cn.nukkit.IPlayer;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import creeperCZ.mobplugin.AutoSpawnTask;
import creeperCZ.mobplugin.FileLogger;
import creeperCZ.mobplugin.entities.autospawn.AbstractEntitySpawner;
import creeperCZ.mobplugin.entities.autospawn.SpawnResult;
import creeperCZ.mobplugin.entities.monster.walking.Husk;

/**
 * @author PikyCZ
 */
public class HuskSpawner extends AbstractEntitySpawner {

    public HuskSpawner(AutoSpawnTask spawnTask, Config pluginConfig) {
        super(spawnTask, pluginConfig);
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

        // TODO: a zombie may also be spawned with items ... but that's to be done later

        int blockLightLevel = level.getBlockLightAt((int) pos.x, (int) pos.y, (int) pos.z);

        if (blockLightLevel > 7) {
            result = SpawnResult.WRONG_LIGHTLEVEL;
        } else if (pos.y > 127 || pos.y < 1 || level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z) == Block.AIR) { // cannot spawn on AIR block
            result = SpawnResult.POSITION_MISMATCH;
        } else {
            this.spawnTask.createEntity(getEntityName(), pos.add(0, 2.8, 0));
        }

        FileLogger.info(String.format("[%s] spawn for %s at %s,%s,%s with lightlevel %s, result: %s", getLogprefix(), iPlayer.getName(), pos.x, pos.y, pos.z, blockLightLevel, result));

        return result;
    }

    /* (@Override)
     * @see cn.nukkit.entity.ai.IEntitySpawner#getEntityNetworkId()
     */
    @Override
    public int getEntityNetworkId() {
        return Husk.NETWORK_ID;
    }

    /* (@Override)
     * @see cn.nukkit.entity.ai.IEntitySpawner#getEntityName()
     */
    @Override
    public String getEntityName() {
        return "Husk";
    }

    /* (@Override)
     * @see de.kniffo80.mobplugin.entities.autospawn.AbstractEntitySpawner#getLogprefix()
     */
    @Override
    protected String getLogprefix() {
        return this.getClass().getSimpleName();
    }

}

