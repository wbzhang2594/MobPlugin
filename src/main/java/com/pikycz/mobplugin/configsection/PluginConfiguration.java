package com.pikycz.mobplugin.configsection;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.utils.ConfigSection;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author PikyCZ
 */
public class PluginConfiguration {
    
    private ConfigSection entities;
    
    public final HashMap<Integer, Level> levelsToSpawn = new HashMap<>();
    private List<String> disabledWorlds;
    public static boolean spawnAnimals = true;
    public static boolean spawnMonsters = true;
    
    
    
    public void disableWorlds() {
        for (Level level : Server.getInstance().getLevels().values()) {
            if (disabledWorlds.contains(level.getFolderName().toLowerCase())) {
                continue;
            }

            levelsToSpawn.put(level.getId(), level);
        }
    }
    
    public boolean spawnAnimals(){
        return false;    
    }
    
    public boolean spawnMonsters(){
        return false;      
    }
}
