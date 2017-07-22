package com.pikycz.mobplugin.entities;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.utils.BlockIterator;

import co.aikar.timings.Timings;

/**
 *
 * @author PikyCZ
 */
class InteractionHelper {

    /**
     * Just a helper function (for better finding where a button text is
     * displayed to player)
     *
     * @param string $text the text to be displayed in the button (we should
     * translate that!)
     * @param Player $player the player to display the text
     */
    public static void displayButtonText(String text, Player player) {
        //return player.setsetDataProperty(Entity.DATA_INTERACTIVE_TAG, Entity.DATA_TYPE_STRING, text);
    }

    /**
     * Returns the button text which is currently displayed to the player
     *
     * @param Player $player the player to get the button text for
     * @return string the button text, may be empty or NULL
     */
    public static String getButtonText(Player player) {
        //return player.getDataProperty(Entity.DATA_INTERACTIVE_TAG);
        return null;
    }

    /**
     * Returns the Entity the player is looking at currently
     *
     * @param Player $player the player to check
     * @param int $maxDistance the maximum distance to check for entities
     * @param bool $useCorrection define if correction should be used or not (if
     * so, the matching will increase but is more unprecise and it will consume
     * more performance)
     * @return mixed|null|Entity either NULL if no entity is found or an
     * instance of the entity
     */
    public static Entity getEntityPlayerLookingAt(Player player, int maxDistance, boolean useCorrection) {
        Timings.entityBaseTickTimer.startTiming();
        //("getEntityPlayerLookingAt [distance:maxDistance]");
        /**
         * @var Entity
         */
        Entity entity = null;
        // just a fix because player MAY not be fully initialized
        if (player.temporalVector != null) {
           // entity.getEntity = player.getLevel().getNearbyEntities(player.boundingBox.grow(maxDistance, maxDistance, maxDistance), player);
            // get all blocks in looking direction until the max interact distance is reached (it's possible that startblock isn't found!)
           // try {
                BlockIterator itr = new BlockIterator(player.getLevel(), player.getPosition(), player.getDirectionVector(), player.getEyeHeight(), maxDistance);
                if (itr != null) {
                    Block block = null;
                    entity = null;
                    while (itr.hasNext()) {
                        block = itr.next();
                       // entity = getEntityAtPosition(Entity, block.x, block.y, block.z, useCorrection);
                        if (entity != null) {
                            break;
                        }
                    }
                }
        }
        Timings.entityBaseTickTimer.stopTiming();
        //("getEntityPlayerLookingAt [distance:$maxDistance]", true);
        return entity;
    }

    /**
     * Returns the entity at the given position from the array of nearby
     * entities
     *
     * @param array $nearbyEntities an array of entity which are close to the
     * player
     * @param int $x the x corrdinate to search for any of the given entites
     * coordinates to match
     * @param int $y the y corrdinate to search for any of the given entites
     * coordinates to match
     * @param int $z the z corrdinate to search for any of the given entites
     * coordinates to match
     * @param bool $useCorrection set this to true if the matching should be
     * extended by -1 / +1 (in x, y, z directions)
     * @return mixed|null|Entity NULL when none of the given entities matched or
     * the first entity matching found
     */
    private static Entity getEntityAtPosition(Entity nearbyEntities, int x, int y, int z, boolean useCorrection) {
        if (nearbyEntities.getFloorX() == x && nearbyEntities.getFloorY() == y & nearbyEntities.getFloorZ() == z) {
            return nearbyEntities;
        } else if (useCorrection) { // when using correction, we search not only in front also 1 block up/down/left/right etc. pp
            return getCorrectedEntity(nearbyEntities, x, y, z);
        }
        return null;
    }

    /**
     * Searches around the given x, y, z coordinates (-1/+1) for the given
     * entity coordinates to match.
     *
     * @param Entity $entity the entity to check coordinates with
     * @param int $x the starting x position
     * @param int $y the starting y position
     * @param int $z the starting z position
     * @return null|Entity NULL when entity position doesn't match, an instance
     * of entity if it matches
     */
    private static Entity getCorrectedEntity(Entity entity, int x, int y, int z) {
        double entityX = entity.getFloorX();
        double entityY = entity.getFloorY();
        double entityZ = entity.getFloorZ();
        double searchX;
        double searchY;
        double searchZ;
        for (searchX = (x - 1); searchX <= (x + 1); searchX++) {
            for (searchY = (y - 1); searchY <= (y + 1); searchY++) {
                for (searchZ = (z - 1); searchZ <= (z + 1); searchZ++) {
                    if (entityX == searchX
                            && entityY == searchY && entityZ == searchZ) {
                        return entity;
                    }
                }
            }
        }
        return null;
    }
}
