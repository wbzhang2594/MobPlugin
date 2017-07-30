package com.pikycz.mobplugin.task;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.PluginTask;
import com.pikycz.mobplugin.configsection.PluginConfiguration;

/**
 *
 * @author PikyCZ
 */
public class AutoSpawnTask extends PluginTask {

    public AutoSpawnTask(Plugin plugin) {
        super(plugin);
        this.prepareSpawnerClasses();
    }

    @Override
    public void onRun(int currentTick) {
        //todo
    }
    
    private void prepareSpawnerClasses() {
        if (PluginConfiguration.spawnAnimals) {
            
        }
        
        if (PluginConfiguration.spawnMonsters) {
            
        }
    }
    
}
