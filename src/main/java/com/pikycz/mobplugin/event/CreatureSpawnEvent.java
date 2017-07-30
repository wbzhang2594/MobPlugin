package com.pikycz.mobplugin.event;

import cn.nukkit.api.API;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.plugin.PluginEvent;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.Plugin;

/**
 *
 * @author PikyCZ
 *///For Developers
@API(usage = API.Usage.BLEEDING, definition = API.Definition.UNIVERSAL)
public class CreatureSpawnEvent extends PluginEvent implements Cancellable {
    
    public Level pos;
    public int entityid;
    public Level level;
    public String type;
    
    public CreatureSpawnEvent(Plugin plugin) {
        super(plugin);
    }
    
    /**
     * Returns the position the entity is about to be spawned at.
     * @return Position
     */
    public Level getPosition() {
        return this.pos;
    }
    
    /**
     * Returns the Network ID from the entity about to be spawned.
     * @return Int
     */
    public int getEntityId(){
        return this.entityid;
    }
    
    /**
     * Returns the level the entity is about to spawn in.
     * @return Level
     */
    public Level getLevel() {
        return this.level;
    }
    
    /**
     * Returns the type of the entity about to be spawned. (Animal/Monster)
     * @return string
     */
    public String getType() {
        return this.type;
    }
    
}
