/**
 * EntitySpawner.java
 * <p>
 * Created on 10:38:53
 */
package com.pikycz.mobplugin.entities.autospawn;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;

import java.util.Collection;

/**
 * @author <a href="mailto:kniffman@googlemail.com">Michael Gertz</a>
 */
public interface IEntitySpawner {

    /**
     *
     * @param onlinePlayers
     */
    public void spawn(Collection<Player> onlinePlayers);

    public SpawnResult spawn(IPlayer iPlayer, Position pos, Level level);

    public int getEntityNetworkId();

    public String getEntityName();

}
