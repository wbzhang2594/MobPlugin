package com.pikycz.mobplugin.entities.monster;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import com.pikycz.mobplugin.entities.SwimmingEntity;

/**
 *
 * @author PikyCZ
 */
public abstract class SwimmingMonster extends SwimmingEntity implements Monster {

    public SwimmingMonster(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getKillExperience() {
        return 0;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void attackEntity(Entity player) {
        //TODO
    }

    @Override
    public int getDamage() {
        return 0;
    }

    @Override
    public int getDamage(Integer difficulty) {
        return 0;
    }

    @Override
    public int getMinDamage() {
        return 0;
    }

    @Override
    public int getMinDamage(Integer difficulty) {
        return 0;
    }

    @Override
    public int getMaxDamage() {
        return 0;
    }

    @Override
    public int getMaxDamage(Integer difficulty) {
        return 0;
    }

    @Override
    public void setDamage(int damage) {
        //TODO
    }

    @Override
    public void setDamage(int[] damage) {
        //TODO
    }

    @Override
    public void setDamage(int damage, int difficulty) {
        //TODO
    }

    @Override
    public void setMinDamage(int damage) {
        //TODO
    }

    @Override
    public void setMinDamage(int[] damage) {
        //TODO
    }

    @Override
    public void setMinDamage(int damage, int difficulty) {
        //TODO
    }

    @Override
    public void setMaxDamage(int damage) {
        //TODO
    }

    @Override
    public void setMaxDamage(int[] damage) {
        //TODO
    }

    @Override
    public void setMaxDamage(int damage, int difficulty) {
        //TODO
    }

}
