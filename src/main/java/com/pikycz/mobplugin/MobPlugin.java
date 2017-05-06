package com.pikycz.mobplugin;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.event.level.LevelUnloadEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerMouseOverEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.DyeColor;
import com.pikycz.mobplugin.entities.BaseEntity;
import com.pikycz.mobplugin.entities.animal.flying.Bat;
import com.pikycz.mobplugin.entities.animal.walking.*;
import com.pikycz.mobplugin.entities.block.BlockEntitySpawner;
import com.pikycz.mobplugin.entities.monster.flying.Blaze;
import com.pikycz.mobplugin.entities.monster.flying.Ghast;
import com.pikycz.mobplugin.entities.monster.walking.*;
import com.pikycz.mobplugin.entities.projectile.EntityFireBall;
import com.pikycz.mobplugin.entities.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MobPlugin extends PluginBase implements Listener {

    public static boolean MOB_AI_ENABLED = true;

    private int counter = 0;

    private Config pluginConfig = null;
    
    public final HashMap<Integer, Level> levelsToSpawn = new HashMap<>();
    private List<String> disabledWorlds;

    @Override
    public void onLoad() {
        registerEntities();
        Utils.logServerInfo("Plugin loaded successfully.");
    }

    @Override
    public void onEnable() {
        // Config reading and writing
        saveDefaultConfig();
        pluginConfig = getConfig();

        // we need this flag as it's controlled by the plugin's entities
        MOB_AI_ENABLED = pluginConfig.getBoolean("entities.mob-ai", true);
        int spawnDelay = pluginConfig.getInt("entities.auto-spawn-tick", 800);
        disabledWorlds = pluginConfig.getList("worlds-spawn-disabled", new ArrayList());

        for (Level level : getServer().getLevels().values()) {
            if (disabledWorlds.contains(level.getFolderName().toLowerCase())) {
                continue;
            }

            levelsToSpawn.put(level.getId(), level);
        }

        // register as listener to plugin events
        this.getServer().getPluginManager().registerEvents(this, this);

        if (spawnDelay > 0) {
            this.getServer().getScheduler().scheduleRepeatingTask(new AutoSpawnTask(this), spawnDelay, true);
        }

        Utils.logServerInfo(String.format("Plugin enabling successful [aiEnabled:%s] [autoSpawnTick:%d]", MOB_AI_ENABLED, spawnDelay));
    }

    @Override
    public void onDisable() {
        Utils.logServerInfo("Plugin disabled successful.");
    }

    /**
     * @param commandSender
     * @param cmd
     * @param label
     * @param sub
     * @return
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] sub) {
        if(cmd.getName().toLowerCase().equals("mob")){
            
            String output = "";

        if (sub.length == 0) {
            output += "use /mob spawn <mob>";
        } else {
            switch (sub[0]) {
                case "spawn":
                    String mob = sub[1];
                    Player playerThatSpawns = null;

                    if (sub.length == 3) {
                        playerThatSpawns = this.getServer().getPlayer(sub[2]);
                    } else {
                        playerThatSpawns = (Player) commandSender;
                    }

                    if (playerThatSpawns != null) {
                        Position pos = playerThatSpawns.getPosition();

                        Entity ent;
                        if ((ent = MobPlugin.create(mob, pos)) != null) {
                            ent.spawnToAll();
                            output += "Spawned " + mob + " to " + playerThatSpawns.getName();
                        } else {
                            output += "Unable to spawn " + mob;
                        }
                    } else {
                        output += "Unknown player " + (sub.length == 3 ? sub[2] : ((Player) commandSender).getName());
                    }
                    break;
                case "removeall":
                    int count = 0;
                    for (Level level : getServer().getLevels().values()) {
                        for (Entity entity : level.getEntities()) {
                            if (entity instanceof BaseEntity && !entity.closed && entity.isAlive()) {
                                entity.close();
                                count++;
                            }
                        }
                    }
                    output += "Removed " + count + " entities from all levels.";
                    break;
                case "removeitems":
                    count = 0;
                    for (Level level : getServer().getLevels().values()) {
                        for (Entity entity : level.getEntities()) {
                             if (entity instanceof EntityItem && entity.isOnGround()) {
                                entity.close();
                                count++;
                            }
                        }
                    }
                    output += "Removed " + count + " items on ground from all levels.";
                    break;
                default:
                    output += "Unkown command.";
                    break;
            }
        }

        commandSender.sendMessage(output);
        }
        return true;
        
    }

    /**
     * Returns plugin specific yml configuration
     * 
     * 
     * @return a {@link Config} instance
     */
    public Config getPluginConfig() {
        return this.pluginConfig;
    }

    private void registerEntities() {
        // register living entities
        Entity.registerEntity(Bat.class.getSimpleName(), Bat.class);
        Entity.registerEntity(Chicken.class.getSimpleName(), Chicken.class);
        Entity.registerEntity(Cow.class.getSimpleName(), Cow.class);
        Entity.registerEntity(Donkey.class.getSimpleName(), Donkey.class);
        Entity.registerEntity(Horse.class.getSimpleName(), Horse.class);
        Entity.registerEntity(Mooshroom.class.getSimpleName(), Mooshroom.class);
        Entity.registerEntity(Mule.class.getSimpleName(), Mule.class);
        Entity.registerEntity(Ocelot.class.getSimpleName(), Ocelot.class);
        Entity.registerEntity(Pig.class.getSimpleName(), Pig.class);
        Entity.registerEntity(Rabbit.class.getSimpleName(), Rabbit.class);
        Entity.registerEntity(Sheep.class.getSimpleName(), Sheep.class);
        Entity.registerEntity(SkeletonHorse.class.getSimpleName(), SkeletonHorse.class);
        //Entity.registerEntity(Villager.class.getSimpleName(), Villager.class);
        Entity.registerEntity(Wolf.class.getSimpleName(), Wolf.class);
        Entity.registerEntity(ZombieHorse.class.getSimpleName(), ZombieHorse.class);

        Entity.registerEntity(Blaze.class.getSimpleName(), Blaze.class);
        Entity.registerEntity(Ghast.class.getSimpleName(), Ghast.class);
        Entity.registerEntity(CaveSpider.class.getSimpleName(), CaveSpider.class);
        Entity.registerEntity(Creeper.class.getSimpleName(), Creeper.class);
        Entity.registerEntity(Enderman.class.getSimpleName(), Enderman.class);
        Entity.registerEntity(IronGolem.class.getSimpleName(), IronGolem.class);
        Entity.registerEntity(PigZombie.class.getSimpleName(), PigZombie.class);
        Entity.registerEntity(Silverfish.class.getSimpleName(), Silverfish.class);
        Entity.registerEntity(Skeleton.class.getSimpleName(), Skeleton.class);
        Entity.registerEntity(SnowGolem.class.getSimpleName(), SnowGolem.class);
        Entity.registerEntity(Spider.class.getSimpleName(), Spider.class);
        Entity.registerEntity(Witch.class.getSimpleName(), Witch.class);
        Entity.registerEntity(Zombie.class.getSimpleName(), Zombie.class);
        Entity.registerEntity(ZombieVillager.class.getSimpleName(), ZombieVillager.class);
        Entity.registerEntity(Husk.class.getSimpleName(), Husk.class);
        Entity.registerEntity(Stray.class.getSimpleName(), Stray.class);

        // register the fireball entity
        Entity.registerEntity("FireBall", EntityFireBall.class);

        // register the mob spawner (which is probably not needed anymore)
        BlockEntity.registerBlockEntity("MobSpawner", BlockEntitySpawner.class);
        Utils.logServerInfo("registerEntites: done.");
    }

    /**
     * @param type
     * @param source
     * @param args
     * @return
     */
    public static Entity create(Object type, Position source, Object... args) {
        FullChunk chunk = source.getLevel().getChunk((int) source.x >> 4, (int) source.z >> 4, true);
        if (chunk.getEntities().size() > 10) {
            Server.getInstance().getLogger().debug("Not spawning mob because the chunk already has too many mobs!");
            return null;
        }
        if (!chunk.isGenerated()) {
            chunk.setGenerated();
        }
        if (!chunk.isPopulated()) {
            chunk.setPopulated();
        }

        CompoundTag nbt = new CompoundTag().putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", source.x)).add(new DoubleTag("", source.y)).add(new DoubleTag("", source.z)))
                .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", 0)).add(new DoubleTag("", 0)).add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation").add(new FloatTag("", source instanceof Location ? (float) ((Location) source).yaw : 0))
                        .add(new FloatTag("", source instanceof Location ? (float) ((Location) source).pitch : 0)));

        return Entity.createEntity(type.toString(), chunk, nbt, args);
    }

    // --- event listeners ---
    
    /**
     * This event is called when an entity dies. We need this for experience gain.
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
                    if (killExperience > 0 && player != null && player.isSurvival()) {
                        player.addExperience(killExperience);
                        // don't drop that fucking experience orbs because they're somehow buggy :(
                        // if (player.isSurvival()) {
                        //for (int i = 1; i <= killExperience; i++) {
                        // player.getLevel().dropExpOrb(baseEntity, 1);
                        // }
                        // }
                    }
                }
            }
        }
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent ev) {
        if (ev.getFace() == null || ev.getAction() != PlayerInteractEvent.RIGHT_CLICK_BLOCK) {
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

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent ev) {
        if (ev.isCancelled()) {
            return;
        }

        Block block = ev.getBlock();
        if ((block.getId() == Block.STONE || block.getId() == Block.STONE_BRICK || block.getId() == Block.STONE_WALL || block.getId() == Block.STONE_BRICK_STAIRS)
                && block.getLevel().getBlockLightAt((int) block.x, (int) block.y, (int) block.z) < 12 && Utils.rand(1, 5) == 1) {
            // TODO: 돌만 붓시면 되긋나
            /*
             * Silverfish entity = (Silverfish) create("Silverfish", block.add(0.5, 0, 0.5)); if(entity != null){ entity.spawnToAll(); }
             */
        }
    }

    @EventHandler
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
    //
    // @EventHandler
    // public void PlayerMouseRightEntityEvent(PlayerMouseRightEntityEvent ev) {
    // FileLogger.debug(String.format("Received PlayerMouseRightEntityEvent [entity:%s]", ev.getEntity()));
    // }
    
     @EventHandler
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
    @EventHandler
    public void onLevelUnload(LevelUnloadEvent e) {
        Level level = e.getLevel();

        levelsToSpawn.remove(level.getId());
    }
    
}

