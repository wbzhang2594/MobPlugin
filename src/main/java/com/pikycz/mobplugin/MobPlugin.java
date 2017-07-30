package com.pikycz.mobplugin;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.Listener;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;

import com.pikycz.mobplugin.entities.BaseEntity;
import com.pikycz.mobplugin.entities.monster.walking.Wolf;
import com.pikycz.mobplugin.entities.animal.walking.*;
import com.pikycz.mobplugin.entities.animal.flying.*;
import com.pikycz.mobplugin.entities.block.BlockEntitySpawner;
import com.pikycz.mobplugin.entities.monster.walking.*;
import com.pikycz.mobplugin.entities.monster.flying.*;
import com.pikycz.mobplugin.entities.monster.jumping.*;
import com.pikycz.mobplugin.entities.monster.swim.*;
import com.pikycz.mobplugin.entities.projectile.*;
import com.pikycz.mobplugin.utils.Utils;
import java.util.LinkedHashMap;

public class MobPlugin extends PluginBase implements Listener {

    private ConfigSection entities;
    private static MobPlugin Instance;

    static MobPlugin getInstance() {
        return Instance;
    }

    private int counter = 0;

    @Override
    public void onLoad() {
        registerEntities();
        Utils.logServerInfo("Loading MobPlugin");
        Utils.logServerInfo("Version - 1.1-Dev");
    }

    @Override
    public void onEnable() {
        Instance = this;
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getLogger().info("Prepare" + this.getDataFolder() + "/MobPlugin.yml");
        this.getDataFolder().mkdirs();
        this.loadAll();

    }

    public Config getPluginConfig() {
        return new Config(this.getDataFolder() + "MobPlugin.yml", Config.YAML);
    }

    public void loadAll() {
        this.saveDefaultConfig();
        this.loadEntities();
    }

    @SuppressWarnings("serial")
    public void loadEntities() {
        this.entities = new Config(this.getDataFolder() + "/MobPlugin.yml", Config.YAML,
                new ConfigSection(new LinkedHashMap<String, Object>() {
                    {
                        put("entities.spawn-animals", true);
                        put("entities.spawn-mobs", true);
                    }
                })).getSections();
    }

    @Override
    public void onDisable() {
        Utils.logServerInfo("Plugin disabled successful.");
        this.saveAll();
    }

    public void saveAll() {
        this.saveEntities();
        this.getConfig().save();
    }

    public void saveEntities(boolean async) {
        Config mobplugin = new Config(this.getDataFolder() + "/MobPlugin.yml", Config.YAML);
        mobplugin.setAll(this.entities);
        mobplugin.save(async); 
    }

    public void saveEntities() {
        Config score = new Config(this.getDataFolder() + "/MobPlugin.yml", Config.YAML);
        score.setAll(this.entities);
        score.save();
    }

    private void registerEntities() {
        // register Passive entities
        Entity.registerEntity(Bat.class.getSimpleName(), Bat.class); //Fly too high
        Entity.registerEntity(Chicken.class.getSimpleName(), Chicken.class);
        Entity.registerEntity(Cow.class.getSimpleName(), Cow.class);
        Entity.registerEntity(Donkey.class.getSimpleName(), Donkey.class);
        Entity.registerEntity(Horse.class.getSimpleName(), Horse.class);
        Entity.registerEntity(Mooshroom.class.getSimpleName(), Mooshroom.class);
        Entity.registerEntity(Mule.class.getSimpleName(), Mule.class);
        Entity.registerEntity(Ocelot.class.getSimpleName(), Ocelot.class);
        Entity.registerEntity(Pig.class.getSimpleName(), Pig.class);
        Entity.registerEntity(PolarBear.class.getSimpleName(), PolarBear.class);
        Entity.registerEntity(Rabbit.class.getSimpleName(), Rabbit.class);
        Entity.registerEntity(Sheep.class.getSimpleName(), Sheep.class);
        Entity.registerEntity(SkeletonHorse.class.getSimpleName(), SkeletonHorse.class);
        Entity.registerEntity(Villager.class.getSimpleName(), Villager.class);
        Entity.registerEntity(Wolf.class.getSimpleName(), Wolf.class);
        Entity.registerEntity(ZombieHorse.class.getSimpleName(), ZombieHorse.class);

        //register Monster entities
        Entity.registerEntity(Blaze.class.getSimpleName(), Blaze.class);
        Entity.registerEntity(EnderDragon.class.getSimpleName(), EnderDragon.class); //TODO: Spawn in End
        Entity.registerEntity(Wither.class.getSimpleName(), Wither.class);
        Entity.registerEntity(ElderGuardian.class.getSimpleName(), ElderGuardian.class); //TODO: Spawn in Ocean palace swim , attack
        Entity.registerEntity(Ghast.class.getSimpleName(), Ghast.class); //TODO: Spawn in Nether
        Entity.registerEntity(Guardian.class.getSimpleName(), Guardian.class); //TODO: Spawn in Ocean palace swim , attack
        Entity.registerEntity(CaveSpider.class.getSimpleName(), CaveSpider.class);
        Entity.registerEntity(Creeper.class.getSimpleName(), Creeper.class);
        Entity.registerEntity(Enderman.class.getSimpleName(), Enderman.class); //TODO: Move(teleport) , attack
        Entity.registerEntity(IronGolem.class.getSimpleName(), IronGolem.class);
        Entity.registerEntity(MagmaCube.class.getSimpleName(), MagmaCube.class);//Spawn In Nether
        Entity.registerEntity(PigZombie.class.getSimpleName(), PigZombie.class);//Spawn in Nether
        Entity.registerEntity(Silverfish.class.getSimpleName(), Silverfish.class); //TODO: Spawn random from stone
        Entity.registerEntity(Skeleton.class.getSimpleName(), Skeleton.class);
        Entity.registerEntity(Slime.class.getSimpleName(), Slime.class); //TODO: Make random spawn Slime (Big,Small)
        Entity.registerEntity(SnowGolem.class.getSimpleName(), SnowGolem.class);
        Entity.registerEntity(Spider.class.getSimpleName(), Spider.class);
        Entity.registerEntity(Stray.class.getSimpleName(), Stray.class);
        Entity.registerEntity(Witch.class.getSimpleName(), Witch.class);
        Entity.registerEntity(Husk.class.getSimpleName(), Husk.class);
        Entity.registerEntity(Zombie.class.getSimpleName(), Zombie.class);
        Entity.registerEntity(ZombieVillager.class.getSimpleName(), ZombieVillager.class);

        // register the fireball entity
        Entity.registerEntity("BlueWitherSkull", BlueWitherSkull.class);
        Entity.registerEntity("BlazeFireBall", BlazeFireBall.class);
        Entity.registerEntity("DragonFireBall", DragonFireBall.class);
        Entity.registerEntity("GhastDireBall", GhastFireBall.class);

        // register the mob spawner (which is probably not needed anymore)
        BlockEntity.registerBlockEntity("MobSpawner", BlockEntitySpawner.class);

        Utils.logServerInfo("Register: Entites/Blocks/Items - Done.");
    }

    /**
     * @param commandSender
     * @param cmd
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        if (cmd.getName().toLowerCase().equals("mob")) {

            String output = "";

            if (args.length == 0) {
                commandSender.sendMessage(TextFormat.GOLD + "--MobPlugin 1.1--");
                commandSender.sendMessage(TextFormat.GREEN + "/mob summon <mob>" + TextFormat.YELLOW + "- Spawn Mob");
                commandSender.sendMessage(TextFormat.GREEN + "/mob removemobs" + TextFormat.YELLOW + "- Remove all Mobs");
                commandSender.sendMessage(TextFormat.GREEN + "/mob removeitems" + TextFormat.YELLOW + "- Remove all items on ground");
                commandSender.sendMessage(TextFormat.RED + "/mob version" + TextFormat.YELLOW + "- Show MobPlugin Version");
                commandSender.sendMessage(TextFormat.RED + "/mob info" + TextFormat.YELLOW + "- Show info result");
            } else {
                switch (args[0]) {
                    case "summon":
                        String mob = args[1];
                        Player playerThatSpawns = null;

                        if (args.length == 3) {
                            playerThatSpawns = this.getServer().getPlayer(args[2]);
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
                            output += "Unknown player " + (args.length == 3 ? args[2] : ((Player) commandSender).getName());
                        }
                        break;
                    case "removemobs":
                        int count = 0;
                        for (Level level : getServer().getLevels().values()) {
                            for (Entity entity : level.getEntities()) {
                                if (entity instanceof BaseEntity) {
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
                    case "version":
                        commandSender.sendMessage(TextFormat.GREEN + "Version > 1.1 working with MCPE 1.1");//Todo Automatic Updater?
                        break;
                    case "info":
                        int chunksCollected = 0;
                        int entitiesCollected = 0;
                        int tilesCollected = 0;
                        long memory = Runtime.getRuntime().freeMemory();

                        for (Level level : Server.getInstance().getLevels().values()) {
                            int chunksCount = level.getChunks().size();
                            int entitiesCount = level.getEntities().length;
                            int tilesCount = level.getBlockEntities().size();
                            level.doChunkGarbageCollection();
                            level.unloadChunks(true);
                            chunksCollected += chunksCount - level.getChunks().size();
                            entitiesCollected += entitiesCount - level.getEntities().length;
                            tilesCollected += tilesCount - level.getBlockEntities().size();
                            level.clearCache(true);
                        }

                        System.gc();

                        long freedMemory = Runtime.getRuntime().freeMemory() - memory;
                        commandSender.sendMessage(TextFormat.GREEN + "---- " + TextFormat.WHITE + "Info result (last 5 mins)" + TextFormat.GREEN + " ----");
                        commandSender.sendMessage(TextFormat.GOLD + "Chunks: " + TextFormat.RED + chunksCollected);
                        commandSender.sendMessage(TextFormat.GOLD + "Entities: " + TextFormat.RED + entitiesCollected);
                        commandSender.sendMessage(TextFormat.GOLD + "Block Entities: " + TextFormat.RED + tilesCollected);
                        commandSender.sendMessage(TextFormat.GOLD + "RAM freed: " + TextFormat.RED + NukkitMath.round((freedMemory / 1024d / 1024d), 2) + " MB");
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
     * @param type
     * @param source
     * @param args
     * @return
     */
    public static Entity create(Object type, Position source, Object... args) {
        FullChunk chunk = source.getLevel().getChunk((int) source.x >> 4, (int) source.z >> 4, true);

        if (chunk.getEntities().size() > 10) {
            FileLogger.debug(String.format("Not spawning mob because the chunk already has too many mobs!"));
            return null;
        }

        CompoundTag nbt = new CompoundTag().putList(new ListTag<DoubleTag>("Pos").add(new DoubleTag("", source.x)).add(new DoubleTag("", source.y)).add(new DoubleTag("", source.z)))
                .putList(new ListTag<DoubleTag>("Motion").add(new DoubleTag("", 0)).add(new DoubleTag("", 0)).add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation").add(new FloatTag("", source instanceof Location ? (float) ((Location) source).yaw : 0))
                        .add(new FloatTag("", source instanceof Location ? (float) ((Location) source).pitch : 0)));

        return Entity.createEntity(type.toString(), chunk, nbt, args);
    }

}
