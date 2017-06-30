package com.pikycz.mobplugin.entities.animal.walking;

import cn.nukkit.Player;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockWool;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityHumanType;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;
import com.pikycz.mobplugin.ai.EntityAIEatGrass;
import com.pikycz.mobplugin.ai.EntityAISwimming;
import com.pikycz.mobplugin.ai.EntityAIWander;
import com.pikycz.mobplugin.entities.animal.WalkingAnimal;
import com.pikycz.mobplugin.entities.utils.Utils;

import java.util.concurrent.ThreadLocalRandom;

public class Sheep extends WalkingAnimal {

    public static final int NETWORK_ID = 13;

    public boolean sheared = false;
    public int color = 0;
    
    private int sheepTimer;
    private EntityAIEatGrass entityAIEatGrass;

    public Sheep(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.65f;
        }
        return 1.3f;
    }

    @Override
    public float getEyeHeight() {
        if (isBaby()) {
            return 0.65f;
        }
        return 1.1f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(8);
        if (!this.namedTag.contains("Color")) {
            this.setColor(this.randomColor());
        } else {
            this.setColor(this.namedTag.getByte("Color"));
        }

        if (!this.namedTag.contains("Sheared")) {
            this.namedTag.putByte("Sheared", 0);
        } else {
            this.sheared = this.namedTag.getBoolean("Sheared");
        }

        this.setDataFlag(0, 26, this.sheared);
    }
    
    @Override
     protected void initEntityAI() {
        this.entityAIEatGrass = new EntityAIEatGrass(this);
        this.tasks.addTask(0, new EntityAISwimming(this));
        //this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
        //this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
        //this.tasks.addTask(3, new EntityAITempt(this, 1.1D, Items.WHEAT, false));
        //this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(5, this.entityAIEatGrass);
        this.tasks.addTask(6, new EntityAIWander(this, 1.0D));
        //this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        //this.tasks.addTask(8, new EntityAILookIdle(this));
    }

    @Override
    public void updateAITasks() {
        this.sheepTimer = this.entityAIEatGrass.getEatingGrassTimer();
        super.updateAITasks();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putByte("Color", this.color);
        this.namedTag.putBoolean("Sheared", this.sheared);
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        if (creature instanceof Player) {
            Player player = (Player) creature;
            return player.isSurvival() && player.spawned && player.isAlive() && player.getInventory().getItemInHand().getId() == Item.SEEDS && !player.closed && distance <= 49;
        }
        return false;
    }

    @Override
    public boolean onInteract(Entity entity, Item item) {
        if (item.getId() == Item.DYE) {
            this.setColor(((ItemDye) item).getDyeColor().getWoolData());

            if (entity instanceof EntityHumanType) { //TODO: change this in nukkit
                EntityHumanType human = (EntityHumanType) entity;
                item.setCount(item.getCount() - 1);

                if (item.getCount() <= 0) {
                    human.getInventory().setItemInHand(new ItemBlock(new BlockAir()));
                } else {
                    human.getInventory().setItemInHand(item);
                }
            }

            return true;
        }

        if (item.getId() == Item.WHEAT) {

        }

        if (item.getId() != Item.SHEARS) {
            return super.onInteract(entity, item);
        }

        if (shear()) {
            if (entity instanceof EntityHumanType) {
                EntityHumanType human = (EntityHumanType) entity;
                item.setDamage(item.getDamage() + 1);

                if (item.getDamage() >= item.getMaxDurability()) {
                    human.getInventory().setItemInHand(new ItemBlock(new BlockAir()));
                } else {
                    human.getInventory().setItemInHand(item);
                }
            }

            return true;
        }

        return false;
    }

    public boolean shear() {
        if (sheared) {
            return false;
        }
        
        this.setSheared(true);

        this.level.dropItem(this, new ItemBlock(new BlockWool(this.getColor()), 0, this.level.rand.nextInt(2) + 1));
        return true;
    }
    
    public void setSheared(boolean value) {
        this.sheared = value;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHEARED, value);
    }

    @Override
    public Item[] getDrops() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            return new Item[]{Item.get(Item.WOOL, getColor(), 1)};
        }
        return new Item[0];
    }

    public void setColor(int color) {
        this.color = color;
        this.setDataProperty(new ByteEntityData(DATA_COLOUR, color));
    }

    public int getColor() {
        return namedTag.getByte("Color");
    }

    private int randomColor() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double rand = random.nextDouble(1, 100);

        if (rand <= 15) {
            return random.nextBoolean() ? DyeColor.BLACK.getWoolData() : random.nextBoolean() ? DyeColor.GRAY.getWoolData() : DyeColor.LIGHT_GRAY.getWoolData();
        }

        return DyeColor.WHITE.getWoolData();
    }

    @Override
    public int getKillExperience() {
        return Utils.rand(1, 4); // gain 1-3 experience
    }
    
    @Override
    public void eatGrassBonus() {
        this.setSheared(false);

        if (isBaby()) {
            //TODO add age
        }
    }

}
