package com.pikycz.mobplugin.entities.features;

import cn.nukkit.Player;

/**
 *
 * @author PikyCZ
 */
interface Shareable {
    
    public boolean shear(Player player);
    
    public boolean isSheared();
    
    public boolean setSheared(boolean sheared);

}
