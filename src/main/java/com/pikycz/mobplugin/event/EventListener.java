package com.pikycz.mobplugin.event;

import cn.nukkit.Player;
//import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
//import cn.nukkit.event.player.PlayerMouseOverEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
//import cn.nukkit.network.protocol.EntityEventPacket;
//import cn.nukkit.utils.DyeColor;
//import com.pikycz.mobplugin.FileLogger;
import com.pikycz.mobplugin.MobPlugin;
import static com.pikycz.mobplugin.MobPlugin.create;
import com.pikycz.mobplugin.entities.BaseEntity;
//import com.pikycz.mobplugin.entities.animal.walking.Wolf;
import com.pikycz.mobplugin.entities.block.BlockEntitySpawner;
//import com.pikycz.mobplugin.entities.monster.walking.Silverfish;
//import com.pikycz.mobplugin.entities.utils.Utils;

public class EventListener implements Listener {

    private int counter = 0;

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent ev) {
        if (ev.getFace() == null || ev.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Item item = ev.getItem();
        Block block = ev.getBlock();
        if (item.getId() == Item.SPAWN_EGG && block.getId() == Item.MONSTER_SPAWNER) {
            ev.setCancelled(true);

            BlockEntity blockEntity = block.getLevel().getBlockEntity(block);
            if (blockEntity != null && blockEntity instanceof BlockEntitySpawner) {
                ((BlockEntitySpawner) blockEntity).setSpawnEntityType(item.getDamage());
            } else {
                if (blockEntity != null) {
                    blockEntity.close();
                }
                CompoundTag nbt = new CompoundTag().putString("id", BlockEntity.MOB_SPAWNER).putInt("EntityId", item.getDamage()).putInt("x", (int) block.x).putInt("y", (int) block.y).putInt("z",
                        (int) block.z);

                BlockEntitySpawner blockEntitySpawner = new BlockEntitySpawner(block.getLevel().getChunk((int) block.x >> 4, (int) block.z >> 4), nbt);
            }
        }
    }

    /**
     * This event is called when an entity dies. We need this for experience
     * gain.
     *
     * @param ev the event that is received
     */
    @EventHandler
    public void EntityDeathEvent(EntityDeathEvent ev) {
        if (ev.getEntity() instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) ev.getEntity();
            if (baseEntity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) baseEntity.getLastDamageCause()).getDamager();
                if (damager instanceof Player) {
                    Player player = (Player) damager;
                    int killExperience = baseEntity.getKillExperience();
                    if (killExperience > 0 && player.isSurvival()) {
                        player.addExperience(killExperience);
                    }
                }
            }
        }
    }

    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent ev) {
        if (ev.isCancelled()) {
            return;
        }

        Block block = ev.getBlock();
        if (block.getId() == Item.JACK_O_LANTERN || block.getId() == Item.PUMPKIN) {
            if (block.down().getId() == Item.SNOW_BLOCK && block.down(2).getId() == Item.SNOW_BLOCK) {
                Entity entity = create("SnowGolem", block.add(0.5, -2, 0.5));
                if (entity != null) {
                    entity.spawnToAll();
                }

                ev.setCancelled();
                block.getLevel().setBlock(block.add(0, -1, 0), new BlockAir());
                block.getLevel().setBlock(block.add(0, -2, 0), new BlockAir());
            } else if (block.down().getId() == Item.IRON_BLOCK && block.down(2).getId() == Item.IRON_BLOCK) {
                block = block.down();

                Block first, second = null;
                if ((first = block.east()).getId() == Item.IRON_BLOCK && (second = block.west()).getId() == Item.IRON_BLOCK) {
                    block.getLevel().setBlock(first, new BlockAir());
                    block.getLevel().setBlock(second, new BlockAir());
                } else if ((first = block.north()).getId() == Item.IRON_BLOCK && (second = block.south()).getId() == Item.IRON_BLOCK) {
                    block.getLevel().setBlock(first, new BlockAir());
                    block.getLevel().setBlock(second, new BlockAir());
                }

                if (second != null) {
                    Entity entity = MobPlugin.create("IronGolem", block.add(0.5, -1, 0.5));
                    if (entity != null) {
                        entity.spawnToAll();
                    }
                    block.getLevel().setBlock(block, new BlockAir());
                    block.getLevel().setBlock(block.add(0, -1, 0), new BlockAir());
                    ev.setCancelled();
                }
            }
        }
    }

    /*@EventHandler //TODO: Taming
    @SuppressWarnings("null")
    public void PlayerMouseOverEntityEvent(PlayerMouseOverEntityEvent ev) {
        if (this.counter > 10) {
            counter = 0;
            FileLogger.debug(String.format("Received PlayerMouseOverEntityEvent [entity:%s]", ev.getEntity()));
            // wolves can be tamed using bones
            if (ev != null && ev.getEntity() != null && ev.getPlayer() != null && ev.getEntity().getNetworkId() == Wolf.NETWORK_ID && ev.getPlayer().getInventory().getItemInHand().getId() == Item.BONE) {
                // check if already owned and tamed ...
                Wolf wolf = (Wolf) ev.getEntity();
                if (!wolf.isAngry() && wolf.getOwner() == null) {
                    // now try it out ...
                    EntityEventPacket packet = new EntityEventPacket();
                    packet.eid = ev.getEntity().getId();
                    packet.event = EntityEventPacket.TAME_SUCCESS;

                    Server.broadcastPacket(new Player[]{ev.getPlayer()}, packet);

                    // set the owner
                    //wolf.setOwner(ev.getPlayer());
                    wolf.setCollarColor(DyeColor.BLUE);
                    wolf.saveNBT();
                }
            }
        } else {
            counter++;
        }
    }

    @EventHandler
    public void PlayerMouseRightEntityEvent(PlayerMouseRightEntityEvent ev) {
       FileLogger.debug(String.format("Received PlayerMouseRightEntityEvent [entity:%s]", ev.getEntity()));
    }*/
}
