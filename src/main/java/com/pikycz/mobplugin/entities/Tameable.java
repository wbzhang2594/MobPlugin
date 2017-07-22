package com.pikycz.mobplugin.entities;

import cn.nukkit.Player;
import cn.nukkit.api.API;

/**
 * Interface that is implemented in tameable entities
 */
@API(usage = API.Usage.DEPRECATED, definition = API.Definition.UNIVERSAL)
public interface Tameable {

    String NAMED_TAG_OWNER = "Owner";

    String NAMED_TAG_OWNER_UUID = "OwnerUUID";

    String NAMED_TAG_SITTING = "Sitting";

    Player getOwner();

    void setOwner(Player player);

    String getName();

}
