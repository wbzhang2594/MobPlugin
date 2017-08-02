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

    @cn.nukkit.event.EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onDie(PlayerDeathEvent event) {
        EntityDamageEvent lastDamageEvent = event.getEntity().getLastDamageCause();
        EntityDamageEvent.DamageCause cause = lastDamageEvent.getCause();
        Player player = ((Player) event.getEntity());
        String message = "&e" + player.getName() + "&4 died";
        switch (cause) {
            case ENTITY_ATTACK:
                if (lastDamageEvent instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent ebde = (EntityDamageByEntityEvent) lastDamageEvent;
                    if (ebde.getDamager() instanceof Player) {
                        Player attacker = (Player) ebde.getDamager();
                        Item inHand = attacker.getInventory().getItemInHand();
                        if (inHand == null || inHand.getId() == Item.AIR) {
                            switch (Utils.rand(0, 3)) {
                                case 0:
                                    message = "&e" + attacker.getName() + "&4 tore apart &3" + player.getName() + "&4 with their hands";
                                    break;
                                case 1:
                                    message = "&e" + player.getName() + "&4 was punched to death by &3" + attacker.getName();
                                    break;
                                case 2:
                                    message = "&e" + attacker.getName() + "&4" + (attacker.getName().endsWith("s") ? "'" : "'s") + " fists collided fatally with &3" + player.getName();
                                    break;
                            }
                        } else {
                            switch (Utils.rand(0, 4)) {
                                case 0:
                                    message = "&e" + player.getName() + "&4 was slashed into gibs by &3" + attacker.getName() + "&4" + (attacker.getName().endsWith("s") ? "'" : "'s") + " &3" + inHand.getName();
                                    break;
                                case 1:
                                    message = "&e" + attacker.getName() + "&4 killed &3" + player.getName() + "&4 with &3" + inHand.getName();
                                    break;
                                case 2:
                                    message = "&e" + player.getName() + "&4 was gibbed by &3" + attacker.getName() + "&4 using &3" + inHand.getName();
                                    break;
                                case 3:
                                    message = "&e" + player.getName() + "&4 was reduced to a red stain by &3" + attacker.getName() + "&4" + (attacker.getName().endsWith("s") ? "'" : "'s") + " &3" + inHand.getName();
                                    break;
                            }
                        }
                    } else {
                        Entity attacker = ebde.getDamager();
                        String attackerName = attacker.getName();
                        switch (Utils.rand(0, 3)) {
                            case 0:
                                message = "&e" + player.getName() + "&4 was killed by " + (attacker.hasCustomName() ? "&3" + attackerName : "a &3" + attackerName);
                                break;
                            case 1:
                                message = "&e" + player.getName() + "&4 walked too close to " + (attacker.hasCustomName() ? "&3" + attackerName : "a &3" + attackerName);
                                break;
                            case 2:
                                message = "&e" + player.getName() + "&4 came in fatal contact with " + (attacker.hasCustomName() ? "&3" + attackerName : "a &3" + attackerName);
                                break;
                        }
                    }
                }
                break;
            case PROJECTILE:
                if (lastDamageEvent instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent ebde = (EntityDamageByEntityEvent) lastDamageEvent;
                    if (ebde.getDamager() instanceof Player) {
                        Player attacker = (Player) ebde.getDamager();
                        Item inHand = attacker.getInventory().getItemInHand();
                        switch (Utils.rand(0, 4)) {
                            case 0:
                                message = "&e" + player.getName() + "&4 was shot to death by &3" + attacker.getName() + "&4" + (attacker.getName().endsWith("s") ? "'" : "'s") + " &3" + inHand.getName();
                                break;
                            case 1:
                                message = "&e" + attacker.getName() + "&4 shot &3" + player.getName() + "&4 with &3" + inHand.getName();
                                break;
                            case 2:
                                message = "&e" + player.getName() + "&4 was sniped by &3" + attacker.getName() + "&4 using &3" + inHand.getName();
                                break;
                            case 3:
                                message = "&e" + player.getName() + "&4 was impaled by &3" + attacker.getName() + "&4" + (attacker.getName().endsWith("s") ? "'" : "'s") + " arrows";
                                break;

                        }
                    } else {
                        Entity attacker = ebde.getDamager();
                        String attackerName = attacker.getName();
                        switch (Utils.rand(0, 3)) {
                            case 0:
                                message = "&e" + player.getName() + "&4 was killed by " + (attacker.hasCustomName() ? "&3" + attackerName : "a &3" + attackerName);
                                break;
                            case 1:
                                message = "&e" + player.getName() + "&4 came within range of " + (attacker.hasCustomName() ? "&3" + attackerName : "a &3" + attackerName);
                                break;
                            case 2:
                                message = "&e" + player.getName() + "&4 suffered a fatal encounter with " + (attacker.hasCustomName() ? "&3" + attackerName : "a &3" + attackerName);
                                break;
                        }
                    }
                }
                break;
            case ENTITY_EXPLOSION:
                if (lastDamageEvent instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent edbe = (EntityDamageByEntityEvent) lastDamageEvent;
                    Entity attacker = edbe.getDamager();
                    String attackerName = attacker.getName();
                    if (attacker instanceof Player) {
                        switch (Utils.rand(0, 3)) {
                            case 0:
                                message = "&e" + player.getName() + "&4 was blown up by &3" + attackerName;
                                break;
                            case 1:
                                message = "&e" + player.getName() + "&4 was blasted to pieces by &4" + attackerName;
                                break;
                            case 2:
                                message = "&e" + player.getName() + "&4 experienced &4" + attackerName + "&4" + (attackerName.endsWith("s") ? "'" : "'s") + " explosion fatally";
                                break;
                        }
                    } else {
                        switch (Utils.rand(0, 2)) {
                            case 0:
                                message = "&e" + player.getName() + "&4 was blown up by " + (attacker.hasCustomName() ? "&3" + attackerName : "a &3" + attackerName);
                                break;
                            case 1:
                                message = "&e" + player.getName() + "&4 was blasted to pieces by &4" + (attacker.hasCustomName() ? "&3" + attackerName : "a &3" + attackerName);
                                break;
                        }
                    }
                }
                break;
            case MAGIC:
                message = "&e" + player.getName() + "&4 was killed by magic";
                break;
        }
        event.setDeathMessage(TextFormat.colorize(message));
    }

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
