package com.pikycz.mobplugin.event;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.event.level.LevelUnloadEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;
import com.pikycz.mobplugin.utils.Utils;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author PikyCZ
 */
public class EventHandler {

    public final HashMap<Integer, Level> levelsToSpawn = new HashMap<>();
    private List<String> disabledWorlds;

    @cn.nukkit.event.EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onLevelLoad(LevelLoadEvent e) {
        Level level = e.getLevel();

        if (!disabledWorlds.contains(level.getFolderName())) {
            levelsToSpawn.put(level.getId(), level);
        }
    }

    /**
     *
     * @param e
     */
    @cn.nukkit.event.EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onLevelUnload(LevelUnloadEvent e) {
        Level level = e.getLevel();

        levelsToSpawn.remove(level.getId());
    }

}
